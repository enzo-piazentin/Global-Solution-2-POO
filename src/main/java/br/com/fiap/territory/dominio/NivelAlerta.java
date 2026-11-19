package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;

public class NivelAlerta {

    public static final String NORMAL = "NORMAL";
    public static final String ATENCAO = "ATENÇÃO";
    public static final String ALERTA = "ALERTA";
    public static final String CRITICO = "CRÍTICO";

    private NivelAlerta() {
    }

    public static int gravidade(String nivel) {
        if (NORMAL.equals(nivel)) {
            return 0;
        }
        if (ATENCAO.equals(nivel)) {
            return 1;
        }
        if (ALERTA.equals(nivel)) {
            return 2;
        }
        if (CRITICO.equals(nivel)) {
            return 3;
        }
        throw new DadosInvalidosException(String.format("Nível de alerta desconhecido: %s.", nivel));
    }

    public static boolean geraAlerta(String nivel) {
        return gravidade(nivel) > 0;
    }

    public static String acaoRecomendada(String nivel) {
        int gravidade = gravidade(nivel);
        if (gravidade == 3) {
            return "Acionar a Defesa Civil (199), abrir o ponto de apoio comunitário e orientar a saída das áreas de risco.";
        }
        if (gravidade == 2) {
            return "Limpar bocas de lobo, retirar objetos das margens e revisar as rotas de saída das áreas baixas.";
        }
        if (gravidade == 1) {
            return "Acompanhar os sensores de perto e avisar os agentes comunitários do bairro.";
        }
        return "Nenhuma ação extra: manter o monitoramento de rotina.";
    }
}
