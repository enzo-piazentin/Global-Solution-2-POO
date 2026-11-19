package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlertaBuilderTest {

    @Test
    public void deveCriarAlertaValido() {
        Alerta alerta = AlertaBuilder.builder()
                .comSensor("CHV-01", "Brasilândia")
                .comNivel(NivelAlerta.CRITICO)
                .comPrevisao(60.0, 50.0, "mm/h")
                .comEstrategia("Média móvel")
                .comDataHora(LocalDateTime.now())
                .build();

        assertEquals("CHV-01", alerta.getSensorCodigo());
        assertEquals("Brasilândia", alerta.getRegiao());
        assertEquals(NivelAlerta.CRITICO, alerta.getNivel());
        assertEquals(60.0, alerta.getValorPrevisto(), 0.001);
        assertEquals(50.0, alerta.getLimiteUltrapassado(), 0.001);
        assertEquals("mm/h", alerta.getUnidade());
        assertEquals("Média móvel", alerta.getEstrategia());
        assertNotNull(alerta.getDataHora());
    }

    @Test
    public void deveGerarDataHoraAutomaticamenteQuandoNaoInformada() {
        Alerta alerta = AlertaBuilder.builder()
                .comSensor("CHV-01", "Brasilândia")
                .comNivel(NivelAlerta.ALERTA)
                .comPrevisao(30.0, 25.0, "mm/h")
                .comEstrategia("Comparação")
                .build();

        assertNotNull(alerta.getDataHora());
    }

    @Test
    public void deveLancarExcecaoQuandoSensorVazio() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> AlertaBuilder.builder()
                        .comSensor(" ", "Brasilândia")
                        .comNivel(NivelAlerta.CRITICO)
                        .comPrevisao(60.0, 50.0, "mm/h")
                        .comEstrategia("Média móvel")
                        .build()
        );

        assertTrue(excecao.getMessage().contains("informe o sensor e a região"));
    }

    @Test
    public void deveLancarExcecaoQuandoRegiaoVazia() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> AlertaBuilder.builder()
                        .comSensor("CHV-01", " ")
                        .comNivel(NivelAlerta.CRITICO)
                        .comPrevisao(60.0, 50.0, "mm/h")
                        .comEstrategia("Média móvel")
                        .build()
        );

        assertTrue(excecao.getMessage().contains("informe o sensor e a região"));
    }

    @Test
    public void deveLancarExcecaoQuandoNivelNormal() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> AlertaBuilder.builder()
                        .comSensor("CHV-01", "Brasilândia")
                        .comNivel(NivelAlerta.NORMAL)
                        .comPrevisao(5.0, 10.0, "mm/h")
                        .comEstrategia("Média móvel")
                        .build()
        );

        assertTrue(excecao.getMessage().contains("não gera alerta"));
    }

    @Test
    public void deveLancarExcecaoQuandoNivelNulo() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> AlertaBuilder.builder()
                        .comSensor("CHV-01", "Brasilândia")
                        .comNivel(null)
                        .comPrevisao(60.0, 50.0, "mm/h")
                        .comEstrategia("Média móvel")
                        .build()
        );

        assertTrue(excecao.getMessage().contains("não gera alerta"));
    }

    @Test
    public void deveLancarExcecaoQuandoUnidadeVazia() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> AlertaBuilder.builder()
                        .comSensor("CHV-01", "Brasilândia")
                        .comNivel(NivelAlerta.CRITICO)
                        .comPrevisao(60.0, 50.0, " ")
                        .comEstrategia("Média móvel")
                        .build()
        );

        assertTrue(excecao.getMessage().contains("informe a unidade e a estratégia"));
    }

    @Test
    public void deveLancarExcecaoQuandoEstrategiaVazia() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> AlertaBuilder.builder()
                        .comSensor("CHV-01", "Brasilândia")
                        .comNivel(NivelAlerta.CRITICO)
                        .comPrevisao(60.0, 50.0, "mm/h")
                        .comEstrategia(" ")
                        .build()
        );

        assertTrue(excecao.getMessage().contains("informe a unidade e a estratégia"));
    }

    @Test
    public void deveLancarExcecaoQuandoValorPrevistoMenorQueLimite() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> AlertaBuilder.builder()
                        .comSensor("CHV-01", "Brasilândia")
                        .comNivel(NivelAlerta.CRITICO)
                        .comPrevisao(40.0, 50.0, "mm/h")
                        .comEstrategia("Média móvel")
                        .build()
        );

        assertTrue(excecao.getMessage().contains("não ultrapassa o limite"));
    }

    @Test
    public void deveAceitarValorPrevistoIgualAoLimite() {
        Alerta alerta = AlertaBuilder.builder()
                .comSensor("CHV-01", "Brasilândia")
                .comNivel(NivelAlerta.CRITICO)
                .comPrevisao(50.0, 50.0, "mm/h")
                .comEstrategia("Média móvel")
                .build();

        assertEquals(50.0, alerta.getValorPrevisto(), 0.001);
        assertEquals(50.0, alerta.getLimiteUltrapassado(), 0.001);
    }
}
