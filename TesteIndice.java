package trabalho_aed_prontuario.indice;

import trabalho_aed_prontuario.mestre.*;
import java.time.LocalDate;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;

public class TesteIndice {
        private static int N = 0;

        public static void main(String[] args) {
            int tamBuckets = 4;
            int profundidade = 1;
            short tamAnotacoes = 10;

            ArquivoMestre mestre = new ArquivoMestre(tamAnotacoes);
            Indice indice = new Indice(profundidade, tamBuckets);
            Prontuario prontuario;
            int numRegistro = 0;

            int[] cpfs = {10,3,14,18,20,8,6,1,12,22,7,16,13,19};
            for (int cpf : cpfs) {
                prontuario = new Prontuario(cpf, "Nome" + cpf, LocalDate.now(), 'm', tamAnotacoes, "blablabla");
                numRegistro = mestre.inserirRegistro(prontuario);
                indice.inserirRegistro(cpf, numRegistro);
            }

            numRegistro = indice.getNumRegistro(3);
            indice.removerRegistro(3);
            mestre.removerRegistro(numRegistro);

            numRegistro = indice.getNumRegistro(13);
            indice.removerRegistro(13);
            mestre.removerRegistro(numRegistro);

            numRegistro = indice.getNumRegistro(22);
            indice.removerRegistro(22);
            mestre.removerRegistro(numRegistro);

            prontuario = new Prontuario(9, "NomeNEW" + 9, LocalDate.now(), 'm', tamAnotacoes, "blablabla");
            numRegistro = mestre.inserirRegistro(prontuario);
            indice.inserirRegistro(9, numRegistro);

            prontuario = new Prontuario(66, "Nome" + 66, LocalDate.now(), 'm', tamAnotacoes, "blablabla");
            numRegistro = mestre.inserirRegistro(prontuario);
            indice.inserirRegistro(66, numRegistro);


            indice.imprimirArquivo();
            mestre.imprimirArquivo();
            System.out.println("\n");
        }
}
