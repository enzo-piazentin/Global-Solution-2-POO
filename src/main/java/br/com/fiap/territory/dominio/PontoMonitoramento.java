package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;

import java.util.ArrayList;
import java.util.List;

public class PontoMonitoramento extends ComponenteTerritorial {

    private final List<Sensor> sensores = new ArrayList<>();

    public PontoMonitoramento(String id, String nome, String regiao, List<Sensor> sensoresInstalados) {
        super(id, nome, regiao);
        if (sensoresInstalados == null || sensoresInstalados.isEmpty()) {
            throw new DadosInvalidosException(String.format("O ponto %s precisa de pelo menos um sensor.", id));
        }
        for (Sensor sensor : sensoresInstalados) {
            instalarSensor(sensor);
        }
    }

    public boolean possuiSensor(String codigoSensor) {
        for (Sensor sensor : sensores) {
            if (sensor.getCodigo().equalsIgnoreCase(codigoSensor)) {
                return true;
            }
        }
        return false;
    }

    public List<Sensor> getSensores() {
        return new ArrayList<>(sensores);
    }

    @Override
    public String verificarStatus() {
        int ativos = 0;
        for (Sensor sensor : sensores) {
            if (sensor.verificarFuncionamento()) {
                ativos++;
            }
        }

        if (ativos == 0) {
            setStatus("INOPERANTE");
        } else if (ativos == sensores.size()) {
            setStatus("OPERANTE");
        } else {
            setStatus("OPERAÇÃO PARCIAL");
        }
        return getStatus();
    }

    @Override
    public String descrever() {
        StringBuilder texto = new StringBuilder(super.descrever());
        texto.append(" | sensores:");
        for (Sensor sensor : sensores) {
            String situacao = "ativo";
            if (!sensor.verificarFuncionamento()) {
                situacao = "em manutenção";
            }
            texto.append(String.format(" %s (%s)", sensor.getCodigo(), situacao));
        }
        return texto.toString();
    }

    private void instalarSensor(Sensor sensor) {
        if (sensor == null) {
            throw new DadosInvalidosException(String.format("Não é possível instalar um sensor nulo no ponto %s.", getId()));
        }
        if (possuiSensor(sensor.getCodigo())) {
            throw new DadosInvalidosException(String.format("O sensor %s já está instalado no ponto %s.",
                    sensor.getCodigo(), getId()));
        }
        sensores.add(sensor);
    }
}
