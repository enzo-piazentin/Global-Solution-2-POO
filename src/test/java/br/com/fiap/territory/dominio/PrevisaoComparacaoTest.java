package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrevisaoComparacaoTest {

    private PrevisaoComparacao previsao;

    @BeforeEach
    public void setUp() {
        previsao = new PrevisaoComparacao(3);
    }

    @Test
    public void deveCalcularPrevisaoComComparacao() {
        List<Medicao> historico = criarHistorico(10.0, 10.0, 10.0, 20.0, 20.0, 20.0);

        double resultado = previsao.prever(historico);

        assertEquals(30.0, resultado, 0.001);
    }

    @Test
    public void deveCalcularPrevisaoQuandoTendenciaDeMelhora() {
        List<Medicao> historico = criarHistorico(20.0, 20.0, 20.0, 10.0, 10.0, 10.0);

        double resultado = previsao.prever(historico);

        assertEquals(0.0, resultado, 0.001);
    }

    @Test
    public void deveIgnorarLeiturasMaisAntigasQueAJanela() {
        List<Medicao> historico = criarHistorico(100.0, 100.0, 100.0, 10.0, 10.0, 10.0, 20.0, 20.0, 20.0);

        double resultado = previsao.prever(historico);

        assertEquals(30.0, resultado, 0.001);
    }

    @Test
    public void deveLancarExcecaoQuandoHistoricoTemMenosDeSeisLeituras() {
        List<Medicao> historico = criarHistorico(10.0, 20.0, 30.0, 40.0, 40.0);

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> previsao.prever(historico)
        );

        assertTrue(excecao.getMessage().contains("Histórico insuficiente"));
        assertTrue(excecao.getMessage().contains("6"));
    }

    @Test
    public void deveExplicarOSignificadoDoValorPrevisto() {
        double valorPrevisto = 15.0;

        String explicacao = previsao.explicarResultado(valorPrevisto, "mm/h");

        assertTrue(explicacao.contains("Comparação com período anterior"));
        assertTrue(explicacao.contains("média das últimas 3 leituras"));
        assertTrue(explicacao.contains("variação em relação às 3 anteriores"));
    }

    @Test
    public void deveRetornarMinimoLeiturasDobrado() {
        int minimo = previsao.getMinimoLeituras();

        assertEquals(6, minimo);
    }

    private List<Medicao> criarHistorico(double... valores) {
        List<Medicao> historico = new ArrayList<>();
        for (double valor : valores) {
            historico.add(MedicaoBuilder.builder()
                    .comSensor("CHV-01", "CHUVA")
                    .comRegiao("Brasilândia")
                    .comValor(valor, "mm/h")
                    .build());
        }
        return historico;
    }
}
