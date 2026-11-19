package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PontoMonitoramentoTest {

    @Test
    public void deveCriarPontoComSensores() {
        List<Sensor> sensores = new ArrayList<>();
        sensores.add(new SensorChuva("CHV-01"));
        sensores.add(new SensorNivelRio("NIV-01"));

        PontoMonitoramento ponto = new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", sensores);

        assertEquals("PM-01", ponto.getId());
        assertEquals("Ponto Brasilândia", ponto.getNome());
        assertEquals("Brasilândia", ponto.getRegiao());
        assertEquals(2, ponto.getSensores().size());
    }

    @Test
    public void deveLancarExcecaoQuandoPontoSemSensores() {
        List<Sensor> sensoresVazios = new ArrayList<>();

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", sensoresVazios)
        );

        assertTrue(excecao.getMessage().contains("precisa de pelo menos um sensor"));
    }

    @Test
    public void deveLancarExcecaoQuandoListaDeSensoresNula() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", null)
        );

        assertTrue(excecao.getMessage().contains("precisa de pelo menos um sensor"));
    }

    @Test
    public void deveLancarExcecaoQuandoSensorDuplicado() {
        List<Sensor> sensores = new ArrayList<>();
        sensores.add(new SensorChuva("CHV-01"));
        sensores.add(new SensorChuva("CHV-01"));

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", sensores)
        );

        assertTrue(excecao.getMessage().contains("já está instalado"));
    }

    @Test
    public void deveLancarExcecaoQuandoSensorNulo() {
        List<Sensor> sensores = new ArrayList<>();
        sensores.add(null);

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", sensores)
        );

        assertTrue(excecao.getMessage().contains("sensor nulo"));
    }

    @Test
    public void deveVerificarSePossuiSensor() {
        List<Sensor> sensores = new ArrayList<>();
        sensores.add(new SensorChuva("CHV-01"));
        sensores.add(new SensorNivelRio("NIV-01"));

        PontoMonitoramento ponto = new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", sensores);

        assertTrue(ponto.possuiSensor("CHV-01"));
        assertTrue(ponto.possuiSensor("NIV-01"));
    }

    @Test
    public void deveRetornarStatusOperanteQuandoTodosSensoresAtivos() {
        List<Sensor> sensores = new ArrayList<>();
        sensores.add(new SensorChuva("CHV-01"));
        sensores.add(new SensorNivelRio("NIV-01"));

        PontoMonitoramento ponto = new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", sensores);

        String status = ponto.verificarStatus();

        assertEquals("OPERANTE", status);
    }

    @Test
    public void deveRetornarStatusInoperanteQuandoNenhumSensorAtivo() {
        List<Sensor> sensores = new ArrayList<>();
        Sensor sensor1 = new SensorChuva("CHV-01");
        Sensor sensor2 = new SensorNivelRio("NIV-01");
        sensor1.definirAtivo(false);
        sensor2.definirAtivo(false);
        sensores.add(sensor1);
        sensores.add(sensor2);

        PontoMonitoramento ponto = new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", sensores);

        String status = ponto.verificarStatus();

        assertEquals("INOPERANTE", status);
    }

    @Test
    public void deveRetornarStatusOperacaoParcialQuandoAlgunsSensoresAtivos() {
        List<Sensor> sensores = new ArrayList<>();
        Sensor sensor1 = new SensorChuva("CHV-01");
        Sensor sensor2 = new SensorNivelRio("NIV-01");
        sensor1.definirAtivo(false);
        sensores.add(sensor1);
        sensores.add(sensor2);

        PontoMonitoramento ponto = new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", sensores);

        String status = ponto.verificarStatus();

        assertEquals("OPERAÇÃO PARCIAL", status);
    }

    @Test
    public void deveDescreverPontoComSensores() {
        List<Sensor> sensores = new ArrayList<>();
        Sensor sensor1 = new SensorChuva("CHV-01");
        Sensor sensor2 = new SensorNivelRio("NIV-01");
        sensor2.definirAtivo(false);
        sensores.add(sensor1);
        sensores.add(sensor2);

        PontoMonitoramento ponto = new PontoMonitoramento("PM-01", "Ponto Brasilândia", "Brasilândia", sensores);

        String descricao = ponto.descrever();

        assertTrue(descricao.contains("PM-01"));
        assertTrue(descricao.contains("CHV-01"));
        assertTrue(descricao.contains("ativo"));
        assertTrue(descricao.contains("NIV-01"));
        assertTrue(descricao.contains("em manutenção"));
    }
}
