package trabalho_aed_prontuario.indice;

import trabalho_aed_prontuario.diretorio.Diretorio;

import java.io.RandomAccessFile;
import java.io.IOException;

public class TesteIndice {
    private static int N = 0;

    public static void main(String[] args) {
            int tamBuckets = 4;
            int profundidade = 1;
            Indice indice = new Indice(profundidade, tamBuckets); 
            Diretorio diretorio = new Diretorio(profundidade, tamBuckets, indice); 

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

            cpf = 8;
            inserir(cpf, indice, diretorio);

            cpf = 6;
            inserir(cpf, indice, diretorio);

            cpf = 1;
            inserir(cpf, indice, diretorio);

            cpf = 12;
            inserir(cpf, indice, diretorio);

            cpf = 22;
            inserir(cpf, indice, diretorio);

            cpf = 7;
            inserir(cpf, indice, diretorio);

            cpf = 16;
            inserir(cpf, indice, diretorio);

            cpf = 13;
            inserir(cpf, indice, diretorio);

            cpf = 19;
            inserir(cpf, indice, diretorio);

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
    }

    private static void inserir(int cpf, Indice indice, Diretorio diretorio) {
        int bucket;

        N++;

        bucket = diretorio.getPaginaIndice(cpf);
        System.out.println("\n\nBucket do hash: " + bucket);
        int insercao;
        insercao = indice.inserirRegistro(cpf, N, bucket);
        System.out.println("insercao em indice: " + insercao);

        if (insercao == -1) {
            diretorio.duplicar();
            indice.setProfundidadeGlobal(diretorio.getProfundidade());

            int profundidadeBucket = indice.inserirRegistro(cpf, N, bucket);
            System.out.println("profundidadeBucket: " + profundidadeBucket);
            System.out.println("getQtdBuckets(): " + indice.getQtdBuckets());
            diretorio.reorganizar(bucket, indice.getQtdBuckets(), profundidadeBucket);

            indice.dividirBucket(bucket, diretorio);

            bucket = diretorio.getPaginaIndice(cpf);
            indice.inserirRegistro(cpf, N, bucket);

        } else if (insercao != 0) { // somente dividir
            diretorio.reorganizar(bucket, indice.getQtdBuckets(), insercao);

            indice.dividirBucket(bucket, diretorio);

            bucket = diretorio.getPaginaIndice(cpf);
            indice.inserirRegistro(cpf, N, bucket);
        }
    }
}
