package br.com.fiap.territory.dominio;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlertaTest {

    @Test
    public void deveGerarMotivoDoAlerta() {
        Alerta alerta = AlertaBuilder.builder()
                .comSensor("CHV-01", "Brasilândia")
                .comNivel(NivelAlerta.CRITICO)
                .comPrevisao(60.0, 50.0, "mm/h")
                .comEstrategia("Média móvel")
                .comDataHora(LocalDateTime.now())
                .build();

        String motivo = alerta.getMotivo();

        assertTrue(motivo.contains("CHV-01"));
        assertTrue(motivo.contains("Brasilândia"));
        assertTrue(motivo.contains("Média móvel"));
        assertTrue(motivo.contains("60.00"));
        assertTrue(motivo.contains("mm/h"));
        assertTrue(motivo.contains("CRÍTICO"));
        assertTrue(motivo.contains("50.00"));
    }

    @Test
    public void deveDescreverAlertaCompleto() {
        Alerta alerta = AlertaBuilder.builder()
                .comSensor("CHV-01", "Brasilândia")
                .comNivel(NivelAlerta.CRITICO)
                .comPrevisao(60.0, 50.0, "mm/h")
                .comEstrategia("Média móvel")
                .comDataHora(LocalDateTime.now())
                .build();

        String descricao = alerta.descrever();

        assertTrue(descricao.contains("CRÍTICO"));
        assertTrue(descricao.contains("Por quê:"));
        assertTrue(descricao.contains("O que fazer hoje:"));
        assertTrue(descricao.contains("Acionar a Defesa Civil"));
    }

    @Test
    public void deveDescreverAlertaComAcaoParaNivelAtencao() {
        Alerta alerta = AlertaBuilder.builder()
                .comSensor("CHV-01", "Brasilândia")
                .comNivel(NivelAlerta.ATENCAO)
                .comPrevisao(15.0, 10.0, "mm/h")
                .comEstrategia("Média móvel")
                .comDataHora(LocalDateTime.now())
                .build();

        String descricao = alerta.descrever();

        assertTrue(descricao.contains("ATENÇÃO"));
        assertTrue(descricao.contains("Acompanhar os sensores"));
    }

    @Test
    public void deveDescreverAlertaComAcaoParaNivelAlerta() {
        Alerta alerta = AlertaBuilder.builder()
                .comSensor("CHV-01", "Brasilândia")
                .comNivel(NivelAlerta.ALERTA)
                .comPrevisao(30.0, 25.0, "mm/h")
                .comEstrategia("Comparação")
                .comDataHora(LocalDateTime.now())
                .build();

        String descricao = alerta.descrever();

        assertTrue(descricao.contains("ALERTA"));
        assertTrue(descricao.contains("Limpar bocas de lobo"));
    }
}
