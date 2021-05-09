package trabalho_aed_prontuario;

import trabalho_aed_prontuario.diretorio.*;

import java.io.*;

public class TestDiretorio {
    public static void main(String[] args) {
        Diretorio d;

        d = new Diretorio(0, "diretorio.db");

        // d.duplicar();

        // d = new Diretorio("diretorio.db");
        // d.carregarArquivo();
        // d.reorganizar(0, 1, 1);

        // d.duplicar();
        // d.reorganizar(0, 4, 2);

        // d.duplicar();
        // d.reorganizar(0, 100, 3);

        // d.duplicar();
        // d.reorganizar(2, 3);

        // d.reorganizar(1, 2);
        /*
          DIRETORIO
          0
          1
          ============
          DIRETORIO
          0
          1
          2
          1
          ============
          DIRETORIO
          0
          1
          2
          1
          0
          1
          6             |> ver com duplicação dos buckets está acontecendo
          1
          ============
          DIRETORIO
          0
          1
          2
          3
          0
          1
          6
          3
          ============
        */
    }
}
