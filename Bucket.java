package trabalho_aed_prontuario;

public class Bucket<T> {
    // Cada bucket contém o valor de profundidade local (p’) seguido por n entradas
    private int profundidade_local;
    private int n; // quantidade máxima de registros do bucket
    private int ocupacao;

    public Bucket(int n) {
        this.n = n;
        profundidade_local = 0;
        ocupacao = 0;
    }

    public Bucket(int n, int profundidade_local) {
        this.n = n;
        this.profundidade_local = profundidade_local;
        ocupacao = 0;
    }

    public int getProfundidadeLocal() {
        return profundidade_local;
    }

    public void setProfundidadeLocal(int profundidade_local) {
        this.profundidade_local = profundidade_local;
    }

    public int getN() {
        return n;
    }

    public int getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(int ocupacao) {
        this.ocupacao = ocupacao;
    }
}
