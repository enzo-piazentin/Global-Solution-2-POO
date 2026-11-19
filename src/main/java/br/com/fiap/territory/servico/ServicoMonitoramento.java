package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Alerta;
import br.com.fiap.territory.dominio.AlertaBuilder;
import br.com.fiap.territory.dominio.DadosTerritorio;
import br.com.fiap.territory.dominio.EstrategiaPrevisao;
import br.com.fiap.territory.dominio.Limiar;
import br.com.fiap.territory.dominio.Medicao;
import br.com.fiap.territory.dominio.MedicaoBuilder;
import br.com.fiap.territory.dominio.NivelAlerta;
import br.com.fiap.territory.dominio.PontoMonitoramento;
import br.com.fiap.territory.dominio.Sensor;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;
import br.com.fiap.territory.persistencia.AlertaRepository;
import br.com.fiap.territory.persistencia.MedicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ServicoMonitoramento {

    private static final Locale PORTUGUES_BRASIL = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter FORMATO_DATA_CSV = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_NOME_ARQUIVO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Autowired
    private MedicaoRepository medicaoRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private FabricaPrevisao fabricaPrevisao;

    public Medicao registrarLeitura(PontoMonitoramento ponto, Sensor sensor) {
        validarSensorDoPonto(ponto, sensor);
        if (!sensor.verificarFuncionamento()) {
            throw new FonteIndisponivelException(String.format("Sensor %s está em manutenção e não pode ser lido.",
                    sensor.getCodigo()));
        }
        double valor = sensor.lerValor();
        if (valor < 0 || valor > sensor.getValorMaximo()) {
            throw new DadosInvalidosException(String.format(
                    "Leitura fora da faixa no sensor %s: %.2f %s (aceito de 0 a %.2f %s).",
                    sensor.getCodigo(), valor, sensor.getUnidade(), sensor.getValorMaximo(), sensor.getUnidade()));
        }
        Medicao medicao = MedicaoBuilder.builder()
                .comSensor(sensor.getCodigo(), sensor.getTipo())
                .comRegiao(ponto.getRegiao())
                .comValor(valor, sensor.getUnidade())
                .build();
        medicaoRepository.save(medicao);
        return medicao;
    }

    public List<Medicao> buscarHistoricoPorSensor(String sensorCodigo) {
        if (sensorCodigo == null || sensorCodigo.isBlank()) {
            throw new DadosInvalidosException("Informe o código do sensor para consultar o histórico.");
        }
        return medicaoRepository.findBySensorCodigoOrderByIdAsc(sensorCodigo.trim().toUpperCase());
    }

    public List<Alerta> listarAlertasEmitidos() {
        return alertaRepository.findAllByOrderByIdDesc();
    }

    public long contarMedicoes() {
        return medicaoRepository.count();
    }

    public String analisarSensor(PontoMonitoramento ponto, Sensor sensor) {
        validarSensorDoPonto(ponto, sensor);
        ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstancia();
        List<EstrategiaPrevisao> estrategias = fabricaPrevisao.criarEstrategiasConfiguradas();
        int validade = configuracao.getValidadeLeiturasMinutos();
        LocalDateTime desde = LocalDateTime.now().minusMinutes(validade);
        List<Medicao> recentes = buscarUltimasLeituras(sensor.getCodigo(), desde, calcularLeiturasNecessarias(estrategias));

        Limiar limiar = configuracao.getLimiar(sensor.getTipo());
        StringBuilder relatorio = new StringBuilder();
        List<Alerta> candidatos = new ArrayList<>();
        int previsoesConfiaveis = 0;

        for (EstrategiaPrevisao estrategia : estrategias) {
            if (recentes.size() < estrategia.getMinimoLeituras()) {
                relatorio.append(String.format("   [%s] SEM PREVISÃO: há %d leitura(s) dos últimos %d min e o mínimo confiável é %d.%n",
                        estrategia.getNome(), recentes.size(), validade, estrategia.getMinimoLeituras()));
            } else {
                previsoesConfiaveis++;
                double previsto = estrategia.prever(recentes);
                relatorio.append(String.format("   %s%n", estrategia.explicarResultado(previsto, sensor.getUnidade())));
                String nivel = limiar.classificar(previsto);
                if (NivelAlerta.geraAlerta(nivel)) {
                    candidatos.add(AlertaBuilder.builder()
                            .comSensor(sensor.getCodigo(), ponto.getRegiao())
                            .comNivel(nivel)
                            .comPrevisao(previsto, limiar.limiteDoNivel(nivel), sensor.getUnidade())
                            .comEstrategia(estrategia.getNome())
                            .build());
                }
            }
        }

        if (previsoesConfiaveis == 0) {
            relatorio.append("   --- O sistema se calou: sem leituras recentes suficientes, nenhum alerta foi emitido.");
            return relatorio.toString();
        }
        if (candidatos.isEmpty()) {
            relatorio.append(String.format("   --- NORMAL: previsões abaixo do limite de ATENÇÃO (%.2f %s).",
                    limiar.getAtencao(), sensor.getUnidade()));
            return relatorio.toString();
        }

        Alerta maisGrave = candidatos.get(0);
        for (Alerta candidato : candidatos) {
            if (NivelAlerta.gravidade(candidato.getNivel()) > NivelAlerta.gravidade(maisGrave.getNivel())) {
                maisGrave = candidato;
            }
        }

        if (alertaRepository.existsBySensorCodigoAndNivelAndDataHoraGreaterThanEqual(sensor.getCodigo(), maisGrave.getNivel(), desde)) {
            relatorio.append(String.format("   --- Alerta %s já emitido para este sensor nos últimos %d min: nada novo foi gravado.",
                    maisGrave.getNivel(), validade));
        } else {
            alertaRepository.save(maisGrave);
            relatorio.append(String.format("   >>> NOVO ALERTA GRAVADO %s", maisGrave.descrever()));
        }
        return relatorio.toString();
    }

    public void alterarLimiares(DadosTerritorio territorio, String codigoAcesso, Sensor sensorReferencia, Limiar novoLimiar) {
        if (territorio == null || sensorReferencia == null) {
            throw new DadosInvalidosException("Informe o território e o tipo de sensor para alterar os limiares.");
        }
        territorio.autorizarAlteracaoDeLimiares(codigoAcesso);
        ConfiguracaoSistema.getInstancia().definirLimiar(sensorReferencia.getTipo(), novoLimiar, sensorReferencia.getValorMaximo());
    }

    public void restaurarLimiaresPadrao(DadosTerritorio territorio, String codigoAcesso) {
        if (territorio == null) {
            throw new DadosInvalidosException("Informe o território para restaurar os limiares.");
        }
        territorio.autorizarAlteracaoDeLimiares(codigoAcesso);
        ConfiguracaoSistema.getInstancia().restaurarLimiaresPadrao();
    }

    public String exportarHistoricoCsv(String sensorCodigo, String pastaDestino) throws IOException {
        List<Medicao> historico = buscarHistoricoPorSensor(sensorCodigo);
        if (historico.isEmpty()) {
            throw new DadosInvalidosException(String.format("Não há medições do sensor %s para exportar.", sensorCodigo));
        }

        File pasta = new File(pastaDestino);
        if (!pasta.exists() && !pasta.mkdirs()) {
            throw new IOException(String.format("Não foi possível criar a pasta %s.", pasta.getAbsolutePath()));
        }
        String nomeArquivo = String.format("historico_%s_%s.csv", sensorCodigo.trim().toUpperCase(),
                LocalDateTime.now().format(FORMATO_NOME_ARQUIVO));
        File arquivo = new File(pasta, nomeArquivo);

        try (PrintWriter escritor = new PrintWriter(new FileWriter(arquivo, StandardCharsets.UTF_8))) {
            escritor.print("﻿");
            escritor.println("id;sensor;tipo;regiao;valor;unidade;data_hora");
            for (Medicao medicao : historico) {
                escritor.println(String.format(PORTUGUES_BRASIL, "%d;%s;%s;%s;%.2f;%s;%s",
                        medicao.getId(), medicao.getSensorCodigo(), medicao.getTipoSensor(), medicao.getRegiao(),
                        medicao.getValor(), medicao.getUnidade(), medicao.getDataHora().format(FORMATO_DATA_CSV)));
            }
            if (escritor.checkError()) {
                throw new IOException(String.format("Falha ao gravar o arquivo %s.", arquivo.getAbsolutePath()));
            }
        }
        return arquivo.getAbsolutePath();
    }

    private List<Medicao> buscarUltimasLeituras(String sensorCodigo, LocalDateTime desde, int quantidade) {
        List<Medicao> recentes = medicaoRepository.findBySensorCodigoAndDataHoraGreaterThanEqualOrderByIdAsc(sensorCodigo, desde);
        int inicio = recentes.size() - quantidade;
        if (inicio < 0) {
            inicio = 0;
        }
        List<Medicao> ultimas = new ArrayList<>();
        for (int i = inicio; i < recentes.size(); i++) {
            ultimas.add(recentes.get(i));
        }
        return ultimas;
    }

    private int calcularLeiturasNecessarias(List<EstrategiaPrevisao> estrategias) {
        int necessarias = 1;
        for (EstrategiaPrevisao estrategia : estrategias) {
            if (estrategia.getMinimoLeituras() > necessarias) {
                necessarias = estrategia.getMinimoLeituras();
            }
        }
        return necessarias;
    }

    private void validarSensorDoPonto(PontoMonitoramento ponto, Sensor sensor) {
        if (ponto == null || sensor == null) {
            throw new DadosInvalidosException("Informe o ponto de monitoramento e o sensor da operação.");
        }
        if (!ponto.possuiSensor(sensor.getCodigo())) {
            throw new DadosInvalidosException(String.format("O sensor %s não está instalado no ponto %s.",
                    sensor.getCodigo(), ponto.getId()));
        }
    }
}
