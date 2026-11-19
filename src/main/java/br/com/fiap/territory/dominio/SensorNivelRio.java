package br.com.fiap.territory.dominio;

import java.util.Random;

public class SensorNivelRio extends SensorBase {

    public static final String TIPO = "NIVEL_RIO";

    public SensorNivelRio(String codigo) {
        super(codigo);
    }

    @Override
    protected double simularValor(Random random) {
        int sorteio = random.nextInt(100);
        if (sorteio < 3) {
            return 20 + random.nextDouble() * 80;
        }
        if (sorteio < 65) {
            return 0.3 + random.nextDouble() * 1.7;
        }
        if (sorteio < 90) {
            return 2.0 + random.nextDouble() * 1.5;
        }
        return 3.5 + random.nextDouble() * 2.0;
    }

    @Override
    protected String descreverFalhaComunicacao() {
        return "a régua ultrassônica perdeu o sinal.";
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public String getUnidade() {
        return "m";
    }

    @Override
    public double getValorMaximo() {
        return 8.0;
    }
}
