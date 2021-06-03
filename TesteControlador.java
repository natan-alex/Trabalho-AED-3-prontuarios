package trabalho_aed_prontuario.main;

import trabalho_aed_prontuario.mestre.Prontuario;

import java.time.LocalDate;

public class TesteControlador {
    public static void main(String[] args) {
        Controlador controlador = new Controlador();
        controlador.criarArquivos(1, 4, 8);
        Prontuario p = new Prontuario(10, "shulambs", LocalDate.now().minusMonths(5), 'f', (short)20, "anotacoes de um bom medico");
        controlador.inserirRegistro(p);
        System.out.println(controlador.editarRegistro(10, 5, "shulambs de nome novo"));
        controlador.imprimirArquivos();
    }
}
