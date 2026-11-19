package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.EstrategiaPrevisao;
import br.com.fiap.territory.dominio.PrevisaoComparacao;
import br.com.fiap.territory.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FabricaPrevisao {

    private final Map<String, EstrategiaPrevisao> estrategias = new HashMap<>();

    public FabricaPrevisao() {
        ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstancia();
        estrategias.put(ConfiguracaoSistema.PREVISAO_MEDIA_MOVEL, new PrevisaoMediaMovel(configuracao.getJanelaMediaMovel()));
        estrategias.put(ConfiguracaoSistema.PREVISAO_COMPARACAO, new PrevisaoComparacao(configuracao.getJanelaComparacao()));
    }

    public EstrategiaPrevisao criar(String tipoPrevisao) {
        EstrategiaPrevisao estrategia = estrategias.get(tipoPrevisao);
        if (estrategia == null) {
            throw new DadosInvalidosException(String.format("Tipo de previsão não cadastrado na fábrica: %s.", tipoPrevisao));
        }
        return estrategia;
    }

    public List<EstrategiaPrevisao> criarEstrategiasConfiguradas() {
        List<EstrategiaPrevisao> lista = new ArrayList<>();
        for (String tipo : ConfiguracaoSistema.getInstancia().getEstrategiasAtivas()) {
            lista.add(criar(tipo));
        }
        return lista;
    }
}
