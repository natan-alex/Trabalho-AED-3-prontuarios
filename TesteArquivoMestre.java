package trabalho_aed_prontuario.mestre;

import trabalho_aed_prontuario.indice.*;
import java.time.LocalDate;

public class TesteArquivoMestre {
    public static void main(String[] args) {
        try {
            int[] cpfs = {10,3,14,18,20,8,6,1,12,22,7,16,13,19};
            Prontuario prontuario;
            short tamAnotacoes = (short) 50;
            ArquivoMestre mestre = new ArquivoMestre(tamAnotacoes);
            int cont = 1;
            Indice indice = new Indice(1, 4);
            int numRegistro;

            for (int cpf : cpfs) {
                prontuario = new Prontuario(cpf, "Nome" + cpf, LocalDate.now(), 'm', tamAnotacoes, "blablabla");
                numRegistro = mestre.inserirRegistro(prontuario);
                System.out.println("Número do registro: " + numRegistro);
                indice.inserirRegistro(cpf, numRegistro);
                System.out.println("Posição do registro: " + mestre.calcularPosicaoDoRegistro(cont++));
            }



            Prontuario[] prontuarios = new Prontuario[cpfs.length];
            for (int i = 0; i < cpfs.length; i++) {
                prontuarios[i] = mestre.recuperarRegistro(i + 1);
                System.out.println( (i+1) + ": " + prontuarios[i]);
            }

            mestre.imprimirArquivo();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

