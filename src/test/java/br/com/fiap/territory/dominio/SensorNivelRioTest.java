package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SensorNivelRioTest {

    @Test
    public void deveCriarSensorNivelRioValido() {
        SensorNivelRio sensor = new SensorNivelRio("NIV-01");

        assertEquals("NIV-01", sensor.getCodigo());
        assertEquals(SensorNivelRio.TIPO, sensor.getTipo());
        assertEquals("m", sensor.getUnidade());
        assertEquals(10.0, sensor.getValorMaximo(), 0.001);
        assertTrue(sensor.verificarFuncionamento());
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoVazio() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new SensorNivelRio(" ")
        );

        assertTrue(excecao.getMessage().contains("código"));
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoNulo() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new SensorNivelRio(null)
        );

        assertTrue(excecao.getMessage().contains("código"));
    }

    @Test
    public void deveConverterCodigoParaMaiusculo() {
        SensorNivelRio sensor = new SensorNivelRio("niv-01");

        assertEquals("NIV-01", sensor.getCodigo());
    }

    @Test
    public void deveLerValor() {
        SensorNivelRio sensor = new SensorNivelRio("NIV-01");

        double valor = sensor.lerValor();

        assertTrue(valor >= 0);
        assertTrue(valor <= 10);
    }

    @Test
    public void deveLancarExcecaoQuandoSensorDesativado() {
        SensorNivelRio sensor = new SensorNivelRio("NIV-01");
        sensor.definirAtivo(false);

        FonteIndisponivelException excecao = assertThrows(
                FonteIndisponivelException.class,
                sensor::lerValor
        );

        assertTrue(excecao.getMessage().contains("manutenção"));
    }

    @Test
    public void deveRetornarTipoNivelRio() {
        SensorNivelRio sensor = new SensorNivelRio("NIV-01");

        assertEquals("NIVEL_RIO", sensor.getTipo());
    }

    @Test
    public void deveRetornarUnidadeMetros() {
        SensorNivelRio sensor = new SensorNivelRio("NIV-01");

        assertEquals("m", sensor.getUnidade());
    }

    @Test
    public void deveRetornarValorMaximo() {
        SensorNivelRio sensor = new SensorNivelRio("NIV-01");

        assertEquals(10.0, sensor.getValorMaximo(), 0.001);
    }
}
