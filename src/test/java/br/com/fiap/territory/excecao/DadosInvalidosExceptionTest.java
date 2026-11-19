package br.com.fiap.territory.excecao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DadosInvalidosExceptionTest {

    @Test
    public void deveCriarExcecaoComMensagem() {
        String mensagem = "Dados inválidos fornecidos";
        DadosInvalidosException excecao = new DadosInvalidosException(mensagem);

        assertEquals(mensagem, excecao.getMessage());
    }

    @Test
    public void deveSerRuntimeException() {
        DadosInvalidosException excecao = new DadosInvalidosException("Teste");

        assertTrue(excecao instanceof RuntimeException);
    }

    @Test
    public void devePoderSerLancadaECapturada() {
        String mensagem = "Valor negativo não permitido";

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> lancarExcecao(mensagem)
        );

        assertEquals(mensagem, excecao.getMessage());
    }

    private void lancarExcecao(String mensagem) {
        throw new DadosInvalidosException(mensagem);
    }
}
