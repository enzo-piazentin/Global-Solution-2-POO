package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;

import java.util.List;

public abstract class EstrategiaPrevisao {

    private final String nome;
    private final int janela;

    public EstrategiaPrevisao(String nome, int janela) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("A estratégia de previsão precisa de um nome.");
        }
        if (janela <= 0) {
            throw new DadosInvalidosException(String.format(
                    "Janela de previsão inválida para %s: %d (deve ser maior que zero).", nome, janela));
        }
        this.nome = nome;
        this.janela = janela;
    }

    /**
     * Calcula o valor previsto a partir das medições em ordem cronológica.
     * Lança DadosInvalidosException se o histórico tiver menos leituras que o mínimo da estratégia.
     */
    public abstract double prever(List<Medicao> historico);

    /**
     * Explica em linguagem simples o que o valor previsto significa para quem vai decidir.
     */
    public abstract String explicarResultado(double valorPrevisto, String unidade);

    public int getMinimoLeituras() {
        return janela;
    }

    public String getNome() {
        return nome;
    }

    public int getJanela() {
        return janela;
    }

    protected void validarHistorico(List<Medicao> historico) {
        int disponiveis = 0;
        if (historico != null) {
            disponiveis = historico.size();
        }
        if (disponiveis < getMinimoLeituras()) {
            throw new DadosInvalidosException(String.format("Histórico insuficiente para %s: %d leitura(s), mínimo de %d.",
                    nome, disponiveis, getMinimoLeituras()));
        }
    }

    protected double calcularMedia(List<Medicao> historico, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma = soma + historico.get(i).getValor();
        }
        return soma / (fim - inicio);
    }
}
