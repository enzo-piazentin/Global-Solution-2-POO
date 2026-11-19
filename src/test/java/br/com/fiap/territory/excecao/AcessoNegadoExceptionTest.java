package br.com.fiap.territory.excecao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AcessoNegadoExceptionTest {

    @Test
    public void deveCriarExcecaoComMensagem() {
        String mensagem = "Código de acesso inválido";
        AcessoNegadoException excecao = new AcessoNegadoException(mensagem);

        assertEquals(mensagem, excecao.getMessage());
    }

    @Test
    public void deveSerRuntimeException() {
        AcessoNegadoException excecao = new AcessoNegadoException("Teste");

        assertTrue(excecao instanceof RuntimeException);
    }

    @Test
    public void devePoderSerLancadaECapturada() {
        String mensagem = "Acesso negado: permissão insuficiente";

        AcessoNegadoException excecao = assertThrows(
                AcessoNegadoException.class,
                () -> lancarExcecao(mensagem)
        );

        assertEquals(mensagem, excecao.getMessage());
    }

    private void lancarExcecao(String mensagem) {
        throw new AcessoNegadoException(mensagem);
    }
}
