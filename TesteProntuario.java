package trabalho_aed_prontuario;

import java.time.LocalDate;

public class TesteProntuario {
    public static void main(String[] args) {
        Prontuario p = new Prontuario("fulano", LocalDate.now(), 'm', "hello world!");
        // System.out.println( p.toByteArray() );
    }
}
