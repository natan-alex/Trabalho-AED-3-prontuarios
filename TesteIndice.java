package trabalho_aed_prontuario.indice;

import trabalho_aed_prontuario.diretorio.Diretorio;

import java.io.RandomAccessFile;
import java.io.IOException;

public class TesteIndice {
    private static int N = 0;

    public static void main(String[] args) {
            int tamBuckets = 4;
            int profundidade = 1;
            Indice indice = new Indice(profundidade, tamBuckets); // esperado: arquivo de indices tenha sido
            Diretorio diretorio = new Diretorio(profundidade, tamBuckets, indice); // esperado: arquivo de indices tenha sido

            // criado com o número de buckets inicial baseado no tamanho do diretório
            // ou na profundidade global
            // params: pglobal, tamanho do bucket
            int cpf;

            cpf = 10;
            inserir(cpf, indice, diretorio);

            cpf = 3;
            inserir(cpf, indice, diretorio);

            cpf = 14;
            inserir(cpf, indice, diretorio);

            cpf = 18;
            inserir(cpf, indice, diretorio);

            cpf = 20;
            inserir(cpf, indice, diretorio);

            // cpf = 8;
            // inserir(cpf, indice, diretorio);

            // cpf = 6;
            // inserir(cpf, indice, diretorio);

            cpf = 1;
            inserir(cpf, indice, diretorio);

            // cpf = 12;
            // inserir(cpf, indice, diretorio);

            // cpf = 22;
            // inserir(cpf, indice, diretorio);

            // cpf = 7;
            // inserir(cpf, indice, diretorio);

            // cpf = 16;
            // inserir(cpf, indice, diretorio);

            // cpf = 13;
            // inserir(cpf, indice, diretorio);

            // cpf = 19;
            // inserir(cpf, indice, diretorio);

            RegistroDoBucket[] registros1 = indice.getBucket(12);
            System.out.println("Lendo do bucket 1:");
            for (RegistroDoBucket registro : registros1) {
                System.out.println("registro: " + registro);
            }

            RegistroDoBucket[] registros2 = indice.getBucket(56);
            System.out.println("Lendo do bucket 2:");
            for (RegistroDoBucket registro : registros2) {
                System.out.println("registro: " + registro);
            }

            // RegistroDoBucket[] registros3 = indice.getBucket(100);
            // System.out.println("Lendo do bucket 3:");
            // for (RegistroDoBucket registro : registros3) {
            //     System.out.println("registro: " + registro);
            // }

            // RegistroDoBucket[] registros4 = indice.getBucket(144);
            // System.out.println("Lendo do bucket 4:");
            // for (RegistroDoBucket registro : registros4) {
            //     System.out.println("registro: " + registro);
            // }

            // RegistroDoBucket[] registros5 = indice.getBucket(188);
            // System.out.println("Lendo do bucket 5:");
            // for (RegistroDoBucket registro : registros5) {
            //     System.out.println("registro: " + registro);
            // }
    }

    private static void inserir(int cpf, Indice indice, Diretorio diretorio) {
        int bucket;

        N++;

        bucket = diretorio.getPaginaIndice(cpf);
        if (indice.inserir_registro(cpf, bucket, N) == -1) {
            diretorio.duplicar();
            indice.setProfundidadeGlobal(diretorio.getProfundidade());

            int profundidadeBucket = indice.inserir_registro(cpf, bucket, N);
            diretorio.reorganizar(bucket, indice.getQtdBuckets(), profundidadeBucket);

            indice.dividir_bucket(bucket, diretorio);

            bucket = diretorio.getPaginaIndice(cpf);
            indice.inserir_registro(cpf, bucket, N);
        }
    }
}
