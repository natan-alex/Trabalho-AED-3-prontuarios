package trabalho_aed_prontuario.indice;

import trabalho_aed_prontuario.diretorio.Diretorio;
import trabalho_aed_prontuario.mestre.ArquivoMestre;
import trabalho_aed_prontuario.mestre.Prontuario;
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
            byte bytesAnotacaoes = 10;
            int num_registro, cpf;

            Prontuario prontuario;
            ArquivoMestre mestre = new ArquivoMestre(bytesAnotacaoes);
            Indice indice = new Indice(profundidade, tamBuckets); // esperado: arquivo de indices tenha sido
            Diretorio diretorio = new Diretorio(profundidade, tamBuckets, indice); // esperado: arquivo de indices tenha sido

            List<Integer> cpfs = Arrays.asList(new Integer[]{10, 3, 18, 20, 8, 6, 1, 12, 22, 7, 16, 13, 19});
            // criado com o número de buckets inicial baseado no tamanho do diretório
            // ou na profundidade global
            // params: pglobal, tamanho do bucket

            for (int i = 0; i < cpfs.size(); i++) {
                cpf = 10;
                prontuario = new Prontuario(cpfs.get(i), "Paciente" + i, LocalDate.now(), 'M', bytesAnotacaoes);
                num_registro = mestre.inserir_registro(prontuario);
                inserir(cpfs.get(i), num_registro, indice, diretorio);
            }

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

            RegistroDoBucket[] registros3 = indice.getBucket(100);
            System.out.println("Lendo do bucket 3:");
            for (RegistroDoBucket registro : registros3) {
                System.out.println("registro: " + registro);
            }

            RegistroDoBucket[] registros4 = indice.getBucket(144);
            System.out.println("Lendo do bucket 4:");
            for (RegistroDoBucket registro : registros4) {
                System.out.println("registro: " + registro);
            }

            RegistroDoBucket[] registros5 = indice.getBucket(188);
            System.out.println("Lendo do bucket 5:");
            for (RegistroDoBucket registro : registros5) {
                System.out.println("registro: " + registro);
            }
    }

    private static void inserir(int cpf, int num_registro, Indice indice, Diretorio diretorio) {
        int bucket;

        bucket = diretorio.getPaginaIndice(cpf);
        int insercao;
        insercao = indice.inserir_registro(cpf, bucket, num_registro);

        if (insercao == -1) {
            diretorio.duplicar();
            indice.setProfundidadeGlobal(diretorio.getProfundidade());

            int profundidadeBucket = indice.inserir_registro(cpf, bucket, num_registro);
            diretorio.reorganizar(bucket, indice.getQtdBuckets(), profundidadeBucket);

            indice.dividir_bucket(bucket, diretorio);

            bucket = diretorio.getPaginaIndice(cpf);
            indice.inserir_registro(cpf, bucket, num_registro);

        } else if (insercao != 0) { // somente dividir
            diretorio.reorganizar(bucket, indice.getQtdBuckets(), insercao);

            indice.dividir_bucket(bucket, diretorio);

            bucket = diretorio.getPaginaIndice(cpf);
            indice.inserir_registro(cpf, bucket, num_registro);
        }
    }
}
