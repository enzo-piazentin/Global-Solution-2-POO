package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;

import java.time.LocalDateTime;

public class MedicaoBuilder {

    private String sensorCodigo;
    private String tipoSensor;
    private String regiao;
    private double valor;
    private boolean valorInformado = false;
    private String unidade;
    private LocalDateTime dataHora;

    private MedicaoBuilder() {
    }

    public static MedicaoBuilder builder() {
        return new MedicaoBuilder();
    }

    public MedicaoBuilder comSensor(String sensorCodigo, String tipoSensor) {
        this.sensorCodigo = sensorCodigo;
        this.tipoSensor = tipoSensor;
        return this;
    }

    public MedicaoBuilder comRegiao(String regiao) {
        this.regiao = regiao;
        return this;
    }

    public MedicaoBuilder comValor(double valor, String unidade) {
        this.valor = valor;
        this.unidade = unidade;
        this.valorInformado = true;
        return this;
    }

    public MedicaoBuilder comDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
        return this;
    }

    public Medicao build() {
        if (sensorCodigo == null || sensorCodigo.isBlank()) {
            throw new DadosInvalidosException("Medição sem código de sensor não pode ser registrada.");
        }
        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException(String.format("Medição do sensor %s sem tipo informado.", sensorCodigo));
        }
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException(String.format("Medição do sensor %s sem região informada.", sensorCodigo));
        }
        if (!valorInformado || unidade == null || unidade.isBlank()) {
            throw new DadosInvalidosException(String.format("Medição do sensor %s sem valor ou unidade.", sensorCodigo));
        }
        if (dataHora == null) {
            dataHora = LocalDateTime.now().withNano(0);
        }
        return new Medicao(sensorCodigo, tipoSensor, regiao, valor, unidade, dataHora);
    }
}
