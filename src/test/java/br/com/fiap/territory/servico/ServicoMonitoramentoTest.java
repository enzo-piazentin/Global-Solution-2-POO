package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Alerta;
import br.com.fiap.territory.dominio.DadosTerritorio;
import br.com.fiap.territory.dominio.Limiar;
import br.com.fiap.territory.dominio.Medicao;
import br.com.fiap.territory.dominio.MedicaoBuilder;
import br.com.fiap.territory.dominio.NivelAlerta;
import br.com.fiap.territory.dominio.PontoMonitoramento;
import br.com.fiap.territory.dominio.Sensor;
import br.com.fiap.territory.dominio.SensorChuva;
import br.com.fiap.territory.excecao.AcessoNegadoException;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import br.com.fiap.territory.persistencia.AlertaRepository;
import br.com.fiap.territory.persistencia.MedicaoRepository;
import br.com.fiap.territory.servico.ConfiguracaoSistema;
import br.com.fiap.territory.servico.FabricaPrevisao;
import br.com.fiap.territory.servico.ServicoMonitoramento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServicoMonitoramentoTest {

    private static final String CODIGO_SENSOR = "CHV-T1";
    private static final String REGIAO = "Brasilândia";

    @Mock
    private MedicaoRepository medicaoRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @Spy
    private FabricaPrevisao fabricaPrevisao = new FabricaPrevisao();

    @InjectMocks
    private ServicoMonitoramento servico;
    private Sensor sensorChuva;
    private PontoMonitoramento ponto;

    @BeforeEach
    public void setUp() {
        ConfiguracaoSistema.getInstancia().restaurarLimiaresPadrao();
        sensorChuva = new SensorChuva(CODIGO_SENSOR);
        ponto = criarPonto(sensorChuva);
    }

    @AfterEach
    public void tearDown() {
        ConfiguracaoSistema.getInstancia().restaurarLimiaresPadrao();
    }

    @Test
    public void deveEmitirAlertaCriticoQuandoPrevisaoUltrapassaLimiteCritico() {
        simularLeiturasRecentes(60.0, 60.0, 60.0, 60.0, 60.0, 60.0);
        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);

        String relatorio = servico.analisarSensor(ponto, sensorChuva);

        verify(alertaRepository).save(captor.capture());
        assertEquals(NivelAlerta.CRITICO, captor.getValue().getNivel());
        assertEquals(50.0, captor.getValue().getLimiteUltrapassado(), 0.001);
        assertEquals(REGIAO, captor.getValue().getRegiao());
        assertTrue(relatorio.contains("NOVO ALERTA GRAVADO"));
    }

    @Test
    public void deveEscolherONivelMaisGraveEntreAsEstrategias() {
        simularLeiturasRecentes(5.0, 5.0, 5.0, 20.0, 20.0, 20.0);
        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);

        servico.analisarSensor(ponto, sensorChuva);

        verify(alertaRepository).save(captor.capture());
        assertEquals(NivelAlerta.ALERTA, captor.getValue().getNivel());
        assertEquals("Comparação com período anterior", captor.getValue().getEstrategia());
        assertEquals(35.0, captor.getValue().getValorPrevisto(), 0.001);
    }

    @Test
    public void naoDeveDuplicarAlertaJaEmitidoRecentemente() {
        simularLeiturasRecentes(60.0, 60.0, 60.0, 60.0, 60.0, 60.0);
        when(alertaRepository.existsBySensorCodigoAndNivelAndDataHoraGreaterThanEqual(
                eq(CODIGO_SENSOR), eq(NivelAlerta.CRITICO), any(LocalDateTime.class)))
                .thenReturn(true);

        String relatorio = servico.analisarSensor(ponto, sensorChuva);

        verify(alertaRepository, never()).save(any(Alerta.class));
        assertTrue(relatorio.contains("nada novo foi gravado"));
    }

    @Test
    public void naoDeveEmitirAlertaQuandoPrevisaoAbaixoDosLimiares() {
        simularLeiturasRecentes(2.0, 3.0, 2.0, 3.0, 2.0, 3.0);

        String relatorio = servico.analisarSensor(ponto, sensorChuva);

        verifyNoInteractions(alertaRepository);
        assertTrue(relatorio.contains("NORMAL"));
    }

    @Test
    public void deveSeCalarQuandoHaMenosDeCincoLeiturasRecentes() {
        simularLeiturasRecentes(80.0, 80.0, 80.0);

        String relatorio = servico.analisarSensor(ponto, sensorChuva);

        verifyNoInteractions(alertaRepository);
        assertTrue(relatorio.contains("O sistema se calou"));
    }

    @Test
    public void deveGravarMedicaoValidaComARegiaoDoPonto() {
        Sensor sensor = criarSensorFalso(12.5);
        when(sensor.getTipo()).thenReturn(SensorChuva.TIPO);
        PontoMonitoramento pontoDoSensor = criarPonto(sensor);
        ArgumentCaptor<Medicao> captor = ArgumentCaptor.forClass(Medicao.class);

        servico.registrarLeitura(pontoDoSensor, sensor);

        verify(medicaoRepository).save(captor.capture());
        assertEquals(12.5, captor.getValue().getValor(), 0.001);
        assertEquals(REGIAO, captor.getValue().getRegiao());
    }

    @Test
    public void deveLancarDadosInvalidosENaoGravarQuandoLeituraForaDaFaixa() {
        Sensor sensor = criarSensorFalso(200.0);
        PontoMonitoramento pontoDoSensor = criarPonto(sensor);

        assertThrows(DadosInvalidosException.class, () -> servico.registrarLeitura(pontoDoSensor, sensor));

        verify(medicaoRepository, never()).save(any(Medicao.class));
    }

    @Test
    public void deveLancarFonteIndisponivelQuandoSensorEstaEmManutencao() {
        sensorChuva.definirAtivo(false);

        FonteIndisponivelException excecao = assertThrows(
                FonteIndisponivelException.class,
                () -> servico.registrarLeitura(ponto, sensorChuva)
        );

        assertTrue(excecao.getMessage().contains("manutenção"));
        verifyNoInteractions(medicaoRepository);
    }

    @Test
    public void deveBloquearAlteracaoDeLimiarComCodigoDeAcessoErrado() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia de teste", "Zona Norte", 1000, "TESTE-123");

        assertThrows(AcessoNegadoException.class,
                () -> servico.alterarLimiares(territorio, "0000", sensorChuva, new Limiar(15.0, 30.0, 60.0)));

        assertEquals(50.0, ConfiguracaoSistema.getInstancia().getLimiar(SensorChuva.TIPO).getCritico(), 0.001);
    }

    @Test
    public void deveRejeitarLimiaresForaDeOrdem() {
        DadosTerritorio territorio = new DadosTerritorio("Bacia de teste", "Zona Norte", 1000, "TESTE-123");

        assertThrows(DadosInvalidosException.class,
                () -> servico.alterarLimiares(territorio, "TESTE-123", sensorChuva, new Limiar(40.0, 30.0, 60.0)));

        assertEquals(10.0, ConfiguracaoSistema.getInstancia().getLimiar(SensorChuva.TIPO).getAtencao(), 0.001);
    }

    private void simularLeiturasRecentes(double... valores) {
        List<Medicao> historico = new ArrayList<>();
        for (double valor : valores) {
            historico.add(MedicaoBuilder.builder()
                    .comSensor(CODIGO_SENSOR, SensorChuva.TIPO)
                    .comRegiao(REGIAO)
                    .comValor(valor, "mm/h")
                    .build());
        }
        when(medicaoRepository.findBySensorCodigoAndDataHoraGreaterThanEqualOrderByIdAsc(
                eq(CODIGO_SENSOR), any(LocalDateTime.class)))
                .thenReturn(historico);
    }

    private Sensor criarSensorFalso(double valorLido) {
        Sensor sensor = mock(Sensor.class);
        when(sensor.getCodigo()).thenReturn(CODIGO_SENSOR);
        when(sensor.verificarFuncionamento()).thenReturn(true);
        when(sensor.lerValor()).thenReturn(valorLido);
        when(sensor.getValorMaximo()).thenReturn(150.0);
        when(sensor.getUnidade()).thenReturn("mm/h");
        return sensor;
    }

    private PontoMonitoramento criarPonto(Sensor sensor) {
        List<Sensor> sensores = new ArrayList<>();
        sensores.add(sensor);
        return new PontoMonitoramento("PM-T1", "Ponto de teste", REGIAO, sensores);
    }
}
