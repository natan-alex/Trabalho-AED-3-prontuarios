package trabalho_aed_prontuario;

public class RegistroDoBucket<T> {
    // Cada bucket contém o valor de profundidade local (p’) seguido por n entradas, as quais 
    // são constituídas por um CPF e o número do registro no arquivo-mestre onde o registro 
    // correspondente ao CPF se encontra (começando por 0).
    private T chave;
    private int num_registro;
    private boolean isLapide;

    public RegistroDoBucket(T chave) {
        this.chave = chave;
        num_registro = -1;
        isLapide = false;
    }

    public RegistroDoBucket(T chave, int num_registro) {
        this.chave = chave;
        this.num_registro = num_registro;
        isLapide = false;
    }

    public T getChave() {
        return chave;
    }

    public void setChave(T chave) {
        this.chave = chave;
    }

    public int getNumRegistro() {
        return num_registro;
    }

    public void setNumRegistro(int num_registro) {
        this.num_registro = num_registro;
    }

    public boolean getIsLapide() {
        return isLapide;
    }

    public void setIsLapide(boolean isLapide) {
        this.isLapide = isLapide;
    }
}