package trabalho_aed_prontuario.indice;

import java.io.RandomAccessFile;
import java.io.IOException;

public class TesteIndice {
        private static int N = 0;

        public static void main(String[] args) {
            int tamBuckets = 4;
            int profundidade = 1;
            Indice indice = new Indice(profundidade, tamBuckets);

            int[] cpfs = {10,3,14,18,20,8,6,1,12,22,7,16,13,19};
            for (int cpf : cpfs) {
                indice.inserirRegistro(cpf, ++N);
            }
            // problemáticos: 8,22,19
            // diretorio: p=3
            // 000=0->1
            // 001=1->2
            // 010=2->3
            // 011=3->2
            // 100=4->1
            // 101=5->2
            // 110=6->4
            // 111=7->5
            // p=2 bucket 1: 20,8,12,16
            // p=2 bucket 2: 3,1,19
            // p=3 bucket 3: 10,18,
            // p=3 bucket 4: 14,6,22,
            // p=3 bucket 5: 7,13

            Bucket bucket1 = indice.getBucketDoArquivoDeIndice(12);
            System.out.println("Lendo do bucket 1:");
            for (RegistroDoBucket registro : bucket1.getRegistrosDoBucket()) {
                    System.out.println("registro: " + registro);
            }

            Bucket bucket2 = indice.getBucketDoArquivoDeIndice(56);
            System.out.println("Lendo do bucket 2:");
            for (RegistroDoBucket registro : bucket2.getRegistrosDoBucket()) {
                    System.out.println("registro: " + registro);
            }

            Bucket bucket3 = indice.getBucketDoArquivoDeIndice(100);
            System.out.println("Lendo do bucket 3:");
            for (RegistroDoBucket registro : bucket3.getRegistrosDoBucket()) {
                    System.out.println("registro: " + registro);
            }

            Bucket bucket4 = indice.getBucketDoArquivoDeIndice(144);
            System.out.println("Lendo do bucket 4:");
            for (RegistroDoBucket registro : bucket4.getRegistrosDoBucket()) {
                    System.out.println("registro: " + registro);
            }

            Bucket bucket5 = indice.getBucketDoArquivoDeIndice(188);
            System.out.println("Lendo do bucket 5:");
            for (RegistroDoBucket registro : bucket5.getRegistrosDoBucket()) {
                    System.out.println("registro: " + registro);
            }

            indice.mostrarDiretorio();
    }
}
