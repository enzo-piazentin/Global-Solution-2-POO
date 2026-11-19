package br.com.fiap.territory.excecao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FonteIndisponivelExceptionTest {

    @Test
    public void deveCriarExcecaoComMensagem() {
        String mensagem = "Sensor não respondeu";
        FonteIndisponivelException excecao = new FonteIndisponivelException(mensagem);

        assertEquals(mensagem, excecao.getMessage());
    }

    @Test
    public void deveSerRuntimeException() {
        FonteIndisponivelException excecao = new FonteIndisponivelException("Teste");

        assertTrue(excecao instanceof RuntimeException);
    }

    @Test
    public void devePoderSerLancadaECapturada() {
        String mensagem = "Sensor em manutenção";

        FonteIndisponivelException excecao = assertThrows(
                FonteIndisponivelException.class,
                () -> lancarExcecao(mensagem)
        );

        assertEquals(mensagem, excecao.getMessage());
    }

    private void lancarExcecao(String mensagem) {
        throw new FonteIndisponivelException(mensagem);
    }
}
