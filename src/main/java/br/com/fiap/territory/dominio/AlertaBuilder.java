package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;

import java.time.LocalDateTime;

public class AlertaBuilder {

    private String sensorCodigo;
    private String regiao;
    private String nivel;
    private double valorPrevisto;
    private double limiteUltrapassado;
    private String unidade;
    private String estrategia;
    private LocalDateTime dataHora;

    private AlertaBuilder() {
    }

    public static AlertaBuilder builder() {
        return new AlertaBuilder();
    }

    public AlertaBuilder comSensor(String sensorCodigo, String regiao) {
        this.sensorCodigo = sensorCodigo;
        this.regiao = regiao;
        return this;
    }

    public AlertaBuilder comNivel(String nivel) {
        this.nivel = nivel;
        return this;
    }

    public AlertaBuilder comPrevisao(double valorPrevisto, double limiteUltrapassado, String unidade) {
        this.valorPrevisto = valorPrevisto;
        this.limiteUltrapassado = limiteUltrapassado;
        this.unidade = unidade;
        return this;
    }

    public AlertaBuilder comEstrategia(String estrategia) {
        this.estrategia = estrategia;
        return this;
    }

    public AlertaBuilder comDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
        return this;
    }

    public Alerta build() {
        if (sensorCodigo == null || sensorCodigo.isBlank() || regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("Alerta incompleto: informe o sensor e a região.");
        }
        if (nivel == null || !NivelAlerta.geraAlerta(nivel)) {
            throw new DadosInvalidosException(String.format("O nível %s não gera alerta.", nivel));
        }
        if (unidade == null || unidade.isBlank() || estrategia == null || estrategia.isBlank()) {
            throw new DadosInvalidosException("Alerta incompleto: informe a unidade e a estratégia de previsão.");
        }
        if (valorPrevisto < limiteUltrapassado) {
            throw new DadosInvalidosException(String.format(
                    "Alerta inconsistente: o valor previsto %.2f não ultrapassa o limite %.2f.",
                    valorPrevisto, limiteUltrapassado));
        }
        if (dataHora == null) {
            dataHora = LocalDateTime.now().withNano(0);
        }
        return new Alerta(sensorCodigo, regiao, nivel, valorPrevisto, limiteUltrapassado, unidade, estrategia, dataHora);
    }
}
