package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;

public abstract class ComponenteTerritorial {

    private String id;
    private String nome;
    private String regiao;
    private String status;

    public ComponenteTerritorial(String id, String nome, String regiao) {
        this.setId(id);
        this.setNome(nome);
        this.setRegiao(regiao);
        this.status = "NÃO VERIFICADO";
    }

    /**
     * Confere a situação atual do componente, atualiza o status e devolve o novo status.
     */
    public abstract String verificarStatus();

    public String descrever() {
        return String.format("[%s] %s (%s) - %s", id, nome, regiao, status);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new DadosInvalidosException(String.format("O status do componente %s não pode ser vazio.", id));
        }
        this.status = status;
    }

    private void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new DadosInvalidosException("O componente territorial precisa de um identificador.");
        }
        this.id = id;
    }

    private void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException(String.format("O componente %s precisa de um nome.", id));
        }
        this.nome = nome;
    }

    private void setRegiao(String regiao) {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException(String.format("O componente %s precisa de uma região.", id));
        }
        this.regiao = regiao;
    }
}
