package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MedicaoTest {

    @Test
    public void deveCriarMedicaoValidaComDataHora() {
        MedicaoBuilder builder = MedicaoBuilder.builder()
                .comSensor("CHV-01", "CHUVA")
                .comRegiao("Brasilândia")
                .comValor(12.5, "mm/h");

        Medicao medicao = builder.build();

        assertEquals("CHV-01", medicao.getSensorCodigo());
        assertEquals(12.5, medicao.getValor(), 0.001);
        assertNotNull(medicao.getDataHora());
    }

    @Test
    public void deveLancarExcecaoQuandoChuvaNegativa() {
        MedicaoBuilder builder = MedicaoBuilder.builder()
                .comSensor("CHV-01", "CHUVA")
                .comRegiao("Brasilândia")
                .comValor(-3.0, "mm/h");

        DadosInvalidosException excecao = assertThrows(DadosInvalidosException.class, () -> builder.build());

        assertTrue(excecao.getMessage().contains("negativa"));
    }

    @Test
    public void deveLancarExcecaoQuandoValorNaoEhNumero() {
        MedicaoBuilder builder = MedicaoBuilder.builder()
                .comSensor("NIV-01", "NIVEL_RIO")
                .comRegiao("Brasilândia")
                .comValor(Double.NaN, "m");

        DadosInvalidosException excecao = assertThrows(DadosInvalidosException.class, () -> builder.build());

        assertEquals("Medição do sensor NIV-01 não é um número válido.", excecao.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoCodigoDoSensorVazio() {
        MedicaoBuilder builder = MedicaoBuilder.builder()
                .comSensor(" ", "CHUVA")
                .comRegiao("Brasilândia")
                .comValor(4.0, "mm/h");

        DadosInvalidosException excecao = assertThrows(DadosInvalidosException.class, () -> builder.build());

        assertEquals("Medição sem código de sensor não pode ser registrada.", excecao.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoBuilderNaoRecebeRegiao() {
        MedicaoBuilder builder = MedicaoBuilder.builder()
                .comSensor("UMI-01", "UMIDADE_SOLO")
                .comValor(60.0, "%");

        DadosInvalidosException excecao = assertThrows(DadosInvalidosException.class, () -> builder.build());

        assertEquals("Medição do sensor UMI-01 sem região informada.", excecao.getMessage());
    }
}
