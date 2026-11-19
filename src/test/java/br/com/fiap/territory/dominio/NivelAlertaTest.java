package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NivelAlertaTest {

    @Test
    public void deveRetornarGravidadeZeroParaNormal() {
        int gravidade = NivelAlerta.gravidade(NivelAlerta.NORMAL);

        assertEquals(0, gravidade);
    }

    @Test
    public void deveRetornarGravidadeUmParaAtencao() {
        int gravidade = NivelAlerta.gravidade(NivelAlerta.ATENCAO);

        assertEquals(1, gravidade);
    }

    @Test
    public void deveRetornarGravidadeDoisParaAlerta() {
        int gravidade = NivelAlerta.gravidade(NivelAlerta.ALERTA);

        assertEquals(2, gravidade);
    }

    @Test
    public void deveRetornarGravidadeTresParaCritico() {
        int gravidade = NivelAlerta.gravidade(NivelAlerta.CRITICO);

        assertEquals(3, gravidade);
    }

    @Test
    public void deveLancarExcecaoParaNivelDesconhecido() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> NivelAlerta.gravidade("DESCONHECIDO")
        );

        assertTrue(excecao.getMessage().contains("Nível de alerta desconhecido"));
    }

    @Test
    public void deveRetornarFalsoParaNivelNormal() {
        assertFalse(NivelAlerta.geraAlerta(NivelAlerta.NORMAL));
    }

    @Test
    public void deveRetornarVerdadeiroParaNivelAtencao() {
        assertTrue(NivelAlerta.geraAlerta(NivelAlerta.ATENCAO));
    }

    @Test
    public void deveRetornarVerdadeiroParaNivelAlerta() {
        assertTrue(NivelAlerta.geraAlerta(NivelAlerta.ALERTA));
    }

    @Test
    public void deveRetornarVerdadeiroParaNivelCritico() {
        assertTrue(NivelAlerta.geraAlerta(NivelAlerta.CRITICO));
    }

    @Test
    public void deveRetornarAcaoParaNivelNormal() {
        String acao = NivelAlerta.acaoRecomendada(NivelAlerta.NORMAL);

        assertTrue(acao.contains("Nenhuma ação extra"));
    }

    @Test
    public void deveRetornarAcaoParaNivelAtencao() {
        String acao = NivelAlerta.acaoRecomendada(NivelAlerta.ATENCAO);

        assertTrue(acao.contains("Acompanhar os sensores"));
        assertTrue(acao.contains("avisar os agentes comunitários"));
    }

    @Test
    public void deveRetornarAcaoParaNivelAlerta() {
        String acao = NivelAlerta.acaoRecomendada(NivelAlerta.ALERTA);

        assertTrue(acao.contains("Limpar bocas de lobo"));
        assertTrue(acao.contains("rotas de saída"));
    }

    @Test
    public void deveRetornarAcaoParaNivelCritico() {
        String acao = NivelAlerta.acaoRecomendada(NivelAlerta.CRITICO);

        assertTrue(acao.contains("Acionar a Defesa Civil"));
        assertTrue(acao.contains("199"));
        assertTrue(acao.contains("ponto de apoio"));
    }
}
