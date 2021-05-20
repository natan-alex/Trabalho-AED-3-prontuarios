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
        StatusDeInsercao status;
        status = indice.getStatusDeUmaNovaInsercao(bucket);
        System.out.println("status em teste_indice: " + status);

        if (status == StatusDeInsercao.DUPLICAR_DIRETORIO) {
            diretorio.duplicar();
            // alterar a profundidade global do indice
            indice.setProfundidadeGlobal(diretorio.getProfundidade());
            // obter o novo número do bucket a partir
            // da criação de um novo bucket no arquivo de indice
            int num_novo_bucket = indice.inserirNovoBucketNoArquivo( diretorio.getProfundidade() );
            // referenciar novo bucket no diretório
            diretorio.reorganizar(bucket, num_novo_bucket, diretorio.getProfundidade());
            // reorganizar chaves do bucket
            indice.dividirBucket(bucket, diretorio);
            // inserir novo registro
            bucket = diretorio.getPaginaIndice(cpf);
            indice.inserirRegistro(cpf, N, bucket);

        } else if (status == StatusDeInsercao.REARRANJAR_CHAVES) { // somente dividir
            // obter a nova profundidade do bucket baseada
            // na atual
            int profundidadeBucket = indice.getProfundidadeDoBucket(bucket) + 1;
            // criar novo bucket com tal profundidade
            int num_novo_bucket = indice.inserirNovoBucketNoArquivo(profundidadeBucket);
            // alterar a profundidade atual do bucket
            indice.setProfundidadeDoBucket(profundidadeBucket + 1, bucket);
            // referenciar novo bucket no diretório
            diretorio.reorganizar(bucket, indice.getQtdBuckets(), profundidadeBucket);
            // reorganizar chaves do bucket
            indice.dividirBucket(bucket, diretorio);
            // inserir novo registro
            bucket = diretorio.getPaginaIndice(cpf);
            indice.inserirRegistro(cpf, N, bucket);
        } else if (status == StatusDeInsercao.TUDO_OK) {
            indice.inserirRegistro(cpf, N, bucket);
        }
    }
}
