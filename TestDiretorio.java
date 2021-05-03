package trabalho_aed_prontuario;

import java.io.*;

public class TestDiretorio {
    public static void main(String[] args) {
        Diretorio d = new Diretorio(0, "diretorio.db");
        d.setCabecalho();

        d.duplicar();
        d.reorganizar(0, 1);

        d.duplicar();
        d.reorganizar(0, 2);

        d.duplicar();
        d.reorganizar(2, 3);

        d.reorganizar(1, 2);

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
