package br.com.fiap.territory.dominio;

import java.util.Random;

public class SensorChuva extends SensorBase {

    public static final String TIPO = "CHUVA";

    public SensorChuva(String codigo) {
        super(codigo);
    }

    @Override
    protected double simularValor(Random random) {
        int sorteio = random.nextInt(100);
        if (sorteio < 3) {
            return -1 - random.nextDouble() * 5;
        }
        if (sorteio < 63) {
            return random.nextDouble() * 8;
        }
        if (sorteio < 88) {
            return 8 + random.nextDouble() * 27;
        }
        return 35 + random.nextDouble() * 45;
    }

    @Override
    protected String descreverFalhaComunicacao() {
        return "o pluviômetro não respondeu.";
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public String getUnidade() {
        return "mm/h";
    }

    @Override
    public double getValorMaximo() {
        return 150.0;
    }
}
