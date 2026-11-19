package br.com.fiap.territory.dominio;

import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    public PrevisaoComparacao(int janela) {
        super("Comparação com período anterior", janela);
    }

    @Override
    public int getMinimoLeituras() {
        return super.getMinimoLeituras() * 2;
    }

    @Override
    public double prever(List<Medicao> historico) {
        validarHistorico(historico);
        int fim = historico.size();
        int meio = fim - getJanela();
        int inicio = meio - getJanela();

        double mediaAtual = calcularMedia(historico, meio, fim);
        double mediaAnterior = calcularMedia(historico, inicio, meio);
        double previsto = mediaAtual + (mediaAtual - mediaAnterior);

        if (previsto < 0) {
            return 0;
        }
        return previsto;
    }

    @Override
    public String explicarResultado(double valorPrevisto, String unidade) {
        return String.format("[%s] Previsto: %.2f %s -> média das últimas %d leituras somada à variação em relação às %d anteriores: mostra se a situação tende a piorar ou a melhorar.",
                getNome(), valorPrevisto, unidade, getJanela(), getJanela());
    }
}
