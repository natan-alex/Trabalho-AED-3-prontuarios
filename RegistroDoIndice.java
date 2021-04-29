package trabalho_aed_prontuario;

public class RegistroDoIndice<T> {
    // Cada bucket contém o valor de profundidade local (p’) seguido por n entradas, as quais 
    // são constituídas por um CPF e o número do registro no arquivo-mestre onde o registro 
    // correspondente ao CPF se encontra (começando por 0).
    private T chave;
    private int num_registro;

    public RegistroDoIndice(T chave, int num_registro) {
        this.chave = chave;
        this.num_registro = num_registro;
    }
}
