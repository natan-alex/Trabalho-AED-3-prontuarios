package trabalho_aed_prontuario;

import java.io.*;

public class TestDiretorio {
    public static void main(String[] args) {
        Diretorio d = new Diretorio(1, "diretorio.db");
        d.setCabecalho();

        System.out.println(d.getPaginaIndice(1));

    }
}
