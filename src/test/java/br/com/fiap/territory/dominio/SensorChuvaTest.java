package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SensorChuvaTest {

    @Test
    public void deveCriarSensorChuvaValido() {
        SensorChuva sensor = new SensorChuva("CHV-01");

        assertEquals("CHV-01", sensor.getCodigo());
        assertEquals(SensorChuva.TIPO, sensor.getTipo());
        assertEquals("mm/h", sensor.getUnidade());
        assertEquals(150.0, sensor.getValorMaximo(), 0.001);
        assertTrue(sensor.verificarFuncionamento());
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoVazio() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new SensorChuva(" ")
        );

        assertTrue(excecao.getMessage().contains("código"));
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoNulo() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new SensorChuva(null)
        );

        assertTrue(excecao.getMessage().contains("código"));
    }

    @Test
    public void deveConverterCodigoParaMaiusculo() {
        SensorChuva sensor = new SensorChuva("chv-01");

        assertEquals("CHV-01", sensor.getCodigo());
    }

    @Test
    public void deveRemoverEspacosDoCodigo() {
        SensorChuva sensor = new SensorChuva("  CHV-01  ");

        assertEquals("CHV-01", sensor.getCodigo());
    }

    @Test
    public void deveLerValor() {
        SensorChuva sensor = new SensorChuva("CHV-01");

        double valor = sensor.lerValor();

        assertTrue(valor >= 0);
        assertTrue(valor <= 150);
    }

    @Test
    public void deveLancarExcecaoQuandoSensorDesativado() {
        SensorChuva sensor = new SensorChuva("CHV-01");
        sensor.definirAtivo(false);

        FonteIndisponivelException excecao = assertThrows(
                FonteIndisponivelException.class,
                sensor::lerValor
        );

        assertTrue(excecao.getMessage().contains("manutenção"));
    }

    @Test
    public void deveRetornarTipoChuva() {
        SensorChuva sensor = new SensorChuva("CHV-01");

        assertEquals("CHUVA", sensor.getTipo());
    }

    @Test
    public void deveRetornarUnidadeMmPorHora() {
        SensorChuva sensor = new SensorChuva("CHV-01");

        assertEquals("mm/h", sensor.getUnidade());
    }

    @Test
    public void deveRetornarValorMaximo() {
        SensorChuva sensor = new SensorChuva("CHV-01");

        assertEquals(150.0, sensor.getValorMaximo(), 0.001);
    }

    @Test
    public void deveAtivarSensor() {
        SensorChuva sensor = new SensorChuva("CHV-01");
        sensor.definirAtivo(false);

        sensor.definirAtivo(true);

        assertTrue(sensor.verificarFuncionamento());
    }

    @Test
    public void deveDesativarSensor() {
        SensorChuva sensor = new SensorChuva("CHV-01");

        sensor.definirAtivo(false);

        assertTrue(!sensor.verificarFuncionamento());
    }
}
