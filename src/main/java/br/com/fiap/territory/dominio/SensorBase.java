package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;

import java.util.Random;

public abstract class SensorBase implements Sensor {

    private static final int CHANCE_FALHA_COMUNICACAO = 3;

    private final String codigo;
    private final Random random = new Random();
    private boolean ativo = true;

    public SensorBase(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new DadosInvalidosException("Todo sensor precisa de um código.");
        }
        this.codigo = codigo.trim().toUpperCase();
    }

    protected abstract double simularValor(Random random);

    protected abstract String descreverFalhaComunicacao();

    @Override
    public double lerValor() {
        if (!ativo) {
            throw new FonteIndisponivelException(String.format("Sensor %s está desativado para manutenção.", codigo));
        }
        if (random.nextInt(100) < CHANCE_FALHA_COMUNICACAO) {
            throw new FonteIndisponivelException(String.format("Sensor %s: %s", codigo, descreverFalhaComunicacao()));
        }
        return Math.round(simularValor(random) * 100) / 100.0;
    }

    @Override
    public boolean verificarFuncionamento() {
        return ativo;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public void definirAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
