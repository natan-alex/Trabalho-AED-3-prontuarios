package trabalho_aed_prontuario.mestre;

import java.time.LocalDate;

public class TesteProntuario {
    public static void main(String[] args) {
        Prontuario p = new Prontuario(1, "fulano", LocalDate.now(), 'm', (short) 20, "hello world!");
        Prontuario p2 = new Prontuario(2, "ciclano", LocalDate.now(), 'm', (short) 10, "teste");
        p2.fromByteArray( p.toByteArray() );
        System.out.println(p2);
        // System.out.println( p.toByteArray() );
    }
}
