package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LimiarTest {

    @Test
    public void deveCriarLimiarValido() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        assertEquals(10.0, limiar.getAtencao(), 0.001);
        assertEquals(25.0, limiar.getAlerta(), 0.001);
        assertEquals(50.0, limiar.getCritico(), 0.001);
    }

    @Test
    public void deveLancarExcecaoQuandoAtencaoZero() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new Limiar(0.0, 25.0, 50.0)
        );

        assertTrue(excecao.getMessage().contains("maiores que zero"));
    }

    @Test
    public void deveLancarExcecaoQuandoAlertaZero() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new Limiar(10.0, 0.0, 50.0)
        );

        assertTrue(excecao.getMessage().contains("maiores que zero"));
    }

    @Test
    public void deveLancarExcecaoQuandoCriticoZero() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new Limiar(10.0, 25.0, 0.0)
        );

        assertTrue(excecao.getMessage().contains("maiores que zero"));
    }

    @Test
    public void deveLancarExcecaoQuandoValoresNegativos() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new Limiar(-10.0, -25.0, -50.0)
        );

        assertTrue(excecao.getMessage().contains("maiores que zero"));
    }

    @Test
    public void deveLancarExcecaoQuandoAtencaoIgualAlerta() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new Limiar(25.0, 25.0, 50.0)
        );

        assertTrue(excecao.getMessage().contains("ATENÇÃO < ALERTA < CRÍTICO"));
    }

    @Test
    public void deveLancarExcecaoQuandoAlertaIgualCritico() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new Limiar(10.0, 50.0, 50.0)
        );

        assertTrue(excecao.getMessage().contains("ATENÇÃO < ALERTA < CRÍTICO"));
    }

    @Test
    public void deveLancarExcecaoQuandoAtencaoMaiorQueAlerta() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new Limiar(30.0, 25.0, 50.0)
        );

        assertTrue(excecao.getMessage().contains("ATENÇÃO < ALERTA < CRÍTICO"));
    }

    @Test
    public void deveLancarExcecaoQuandoAlertaMaiorQueCritico() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> new Limiar(10.0, 60.0, 50.0)
        );

        assertTrue(excecao.getMessage().contains("ATENÇÃO < ALERTA < CRÍTICO"));
    }

    @Test
    public void deveClassificarComoNormal() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        String nivel = limiar.classificar(5.0);

        assertEquals(NivelAlerta.NORMAL, nivel);
    }

    @Test
    public void deveClassificarComoAtencao() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        String nivel = limiar.classificar(15.0);

        assertEquals(NivelAlerta.ATENCAO, nivel);
    }

    @Test
    public void deveClassificarComoAlerta() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        String nivel = limiar.classificar(30.0);

        assertEquals(NivelAlerta.ALERTA, nivel);
    }

    @Test
    public void deveClassificarComoCritico() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        String nivel = limiar.classificar(60.0);

        assertEquals(NivelAlerta.CRITICO, nivel);
    }

    @Test
    public void deveClassificarNoLimiteDeAtencao() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        String nivel = limiar.classificar(10.0);

        assertEquals(NivelAlerta.ATENCAO, nivel);
    }

    @Test
    public void deveClassificarNoLimiteDeAlerta() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        String nivel = limiar.classificar(25.0);

        assertEquals(NivelAlerta.ALERTA, nivel);
    }

    @Test
    public void deveClassificarNoLimiteDeCritico() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        String nivel = limiar.classificar(50.0);

        assertEquals(NivelAlerta.CRITICO, nivel);
    }

    @Test
    public void deveRetornarLimiteDoNivelNormal() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        double limite = limiar.limiteDoNivel(NivelAlerta.NORMAL);

        assertEquals(10.0, limite, 0.001);
    }

    @Test
    public void deveRetornarLimiteDoNivelAtencao() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        double limite = limiar.limiteDoNivel(NivelAlerta.ATENCAO);

        assertEquals(10.0, limite, 0.001);
    }

    @Test
    public void deveRetornarLimiteDoNivelAlerta() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        double limite = limiar.limiteDoNivel(NivelAlerta.ALERTA);

        assertEquals(25.0, limite, 0.001);
    }

    @Test
    public void deveRetornarLimiteDoNivelCritico() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        double limite = limiar.limiteDoNivel(NivelAlerta.CRITICO);

        assertEquals(50.0, limite, 0.001);
    }

    @Test
    public void deveValidarLimiarParaSensor() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        limiar.validarParaSensor(100.0);
    }

    @Test
    public void deveLancarExcecaoQuandoCriticoMaiorQueMaximoSensor() {
        Limiar limiar = new Limiar(10.0, 25.0, 50.0);

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> limiar.validarParaSensor(40.0)
        );

        assertTrue(excecao.getMessage().contains("maior que o máximo que o sensor consegue medir"));
    }
}
