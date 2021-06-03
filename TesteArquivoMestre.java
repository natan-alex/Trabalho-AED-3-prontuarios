package trabalho_aed_prontuario.mestre;

import trabalho_aed_prontuario.indice.*;
import java.time.LocalDate;

public class TesteArquivoMestre {
    public static void main(String[] args) {
        try {
            int[] cpfs = {10,3,14,18,20,8,6,1,12,22,7,16,13,19};
            Prontuario prontuario;
            short tamAnotacoes = (short) 50;
            ArquivoMestre mestre = new ArquivoMestre("arquivo_mestre.db", tamAnotacoes);
            int cont = 1;
            Indice indice = new Indice("indice.db", "diretorio.db",1, 4);
            int numRegistro;

            for (int cpf : cpfs) {
                prontuario = new Prontuario(cpf, "Nome" + cpf, LocalDate.now(), 'm', tamAnotacoes, "blablabla");
                numRegistro = mestre.inserirRegistro(prontuario);
                System.out.println("Número do registro: " + numRegistro);
                indice.inserirRegistro(cpf, numRegistro);
                System.out.println("Posição do registro: " + mestre.calcularPosicaoDoRegistro(cont++));
            }

            // testar edição
            int cpf_editar = 8;
            numRegistro = indice.getNumRegistro(cpf_editar);
            Prontuario atual = mestre.recuperarRegistro(numRegistro);
            Prontuario alterado = new Prontuario(cpf_editar, "shulambs", LocalDate.now().minusMonths(5), 'f', tamAnotacoes, "anotacoes de um bom medico");
            // mestre.editarRegistro(numRegistro, alterado);
            System.out.println("Novo registro: " + mestre.recuperarRegistro(numRegistro));

            System.out.println();
            mestre.imprimirArquivo();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

