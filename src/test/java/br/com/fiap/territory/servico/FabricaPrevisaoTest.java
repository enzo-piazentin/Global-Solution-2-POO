package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.EstrategiaPrevisao;
import br.com.fiap.territory.dominio.PrevisaoComparacao;
import br.com.fiap.territory.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FabricaPrevisaoTest {

    private FabricaPrevisao fabrica;

    @BeforeEach
    public void setUp() {
        ConfiguracaoSistema.getInstancia().restaurarLimiaresPadrao();
        fabrica = new FabricaPrevisao();
    }

    @AfterEach
    public void tearDown() {
        ConfiguracaoSistema.getInstancia().restaurarLimiaresPadrao();
    }

    @Test
    public void deveCriarEstrategiaMediaMovel() {
        EstrategiaPrevisao estrategia = fabrica.criar(ConfiguracaoSistema.PREVISAO_MEDIA_MOVEL);

        assertEquals(PrevisaoMediaMovel.class, estrategia.getClass());
        assertEquals("Média móvel", estrategia.getNome());
        assertEquals(5, estrategia.getJanela());
    }

    @Test
    public void deveCriarEstrategiaComparacao() {
        EstrategiaPrevisao estrategia = fabrica.criar(ConfiguracaoSistema.PREVISAO_COMPARACAO);

        assertEquals(PrevisaoComparacao.class, estrategia.getClass());
        assertEquals("Comparação com período anterior", estrategia.getNome());
        assertEquals(3, estrategia.getJanela());
    }

    @Test
    public void deveLancarExcecaoParaTipoDesconhecido() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> fabrica.criar("TIPO_DESCONHECIDO")
        );

        assertTrue(excecao.getMessage().contains("Tipo de previsão não cadastrado na fábrica"));
    }

    @Test
    public void deveCriarListaDeEstrategiasConfiguradas() {
        List<EstrategiaPrevisao> estrategias = fabrica.criarEstrategiasConfiguradas();

        assertEquals(2, estrategias.size());
        assertTrue(estrategias.stream().anyMatch(e -> e instanceof PrevisaoMediaMovel));
        assertTrue(estrategias.stream().anyMatch(e -> e instanceof PrevisaoComparacao));
    }

    @Test
    public void deveRetornarMesmaInstanciaDeEstrategia() {
        EstrategiaPrevisao estrategia1 = fabrica.criar(ConfiguracaoSistema.PREVISAO_MEDIA_MOVEL);
        EstrategiaPrevisao estrategia2 = fabrica.criar(ConfiguracaoSistema.PREVISAO_MEDIA_MOVEL);

        assertEquals(estrategia1, estrategia2);
    }

    @Test
    public void deveUsarJanelaDaConfiguracaoParaMediaMovel() {
        EstrategiaPrevisao estrategia = fabrica.criar(ConfiguracaoSistema.PREVISAO_MEDIA_MOVEL);

        assertEquals(ConfiguracaoSistema.getInstancia().getJanelaMediaMovel(), estrategia.getJanela());
    }

    @Test
    public void deveUsarJanelaDaConfiguracaoParaComparacao() {
        EstrategiaPrevisao estrategia = fabrica.criar(ConfiguracaoSistema.PREVISAO_COMPARACAO);

        assertEquals(ConfiguracaoSistema.getInstancia().getJanelaComparacao(), estrategia.getJanela());
    }
}
