package br.com.fiap.territory.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "alertas")
public class Alerta {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String sensorCodigo;

    @Column(nullable = false, length = 80)
    private String regiao;

    @Column(nullable = false, length = 10)
    private String nivel;

    @Column(nullable = false)
    private double valorPrevisto;

    @Column(nullable = false)
    private double limiteUltrapassado;

    @Column(nullable = false, length = 10)
    private String unidade;

    @Column(nullable = false, length = 60)
    private String estrategia;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected Alerta() {
    }

    Alerta(String sensorCodigo, String regiao, String nivel, double valorPrevisto, double limiteUltrapassado,
           String unidade, String estrategia, LocalDateTime dataHora) {
        this.sensorCodigo = sensorCodigo;
        this.regiao = regiao;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiteUltrapassado = limiteUltrapassado;
        this.unidade = unidade;
        this.estrategia = estrategia;
        this.dataHora = dataHora;
    }

    public String getMotivo() {
        return String.format("Sensor %s (%s): a previsão por %s é de %.2f %s, acima do limite de %s (%.2f %s).",
                sensorCodigo, regiao, estrategia, valorPrevisto, unidade, nivel, limiteUltrapassado, unidade);
    }

    public String descrever() {
        return String.format("[%s] %s%n   Por quê: %s%n   O que fazer hoje: %s",
                nivel, dataHora.format(FORMATO_DATA), getMotivo(), NivelAlerta.acaoRecomendada(nivel));
    }

    public Long getId() {
        return id;
    }

    public String getSensorCodigo() {
        return sensorCodigo;
    }

    public String getRegiao() {
        return regiao;
    }

    public String getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimiteUltrapassado() {
        return limiteUltrapassado;
    }

    public String getUnidade() {
        return unidade;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
