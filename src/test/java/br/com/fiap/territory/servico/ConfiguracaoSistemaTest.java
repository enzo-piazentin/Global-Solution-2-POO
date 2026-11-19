package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Limiar;
import br.com.fiap.territory.dominio.SensorChuva;
import br.com.fiap.territory.dominio.SensorNivelRio;
import br.com.fiap.territory.dominio.SensorUmidadeSolo;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfiguracaoSistemaTest {

    @BeforeEach
    public void setUp() {
        ConfiguracaoSistema.getInstancia().restaurarLimiaresPadrao();
    }

    @AfterEach
    public void tearDown() {
        ConfiguracaoSistema.getInstancia().restaurarLimiaresPadrao();
    }

    @Test
    public void deveRetornarMesmaInstancia() {
        ConfiguracaoSistema instancia1 = ConfiguracaoSistema.getInstancia();
        ConfiguracaoSistema instancia2 = ConfiguracaoSistema.getInstancia();

        assertEquals(instancia1, instancia2);
    }

    @Test
    public void deveRetornarJanelaMediaMovel() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        assertEquals(5, config.getJanelaMediaMovel());
    }

    @Test
    public void deveRetornarJanelaComparacao() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        assertEquals(3, config.getJanelaComparacao());
    }

    @Test
    public void deveRetornarValidadeLeiturasMinutos() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        assertEquals(60, config.getValidadeLeiturasMinutos());
    }

    @Test
    public void deveRetornarEstrategiasAtivas() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        List<String> estrategias = config.getEstrategiasAtivas();

        assertEquals(2, estrategias.size());
        assertTrue(estrategias.contains(ConfiguracaoSistema.PREVISAO_MEDIA_MOVEL));
        assertTrue(estrategias.contains(ConfiguracaoSistema.PREVISAO_COMPARACAO));
    }

    @Test
    public void deveRetornarLimiarParaSensorChuva() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        Limiar limiar = config.getLimiar(SensorChuva.TIPO);

        assertEquals(10.0, limiar.getAtencao(), 0.001);
        assertEquals(25.0, limiar.getAlerta(), 0.001);
        assertEquals(50.0, limiar.getCritico(), 0.001);
    }

    @Test
    public void deveRetornarLimiarParaSensorNivelRio() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        Limiar limiar = config.getLimiar(SensorNivelRio.TIPO);

        assertEquals(2.0, limiar.getAtencao(), 0.001);
        assertEquals(3.0, limiar.getAlerta(), 0.001);
        assertEquals(4.0, limiar.getCritico(), 0.001);
    }

    @Test
    public void deveRetornarLimiarParaSensorUmidadeSolo() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        Limiar limiar = config.getLimiar(SensorUmidadeSolo.TIPO);

        assertEquals(75.0, limiar.getAtencao(), 0.001);
        assertEquals(85.0, limiar.getAlerta(), 0.001);
        assertEquals(95.0, limiar.getCritico(), 0.001);
    }

    @Test
    public void deveLancarExcecaoParaTipoDeSensorDesconhecido() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> config.getLimiar("TIPO_DESCONHECIDO")
        );

        assertTrue(excecao.getMessage().contains("Não há limiares configurados"));
    }

    @Test
    public void deveDefinirNovoLimiarParaSensor() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        Limiar novoLimiar = new Limiar(15.0, 30.0, 60.0);

        config.definirLimiar(SensorChuva.TIPO, novoLimiar, 150.0);

        Limiar limiarAlterado = config.getLimiar(SensorChuva.TIPO);
        assertEquals(15.0, limiarAlterado.getAtencao(), 0.001);
        assertEquals(30.0, limiarAlterado.getAlerta(), 0.001);
        assertEquals(60.0, limiarAlterado.getCritico(), 0.001);
    }

    @Test
    public void deveLancarExcecaoAoDefinirLimiarNulo() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> config.definirLimiar(SensorChuva.TIPO, null, 150.0)
        );

        assertTrue(excecao.getMessage().contains("Informe o novo limiar"));
    }

    @Test
    public void deveLancarExcecaoQuandoLimiarCriticoMaiorQueMaximoSensor() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        Limiar novoLimiar = new Limiar(15.0, 30.0, 200.0);

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> config.definirLimiar(SensorChuva.TIPO, novoLimiar, 150.0)
        );

        assertTrue(excecao.getMessage().contains("maior que o máximo que o sensor consegue medir"));
    }

    @Test
    public void deveRestaurarLimiaresPadrao() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        Limiar novoLimiar = new Limiar(15.0, 30.0, 60.0);
        config.definirLimiar(SensorChuva.TIPO, novoLimiar, 150.0);

        config.restaurarLimiaresPadrao();

        Limiar limiarRestaurado = config.getLimiar(SensorChuva.TIPO);
        assertEquals(10.0, limiarRestaurado.getAtencao(), 0.001);
        assertEquals(25.0, limiarRestaurado.getAlerta(), 0.001);
        assertEquals(50.0, limiarRestaurado.getCritico(), 0.001);
    }

    @Test
    public void deveLancarExcecaoAoDefinirLimiarParaTipoDesconhecido() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        Limiar novoLimiar = new Limiar(15.0, 30.0, 60.0);

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> config.definirLimiar("TIPO_DESCONHECIDO", novoLimiar, 150.0)
        );

        assertTrue(excecao.getMessage().contains("Não há limiares configurados"));
    }
}
