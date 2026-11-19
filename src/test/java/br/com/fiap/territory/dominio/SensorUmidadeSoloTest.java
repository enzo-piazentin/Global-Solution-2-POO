package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SensorUmidadeSoloTest {

    @Test
    public void deveCriarSensorUmidadeSoloValido() {
        SensorUmidadeSolo sensor = new SensorUmidadeSolo("UMI-01");

        assertEquals("UMI-01", sensor.getCodigo());
        assertEquals(SensorUmidadeSolo.TIPO, sensor.getTipo());
        assertEquals("%", sensor.getUnidade());
        assertEquals(100.0, sensor.getValorMaximo(), 0.001);
        assertTrue(sensor.verificarFuncionamento());
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoVazio() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new SensorUmidadeSolo(" ")
        );

        assertTrue(excecao.getMessage().contains("código"));
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoNulo() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new SensorUmidadeSolo(null)
        );

        assertTrue(excecao.getMessage().contains("código"));
    }

    @Test
    public void deveConverterCodigoParaMaiusculo() {
        SensorUmidadeSolo sensor = new SensorUmidadeSolo("umi-01");

        assertEquals("UMI-01", sensor.getCodigo());
    }

    @Test
    public void deveLerValor() {
        SensorUmidadeSolo sensor = new SensorUmidadeSolo("UMI-01");

        double valor = sensor.lerValor();

        assertTrue(valor >= 0);
        assertTrue(valor <= 100);
    }

    @Test
    public void deveLancarExcecaoQuandoSensorDesativado() {
        SensorUmidadeSolo sensor = new SensorUmidadeSolo("UMI-01");
        sensor.definirAtivo(false);

        FonteIndisponivelException excecao = assertThrows(
                FonteIndisponivelException.class,
                sensor::lerValor
        );

        assertTrue(excecao.getMessage().contains("manutenção"));
    }

    @Test
    public void deveRetornarTipoUmidadeSolo() {
        SensorUmidadeSolo sensor = new SensorUmidadeSolo("UMI-01");

        assertEquals("UMIDADE_SOLO", sensor.getTipo());
    }

    @Test
    public void deveRetornarUnidadePorcentagem() {
        SensorUmidadeSolo sensor = new SensorUmidadeSolo("UMI-01");

        assertEquals("%", sensor.getUnidade());
    }

    @Test
    public void deveRetornarValorMaximo() {
        SensorUmidadeSolo sensor = new SensorUmidadeSolo("UMI-01");

        assertEquals(100.0, sensor.getValorMaximo(), 0.001);
    }
}
