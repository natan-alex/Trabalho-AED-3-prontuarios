package trabalho_aed_prontuario.main;

import trabalho_aed_prontuario.mestre.Prontuario;

import java.time.LocalDate;

public class TesteControlador {
    public static void main(String[] args) {
        Controlador controlador = new Controlador(2, 4, 20);
        Prontuario p = new Prontuario(10, "shulambs", LocalDate.now().minusMonths(5), 'f', (short)20, "anotacoes de um bom medico");
        controlador.inserirRegistro(p);
        controlador.editarRegistro(10, 1, "shulambs de nome novo");
        controlador.imprimirArquivos();
    }
}
