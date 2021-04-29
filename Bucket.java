package trabalho_aed_prontuario;

import java.util.ArrayList;

public class Bucket {
    // Cada bucket contém o valor de profundidade local (p’) seguido por n entradas
    private int profundidade_local;
    private int n; // quantidade máxima de registros do bucket
    private int ocupacao; // quantidade atual de registros do bucket
    private ArrayList<RegistroDoIndice<Integer>> registros;

    public Bucket(int profundidade_local, int n) {
        this.profundidade_local = profundidade_local;
        this.n = n;
        ocupacao = 0;
        registros = new ArrayList<RegistroDoIndice<Integer>>(n);
    }
}
