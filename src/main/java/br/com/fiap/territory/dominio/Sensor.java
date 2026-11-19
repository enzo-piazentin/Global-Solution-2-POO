package br.com.fiap.territory.dominio;

/**
 * Contrato de qualquer sensor do território: ler um valor, informar se está funcionando e dizer o que mede.
 */
public interface Sensor {

    double lerValor();

    boolean verificarFuncionamento();

    String getTipo();

    String getCodigo();

    String getUnidade();

    double getValorMaximo();

    void definirAtivo(boolean ativo);
}
