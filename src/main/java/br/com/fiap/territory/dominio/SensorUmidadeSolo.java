package br.com.fiap.territory.dominio;

import java.util.Random;

public class SensorUmidadeSolo extends SensorBase {

    public static final String TIPO = "UMIDADE_SOLO";

    public SensorUmidadeSolo(String codigo) {
        super(codigo);
    }

    @Override
    protected double simularValor(Random random) {
        int sorteio = random.nextInt(100);
        if (sorteio < 3) {
            return 101 + random.nextDouble() * 40;
        }
        if (sorteio < 60) {
            return 35 + random.nextDouble() * 35;
        }
        if (sorteio < 90) {
            return 70 + random.nextDouble() * 20;
        }
        return 90 + random.nextDouble() * 10;
    }

    @Override
    protected String descreverFalhaComunicacao() {
        return "a sonda de umidade está sem bateria.";
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public String getUnidade() {
        return "%";
    }

    @Override
    public double getValorMaximo() {
        return 100.0;
    }
}
