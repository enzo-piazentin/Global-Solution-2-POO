package br.com.fiap.territory.dominio;

import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    public PrevisaoMediaMovel(int janela) {
        super("Média móvel", janela);
    }

    @Override
    public double prever(List<Medicao> historico) {
        validarHistorico(historico);
        int fim = historico.size();
        int inicio = fim - getJanela();
        return calcularMedia(historico, inicio, fim);
    }

    @Override
    public String explicarResultado(double valorPrevisto, String unidade) {
        return String.format("[%s] Previsto: %.2f %s -> média das últimas %d leituras: é o valor esperado se o ritmo recente continuar igual.",
                getNome(), valorPrevisto, unidade, getJanela());
    }
}
