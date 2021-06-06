package trabalho_aed_prontuario.mestre;

import java.time.LocalDate;

public class TesteProntuario {
    public static void main(String[] args) {
        Prontuario p = new Prontuario(1, "fulano", LocalDate.now(), 'm', "hello world!");
        Prontuario p2 = new Prontuario(2, "ciclano", LocalDate.now(), 'm', "teste");
        p2.fromByteArray( p.toByteArray() );
        System.out.println(p2);
        // System.out.println( p.toByteArray() );
    }
}
