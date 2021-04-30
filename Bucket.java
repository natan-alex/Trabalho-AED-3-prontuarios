package trabalho_aed_prontuario;

import java.util.ArrayList;

public class Bucket<T> {
    // Cada bucket contém o valor de profundidade local (p’) seguido por n entradas
    private int profundidade_local;
    private int n; // quantidade máxima de registros do bucket
    private ArrayList<RegistroDoBucket<T>> registros;

    public Bucket(int n) {
        this.n = n;
        profundidade_local = 0;
        registros = new ArrayList<RegistroDoBucket<T>>(n);
    }

    public Bucket(int n, int profundidade_local) {
        this.n = n;
        this.profundidade_local = profundidade_local;
        registros = new ArrayList<RegistroDoBucket<T>>(n);
    }

    public boolean adicionarRegistro(RegistroDoBucket<T> registro) {
        // capacidade total do bucket já ocupada
        if (registros.size() == n) {
            return false;
        }

        registros.add(registro);
        return true;
    }

    public boolean removerRegistro(T chave_do_registro) {
        for (RegistroDoBucket<T> registro : registros) {
            if (registro.getChave() == chave_do_registro) {
                registro.setIsLapide(true);
                return true;
            }
        }
        return false;
    }
}
