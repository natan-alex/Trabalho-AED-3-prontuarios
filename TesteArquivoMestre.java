package trabalho_aed_prontuario.mestre;

import trabalho_aed_prontuario.*;

import java.time.LocalDate;

public class TesteArquivoMestre {
    public static void main(String[] args) {
        ArquivoMestre arq = new ArquivoMestre((short) 100);
        Prontuario p = new Prontuario("fulano", LocalDate.now(), 'm', (short) 50);
    }
}

