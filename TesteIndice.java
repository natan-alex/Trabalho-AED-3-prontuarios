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

            ArquivoMestre mestre = new ArquivoMestre("arquivo_mestre.db", tamAnotacoes);
            Indice indice = new Indice("indice.db", "diretorio.db", profundidade, tamBuckets);
            Prontuario prontuario;
            int numRegistro = 0;

            int[] cpfs = {10,3,14,18,20,8,6,1,12,22,7,16,13,19};
            for (int cpf : cpfs) {
                prontuario = new Prontuario(cpf, "Nome" + cpf, LocalDate.now(), 'm', "blablabla");
                numRegistro = mestre.inserirRegistro(prontuario);
                indice.inserirRegistro(cpf, numRegistro);
            }

            // problemáticos: 8,22,19
            // diretorio: p=3
            // 000=0->1
            // 001=1->2
            // 010=2->3
            // 011=3->2
            // 100=4->1
            // 101=5->5
            // 110=6->4
            // 111=7->5
            // p=2 bucket 1: 20,8,12,16
            // p=2 bucket 2: 3,1,19
            // p=3 bucket 3: 10,18,
            // p=3 bucket 4: 14,6,22,
            // p=3 bucket 5: 7,13

//            Bucket bucket1 = indice.getBucketDoArquivoDeIndice(1);
//            System.out.println("Lendo do bucket 1:");
//            for (RegistroDoBucket registro : bucket1.getRegistrosDoBucket()) {
//                    System.out.println("registro: " + registro);
//            }
//
//            Bucket bucket2 = indice.getBucketDoArquivoDeIndice(2);
//            System.out.println("Lendo do bucket 2:");
//            for (RegistroDoBucket registro : bucket2.getRegistrosDoBucket()) {
//                    System.out.println("registro: " + registro);
//            }
//
//            Bucket bucket3 = indice.getBucketDoArquivoDeIndice(3);
//            System.out.println("Lendo do bucket 3:");
//            for (RegistroDoBucket registro : bucket3.getRegistrosDoBucket()) {
//                    System.out.println("registro: " + registro);
//            }
//
//            Bucket bucket4 = indice.getBucketDoArquivoDeIndice(4);
//            System.out.println("Lendo do bucket 4:");
//            for (RegistroDoBucket registro : bucket4.getRegistrosDoBucket()) {
//                    System.out.println("registro: " + registro);
//            }
//
//            Bucket bucket5 = indice.getBucketDoArquivoDeIndice(5);
//            System.out.println("Lendo do bucket 5:");
//            for (RegistroDoBucket registro : bucket5.getRegistrosDoBucket()) {
//                    System.out.println("registro: " + registro);
//            }
            int cpf = 22;
            System.out.println("NumRegistro associado ao cpf " + cpf + " : " + indice.getNumRegistro(cpf));
            cpf = 15;
            System.out.println("NumRegistro associado ao cpf " + cpf + " : " + indice.getNumRegistro(cpf));
            cpf = 14;
            System.out.println("NumRegistro associado ao cpf " + cpf + " : " + indice.getNumRegistro(cpf));

            indice.imprimirArquivo();
            System.out.println("\n");
        }
}
