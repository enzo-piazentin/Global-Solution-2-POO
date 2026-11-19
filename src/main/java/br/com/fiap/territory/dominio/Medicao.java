package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "medicoes")
public class Medicao {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String sensorCodigo;

    @Column(nullable = false, length = 20)
    private String tipoSensor;

    @Column(nullable = false, length = 80)
    private String regiao;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false, length = 10)
    private String unidade;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected Medicao() {
    }

    Medicao(String sensorCodigo, String tipoSensor, String regiao, double valor, String unidade, LocalDateTime dataHora) {
        this.sensorCodigo = sensorCodigo;
        this.tipoSensor = tipoSensor;
        this.regiao = regiao;
        this.unidade = unidade;
        this.dataHora = dataHora;
        this.setValor(valor);
    }

    public String descrever() {
        return String.format("%s | %s | %s: %.2f %s", dataHora.format(FORMATO_DATA), sensorCodigo, tipoSensor,
                valor, unidade);
    }

    public Long getId() {
        return id;
    }

    public String getSensorCodigo() {
        return sensorCodigo;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public String getRegiao() {
        return regiao;
    }

    public double getValor() {
        return valor;
    }

    public String getUnidade() {
        return unidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    private void setValor(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DadosInvalidosException(String.format("Medição do sensor %s não é um número válido.", sensorCodigo));
        }
        if (valor < 0) {
            throw new DadosInvalidosException(String.format("Medição negativa no sensor %s (%s): %.2f %s.",
                    sensorCodigo, tipoSensor, valor, unidade));
        }
        this.valor = valor;
    }
}
