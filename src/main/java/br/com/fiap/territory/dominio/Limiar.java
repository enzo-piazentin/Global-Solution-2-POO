package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;

public class Limiar {

    private final double atencao;
    private final double alerta;
    private final double critico;

    public Limiar(double atencao, double alerta, double critico) {
        if (atencao <= 0 || alerta <= 0 || critico <= 0) {
            throw new DadosInvalidosException("Limiar inválido: ATENÇÃO, ALERTA e CRÍTICO devem ser maiores que zero.");
        }
        if (atencao >= alerta || alerta >= critico) {
            throw new DadosInvalidosException(String.format(
                    "Limiar inválido: é obrigatório ATENÇÃO < ALERTA < CRÍTICO (recebido %.2f / %.2f / %.2f).",
                    atencao, alerta, critico));
        }
        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    public String classificar(double valor) {
        if (valor >= critico) {
            return NivelAlerta.CRITICO;
        }
        if (valor >= alerta) {
            return NivelAlerta.ALERTA;
        }
        if (valor >= atencao) {
            return NivelAlerta.ATENCAO;
        }
        return NivelAlerta.NORMAL;
    }

    public double limiteDoNivel(String nivel) {
        int gravidade = NivelAlerta.gravidade(nivel);
        if (gravidade == 3) {
            return critico;
        }
        if (gravidade == 2) {
            return alerta;
        }
        return atencao;
    }

    public void validarParaSensor(double valorMaximoSensor) {
        if (critico > valorMaximoSensor) {
            throw new DadosInvalidosException(String.format(
                    "Limiar inválido: CRÍTICO de %.2f é maior que o máximo que o sensor consegue medir (%.2f).",
                    critico, valorMaximoSensor));
        }
    }

    public double getAtencao() {
        return atencao;
    }

    public double getAlerta() {
        return alerta;
    }

    public double getCritico() {
        return critico;
    }
}
