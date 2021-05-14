package trabalho_aed_prontuario.indice;

import trabalho_aed_prontuario.diretorio.Diretorio;

import java.io.RandomAccessFile;
import java.io.IOException;

public class TesteIndice {
    public static void main(String[] args) {
        try {
            int tamBuckets = 2;
            Indice indice = new Indice(1, tamBuckets); // esperado: arquivo de indices tenha sido
            Diretorio diretorio = new Diretorio(1, tamBuckets, indice); // esperado: arquivo de indices tenha sido
            // criado com o número de buckets inicial baseado no tamanho do diretório
            // ou na profundidade global
            // params: pglobal, tamanho do bucket

            RandomAccessFile raf = new RandomAccessFile("indice.db", "r");

            System.out.println( indice.inserir_registro(1, 1, 1) );
            System.out.println( indice.inserir_registro(2, 1, 2) );

            System.out.println( indice.inserir_registro(3, 2, 3) );
            System.out.println( indice.inserir_registro(4, 2, 4) );
            int cpf = 1;
            int bucket = diretorio.getPaginaIndice(cpf);
            System.out.println("hash com cpf=" + cpf + ": " + bucket);

            if (indice.inserir_registro(5, bucket, 5) == -1) {
                diretorio.duplicar();

                indice.setProfundidadeGlobal(diretorio.getProfundidade());
                int profundidadeBucket = indice.inserir_registro(5, bucket, 5);
                System.out.println("profundidadeBucket: " + profundidadeBucket);
                //
                diretorio.reorganizar(bucket, indice.getQtdBuckets(), profundidadeBucket);
                indice.inserir_registro(6, indice.getQtdBuckets(), 6);
            }

            RegistroDoBucket[] registros1 = indice.getBucket(12);
            System.out.println("Lendo do bucket 1:");
            for (RegistroDoBucket registro : registros1) {
                System.out.println("registro: " + registro);
            }

            RegistroDoBucket[] registros2 = indice.getBucket(38);
            System.out.println("Lendo do bucket 2:");
            for (RegistroDoBucket registro : registros2) {
                System.out.println("registro: " + registro);
            }

            RegistroDoBucket[] registros3 = indice.getBucket(64);
            System.out.println("Lendo do bucket 3:");
            for (RegistroDoBucket registro : registros3) {
                System.out.println("registro: " + registro);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
