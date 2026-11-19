package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.AcessoNegadoException;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DadosTerritorioTest {

    @Test
    public void deveCriarTerritorioValido() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, "ZN-2026");

        assertEquals("Bacia Cabuçu", territorio.getNome());
        assertEquals("Zona Norte", territorio.getRegiao());
        assertEquals(95000, territorio.getPopulacaoEstimada());
    }

    @Test
    public void deveLancarExcecaoQuandoNomeVazio() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new DadosTerritorio("", "Zona Norte", 95000, "ZN-2026")
        );

        assertTrue(excecao.getMessage().contains("precisa de um nome"));
    }

    @Test
    public void deveLancarExcecaoQuandoNomeNulo() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new DadosTerritorio(null, "Zona Norte", 95000, "ZN-2026")
        );

        assertTrue(excecao.getMessage().contains("precisa de um nome"));
    }

    @Test
    public void deveLancarExcecaoQuandoRegiaoVazia() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new DadosTerritorio("Bacia Cabuçu", "", 95000, "ZN-2026")
        );

        assertTrue(excecao.getMessage().contains("precisa de uma região"));
    }

    @Test
    public void deveLancarExcecaoQuandoRegiaoNula() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new DadosTerritorio("Bacia Cabuçu", null, 95000, "ZN-2026")
        );

        assertTrue(excecao.getMessage().contains("precisa de uma região"));
    }

    @Test
    public void deveLancarExcecaoQuandoPopulacaoNegativa() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new DadosTerritorio("Bacia Cabuçu", "Zona Norte", -100, "ZN-2026")
        );

        assertTrue(excecao.getMessage().contains("População negativa"));
    }

    @Test
    public void deveAceitarPopulacaoZero() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 0, "ZN-2026");

        assertEquals(0, territorio.getPopulacaoEstimada());
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoAcessoCurto() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, "ABC")
        );

        assertTrue(excecao.getMessage().contains("pelo menos 4 caracteres"));
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoAcessoNulo() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, null)
        );

        assertTrue(excecao.getMessage().contains("pelo menos 4 caracteres"));
    }

    @Test
    public void deveAutorizarAlteracaoComCodigoCorreto() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, "ZN-2026");

        territorio.autorizarAlteracaoDeLimiares("ZN-2026");
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoAcessoErrado() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, "ZN-2026");

        AcessoNegadoException excecao = assertThrows(
                AcessoNegadoException.class,
                () -> territorio.autorizarAlteracaoDeLimiares("ERRADO")
        );

        assertTrue(excecao.getMessage().contains("código de acesso inválido"));
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoAcessoNulo() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, "ZN-2026");

        AcessoNegadoException excecao = assertThrows(
                AcessoNegadoException.class,
                () -> territorio.autorizarAlteracaoDeLimiares(null)
        );

        assertTrue(excecao.getMessage().contains("código de acesso inválido"));
    }

    @Test
    public void deveAutorizarComEspacosNoCodigo() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, "ZN-2026");

        territorio.autorizarAlteracaoDeLimiares("  ZN-2026  ");
    }

    @Test
    public void deveAlterarPopulacaoEstimada() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, "ZN-2026");

        territorio.setPopulacaoEstimada(100000);

        assertEquals(100000, territorio.getPopulacaoEstimada());
    }

    @Test
    public void deveLancarExcecaoAoAlterarParaPopulacaoNegativa() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, "ZN-2026");

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> territorio.setPopulacaoEstimada(-50)
        );

        assertTrue(excecao.getMessage().contains("População negativa"));
    }

    @Test
    public void deveDescreverTerritorio() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia Cabuçu", "Zona Norte", 95000, "ZN-2026");

        String descricao = territorio.descrever();

        assertTrue(descricao.contains("Bacia Cabuçu"));
        assertTrue(descricao.contains("Zona Norte"));
        assertTrue(descricao.contains("95000"));
    }
}
