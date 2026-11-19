package br.com.fiap.territory.dominio;

import br.com.fiap.territory.dominio.Medicao;
import br.com.fiap.territory.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrevisaoMediaMovelTest {

    private PrevisaoMediaMovel previsao;

    @BeforeEach
    public void setUp() {
        previsao = new PrevisaoMediaMovel(5);
    }

    @Test
    public void deveCalcularMediaDasUltimasCincoLeituras() {
        List<Medicao> historico = criarHistorico(10.0, 20.0, 30.0, 40.0, 50.0);

        double resultado = previsao.prever(historico);

        assertEquals(30.0, resultado, 0.001);
    }

    @Test
    public void deveIgnorarLeiturasMaisAntigasQueAJanela() {
        List<Medicao> historico = criarHistorico(100.0, 100.0, 2.0, 4.0, 6.0, 8.0, 10.0);

        double resultado = previsao.prever(historico);

        assertEquals(6.0, resultado, 0.001);
    }

    @Test
    public void deveLancarExcecaoQuandoHistoricoTemMenosQueCincoLeituras() {
        List<Medicao> historico = criarHistorico(10.0, 20.0, 30.0);

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> previsao.prever(historico)
        );

        assertEquals("Histórico insuficiente para Média móvel: 3 leitura(s), mínimo de 5.", excecao.getMessage());
    }

    @Test
    public void deveExplicarOSignificadoDoValorPrevisto() {
        double valorPrevisto = 12.0;

        String explicacao = previsao.explicarResultado(valorPrevisto, "mm/h");

        assertTrue(explicacao.contains("média das últimas 5 leituras"));
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
