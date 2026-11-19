package br.com.fiap.territory;

import br.com.fiap.territory.dominio.Alerta;
import br.com.fiap.territory.dominio.DadosTerritorio;
import br.com.fiap.territory.dominio.Limiar;
import br.com.fiap.territory.dominio.Medicao;
import br.com.fiap.territory.dominio.NivelAlerta;
import br.com.fiap.territory.dominio.PontoMonitoramento;
import br.com.fiap.territory.dominio.Sensor;
import br.com.fiap.territory.dominio.SensorChuva;
import br.com.fiap.territory.dominio.SensorNivelRio;
import br.com.fiap.territory.dominio.SensorUmidadeSolo;
import br.com.fiap.territory.excecao.AcessoNegadoException;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;
import br.com.fiap.territory.servico.ConfiguracaoSistema;
import br.com.fiap.territory.servico.ServicoMonitoramento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class SistemaMonitoramento implements CommandLineRunner {

    private static final int MAXIMO_CICLOS = 50;
    private static final String LINHA = "=====================================================";

    @Autowired
    private ServicoMonitoramento servico;

    @Value("${territorio.codigo-acesso}")
    private String codigoAcesso;

    private final Scanner scanner = new Scanner(System.in);
    private DadosTerritorio territorio;
    private List<PontoMonitoramento> pontos;

    public static void main(String[] args) {
        ConfigurableApplicationContext contexto = SpringApplication.run(SistemaMonitoramento.class, args);
        contexto.close();
    }

    @Override
    public void run(String... args) {
        territorio = new DadosTerritorio("Bacia do Córrego Cabuçu de Baixo", "Zona Norte de São Paulo", 95000,
                codigoAcesso);
        pontos = montarPontos();
        executar();
    }

    private static List<PontoMonitoramento> montarPontos() {
        List<Sensor> sensoresMargem = new ArrayList<>();
        sensoresMargem.add(new SensorChuva("CHV-01"));
        sensoresMargem.add(new SensorNivelRio("NIV-01"));
        sensoresMargem.add(new SensorUmidadeSolo("UMI-01"));

        List<Sensor> sensoresVarzea = new ArrayList<>();
        sensoresVarzea.add(new SensorChuva("CHV-02"));
        sensoresVarzea.add(new SensorNivelRio("NIV-02"));

        List<PontoMonitoramento> pontos = new ArrayList<>();
        pontos.add(new PontoMonitoramento("PM-01", "Margem do córrego - trecho baixo", "Brasilândia", sensoresMargem));
        pontos.add(new PontoMonitoramento("PM-02", "Área de várzea junto à escola municipal", "Freguesia do Ó", sensoresVarzea));
        return pontos;
    }

    private void executar() {
        System.out.printf("%n%s%n   TERRITORY PREDICTIVE INSIGHTS - Chuva e Alagamento%n   %s | %s%n%s%n",
                LINHA, territorio.getNome(), territorio.getRegiao(), LINHA);

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");
            try {
                executarOpcao(opcao);
            } catch (DadosInvalidosException e) {
                System.out.printf("%n[DADOS INVÁLIDOS] %s%n", e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.out.printf("%n[FONTE INDISPONÍVEL] %s%n", e.getMessage());
            } catch (AcessoNegadoException e) {
                System.out.printf("%n[ACESSO NEGADO] %s%n", e.getMessage());
            } catch (RuntimeException e) {
                System.out.printf("%n[ERRO INESPERADO] A operação foi cancelada: %s%n", e.getMessage());
                System.out.printf("Os dados que já estavam gravados no banco continuam preservados.%n");
            }
        } while (opcao != 0);

        System.out.printf("%nSistema encerrado. O histórico continua salvo no banco de dados.%n");
    }

    private void exibirMenu() {
        System.out.printf("%n================ MENU PRINCIPAL ================%n");
        System.out.printf("1 - Verificar sensores (coletar e gravar leituras)%n");
        System.out.printf("2 - Rodar previsões e alertas%n");
        System.out.printf("3 - Ver histórico (medições e alertas)%n");
        System.out.printf("4 - Gerenciar dados do território%n");
        System.out.printf("5 - Status completo%n");
        System.out.printf("0 - Sair%n");
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                verificarSensores();
                break;
            case 2:
                rodarPrevisoes();
                break;
            case 3:
                verHistorico();
                break;
            case 4:
                gerenciarTerritorio();
                break;
            case 5:
                exibirStatusCompleto();
                break;
            case 0:
                break;
            default:
                System.out.printf("Opção inválida: %d. Escolha um número do menu.%n", opcao);
        }
    }

    private void verificarSensores() {
        int ciclos = lerInteiro(String.format("Quantos ciclos de leitura deseja executar (1 a %d)? ", MAXIMO_CICLOS));
        if (ciclos < 1 || ciclos > MAXIMO_CICLOS) {
            throw new DadosInvalidosException(String.format("Quantidade de ciclos inválida: %d (use de 1 a %d).",
                    ciclos, MAXIMO_CICLOS));
        }

        int gravadas = 0;
        int descartadas = 0;
        int falhas = 0;
        for (int ciclo = 1; ciclo <= ciclos; ciclo++) {
            System.out.printf("%n--- Ciclo %d de %d ---%n", ciclo, ciclos);
            for (PontoMonitoramento ponto : pontos) {
                ponto.verificarStatus();
                System.out.printf("%s%n", ponto.descrever());
                for (Sensor sensor : ponto.getSensores()) {
                    try {
                        Medicao medicao = servico.registrarLeitura(ponto, sensor);
                        System.out.printf("   [GRAVADA] %s%n", medicao.descrever());
                        gravadas++;
                    } catch (DadosInvalidosException e) {
                        System.out.printf("   [DESCARTADA] %s%n", e.getMessage());
                        descartadas++;
                    } catch (FonteIndisponivelException e) {
                        System.out.printf("   [FALHA] %s%n", e.getMessage());
                        falhas++;
                    }
                }
            }
        }
        System.out.printf("%nResumo: %d leitura(s) gravada(s), %d descartada(s) por valor inválido e %d falha(s) de sensor.%n",
                gravadas, descartadas, falhas);
    }

    private void rodarPrevisoes() {
        int alertasAntes = servico.listarAlertasEmitidos().size();
        for (PontoMonitoramento ponto : pontos) {
            System.out.printf("%n### %s - %s (%s)%n", ponto.getId(), ponto.getNome(), ponto.getRegiao());
            for (Sensor sensor : ponto.getSensores()) {
                System.out.printf("%n%s - %s%n", sensor.getCodigo(), sensor.getTipo());
                System.out.printf("%s%n", servico.analisarSensor(ponto, sensor));
            }
        }
        int novosAlertas = servico.listarAlertasEmitidos().size() - alertasAntes;
        System.out.printf("%nNovos alertas gravados nesta rodada: %d%n", novosAlertas);
        System.out.printf("Os alertas apoiam a decisão da equipe comunitária: confira a situação no local antes de agir.%n");
    }

    private void verHistorico() {
        int opcao;
        do {
            System.out.printf("%n--- HISTÓRICO ---%n1 - Medições por sensor%n2 - Alertas já emitidos%n0 - Voltar%n");
            opcao = lerInteiro("Opção: ");
            try {
                if (opcao == 1) {
                    listarMedicoesPorSensor();
                } else if (opcao == 2) {
                    listarAlertas();
                } else if (opcao != 0) {
                    System.out.printf("Opção inválida: %d.%n", opcao);
                }
            } catch (DadosInvalidosException e) {
                System.out.printf("[DADOS INVÁLIDOS] %s%n", e.getMessage());
            }
        } while (opcao != 0);
    }

    private void listarMedicoesPorSensor() {
        listarSensores();
        String codigo = lerTexto("Código do sensor: ");
        List<Medicao> historico = servico.buscarHistoricoPorSensor(codigo);
        if (historico.isEmpty()) {
            System.out.printf("Nenhuma medição gravada para o sensor %s.%n", codigo.toUpperCase());
            return;
        }
        for (Medicao medicao : historico) {
            System.out.printf("   %s%n", medicao.descrever());
        }
        System.out.printf("Total: %d medição(ões).%n", historico.size());

        String resposta = lerTexto("Exportar este histórico para CSV? (s/n): ");
        if (resposta.equalsIgnoreCase("s")) {
            try {
                String caminho = servico.exportarHistoricoCsv(codigo, "exportacoes");
                System.out.printf("CSV gerado em: %s%n", caminho);
            } catch (IOException e) {
                System.out.printf("[ERRO DE ARQUIVO] Não foi possível exportar o CSV: %s%n", e.getMessage());
            }
        }
    }

    private void listarAlertas() {
        List<Alerta> alertas = servico.listarAlertasEmitidos();
        if (alertas.isEmpty()) {
            System.out.printf("Nenhum alerta emitido até agora.%n");
            return;
        }
        for (Alerta alerta : alertas) {
            System.out.printf("%s%n", alerta.descrever());
        }
        System.out.printf("Total: %d alerta(s).%n", alertas.size());
    }

    private void gerenciarTerritorio() {
        int opcao;
        do {
            System.out.printf("%n--- DADOS DO TERRITÓRIO ---%n");
            System.out.printf("1 - Ver dados do território e limiares%n");
            System.out.printf("2 - Alterar população estimada%n");
            System.out.printf("3 - Alterar limiares de um tipo de sensor (exige código de acesso)%n");
            System.out.printf("4 - Colocar sensor em manutenção ou reativar%n");
            System.out.printf("5 - Restaurar limiares padrão (exige código de acesso)%n");
            System.out.printf("0 - Voltar%n");
            opcao = lerInteiro("Opção: ");
            try {
                executarOpcaoTerritorio(opcao);
            } catch (DadosInvalidosException e) {
                System.out.printf("[DADOS INVÁLIDOS] %s%n", e.getMessage());
            } catch (AcessoNegadoException e) {
                System.out.printf("[ACESSO NEGADO] %s%n", e.getMessage());
            }
        } while (opcao != 0);
    }

    private void executarOpcaoTerritorio(int opcao) {
        if (opcao == 1) {
            System.out.printf("%s%n", territorio.descrever());
            exibirLimiares();
        } else if (opcao == 2) {
            territorio.setPopulacaoEstimada(lerInteiro("Nova população estimada na área de risco: "));
            System.out.printf("População atualizada para %d pessoas.%n", territorio.getPopulacaoEstimada());
        } else if (opcao == 3) {
            alterarLimiares();
        } else if (opcao == 4) {
            alterarFuncionamentoSensor();
        } else if (opcao == 5) {
            servico.restaurarLimiaresPadrao(territorio, lerTexto("Código de acesso: "));
            System.out.printf("Limiares padrão restaurados.%n");
        } else if (opcao != 0) {
            System.out.printf("Opção inválida: %d.%n", opcao);
        }
    }

    private void alterarLimiares() {
        listarSensores();
        Sensor sensor = buscarSensor(lerTexto("Código de um sensor do tipo que deseja ajustar: "));
        String codigoAcesso = lerTexto("Código de acesso: ");
        double atencao = lerDecimal(String.format("Novo limite de ATENÇÃO (%s): ", sensor.getUnidade()));
        double alerta = lerDecimal(String.format("Novo limite de ALERTA (%s): ", sensor.getUnidade()));
        double critico = lerDecimal(String.format("Novo limite de CRÍTICO (%s): ", sensor.getUnidade()));
        Limiar novoLimiar = new Limiar(atencao, alerta, critico);
        servico.alterarLimiares(territorio, codigoAcesso, sensor, novoLimiar);
        System.out.printf("Limiares de %s atualizados.%n", sensor.getTipo());
    }

    private void alterarFuncionamentoSensor() {
        listarSensores();
        Sensor sensor = buscarSensor(lerTexto("Código do sensor: "));
        boolean emManutencao = lerTexto("Colocar em manutenção (m) ou reativar (r)? ").equalsIgnoreCase("m");
        sensor.definirAtivo(!emManutencao);
        if (emManutencao) {
            System.out.printf("Sensor %s colocado em manutenção.%n", sensor.getCodigo());
        } else {
            System.out.printf("Sensor %s reativado.%n", sensor.getCodigo());
        }
    }

    private void exibirStatusCompleto() {
        System.out.printf("%n================ STATUS COMPLETO ================%n%s%n", territorio.descrever());

        System.out.printf("%n--- Componentes do território ---%n");
        for (PontoMonitoramento ponto : pontos) {
            ponto.verificarStatus();
            System.out.printf("%s%n", ponto.descrever());
        }

        List<Alerta> alertas = servico.listarAlertasEmitidos();
        System.out.printf("%n--- Histórico no banco ---%n");
        System.out.printf("Medições gravadas: %d%n", servico.contarMedicoes());
        System.out.printf("Alertas emitidos: %d", alertas.size());
        String[] niveis = {NivelAlerta.ATENCAO, NivelAlerta.ALERTA, NivelAlerta.CRITICO};
        for (String nivel : niveis) {
            int quantidade = 0;
            for (Alerta alerta : alertas) {
                if (alerta.getNivel().equals(nivel)) {
                    quantidade++;
                }
            }
            System.out.printf(" | %s: %d", nivel, quantidade);
        }
        System.out.printf("%n");
        if (!alertas.isEmpty()) {
            System.out.printf("Alerta mais recente: %s%n", alertas.get(0).descrever());
        }

        System.out.printf("%n");
        exibirLimiares();
        System.out.printf("=================================================%n");
    }

    private void exibirLimiares() {
        ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstancia();
        List<String> tiposExibidos = new ArrayList<>();
        System.out.printf("--- Limiares vigentes (ATENÇÃO / ALERTA / CRÍTICO) ---%n");
        for (PontoMonitoramento ponto : pontos) {
            for (Sensor sensor : ponto.getSensores()) {
                if (!tiposExibidos.contains(sensor.getTipo())) {
                    tiposExibidos.add(sensor.getTipo());
                    Limiar limiar = configuracao.getLimiar(sensor.getTipo());
                    System.out.printf("%s: %.2f / %.2f / %.2f %s%n", sensor.getTipo(), limiar.getAtencao(),
                            limiar.getAlerta(), limiar.getCritico(), sensor.getUnidade());
                }
            }
        }
    }

    private void listarSensores() {
        System.out.printf("Sensores cadastrados:%n");
        for (PontoMonitoramento ponto : pontos) {
            for (Sensor sensor : ponto.getSensores()) {
                String situacao = "ativo";
                if (!sensor.verificarFuncionamento()) {
                    situacao = "em manutenção";
                }
                System.out.printf("   %s - %s (%s, %s)%n", sensor.getCodigo(), sensor.getTipo(), ponto.getId(), situacao);
            }
        }
    }

    private Sensor buscarSensor(String codigo) {
        for (PontoMonitoramento ponto : pontos) {
            for (Sensor sensor : ponto.getSensores()) {
                if (sensor.getCodigo().equalsIgnoreCase(codigo)) {
                    return sensor;
                }
            }
        }
        throw new DadosInvalidosException(String.format("Sensor não encontrado: %s.", codigo));
    }

    private String lerTexto(String mensagem) {
        System.out.printf("%s", mensagem);
        if (!scanner.hasNextLine()) {
            return "0";
        }
        return scanner.nextLine().trim();
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.printf("Valor inválido: \"%s\" não é um número inteiro. Tente novamente.%n", texto);
            }
        }
    }

    private double lerDecimal(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem).replace(',', '.');
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                System.out.printf("Valor inválido: \"%s\" não é um número. Use, por exemplo, 12.5 ou 12,5.%n", texto);
            }
        }
    }
}
