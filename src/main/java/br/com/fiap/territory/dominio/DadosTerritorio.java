package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.AcessoNegadoException;
import br.com.fiap.territory.excecao.DadosInvalidosException;

public class DadosTerritorio {

    private String nome;
    private String regiao;
    private int populacaoEstimada;
    private final String codigoAcesso;

    public DadosTerritorio(String nome, String regiao, int populacaoEstimada, String codigoAcesso) {
        if (codigoAcesso == null || codigoAcesso.trim().length() < 4) {
            throw new DadosInvalidosException("O código de acesso do território precisa ter pelo menos 4 caracteres.");
        }
        this.codigoAcesso = codigoAcesso.trim();
        this.setNome(nome);
        this.setRegiao(regiao);
        this.setPopulacaoEstimada(populacaoEstimada);
    }

    public void autorizarAlteracaoDeLimiares(String codigoInformado) {
        if (codigoInformado == null || !codigoAcesso.equals(codigoInformado.trim())) {
            throw new AcessoNegadoException("Acesso negado: código de acesso inválido para alterar limiares.");
        }
    }

    public String descrever() {
        return String.format("Território: %s%nRegião: %s%nPopulação estimada na área de risco: %d pessoas",
                nome, regiao, populacaoEstimada);
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public int getPopulacaoEstimada() {
        return populacaoEstimada;
    }

    public void setPopulacaoEstimada(int populacaoEstimada) {
        if (populacaoEstimada < 0) {
            throw new DadosInvalidosException(String.format("População negativa não é permitida: %d.", populacaoEstimada));
        }
        this.populacaoEstimada = populacaoEstimada;
    }

    private void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O território precisa de um nome.");
        }
        this.nome = nome;
    }

    private void setRegiao(String regiao) {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("O território precisa de uma região.");
        }
        this.regiao = regiao;
    }
}
