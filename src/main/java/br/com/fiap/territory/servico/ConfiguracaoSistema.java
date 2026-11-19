package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Limiar;
import br.com.fiap.territory.dominio.SensorChuva;
import br.com.fiap.territory.dominio.SensorNivelRio;
import br.com.fiap.territory.dominio.SensorUmidadeSolo;
import br.com.fiap.territory.excecao.DadosInvalidosException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfiguracaoSistema {

    public static final String PREVISAO_MEDIA_MOVEL = "MEDIA_MOVEL";
    public static final String PREVISAO_COMPARACAO = "COMPARACAO";

    private static ConfiguracaoSistema instancia;

    private final int janelaMediaMovel = 5;
    private final int janelaComparacao = 3;
    private final int validadeLeiturasMinutos = 60;
    private final List<String> estrategiasAtivas = new ArrayList<>();
    private final Map<String, Limiar> limiares = new HashMap<>();

    private ConfiguracaoSistema() {
        estrategiasAtivas.add(PREVISAO_MEDIA_MOVEL);
        estrategiasAtivas.add(PREVISAO_COMPARACAO);
        restaurarLimiaresPadrao();
    }

    public static synchronized ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    public synchronized void restaurarLimiaresPadrao() {
        limiares.put(SensorChuva.TIPO, new Limiar(10.0, 25.0, 50.0));
        limiares.put(SensorNivelRio.TIPO, new Limiar(2.0, 3.0, 4.0));
        limiares.put(SensorUmidadeSolo.TIPO, new Limiar(75.0, 85.0, 95.0));
    }

    public synchronized void definirLimiar(String tipoSensor, Limiar novoLimiar, double valorMaximoSensor) {
        getLimiar(tipoSensor);
        if (novoLimiar == null) {
            throw new DadosInvalidosException("Informe o novo limiar.");
        }
        novoLimiar.validarParaSensor(valorMaximoSensor);
        limiares.put(tipoSensor, novoLimiar);
    }

    public synchronized Limiar getLimiar(String tipoSensor) {
        Limiar limiar = limiares.get(tipoSensor);
        if (limiar == null) {
            throw new DadosInvalidosException(String.format("Não há limiares configurados para o tipo de sensor %s.", tipoSensor));
        }
        return limiar;
    }

    public List<String> getEstrategiasAtivas() {
        return new ArrayList<>(estrategiasAtivas);
    }

    public int getJanelaMediaMovel() {
        return janelaMediaMovel;
    }

    public int getJanelaComparacao() {
        return janelaComparacao;
    }

    public int getValidadeLeiturasMinutos() {
        return validadeLeiturasMinutos;
    }
}
