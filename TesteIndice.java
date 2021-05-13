package trabalho_aed_prontuario.indice;

import trabalho_aed_prontuario.diretorio.Diretorio;

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.IOException;

public class TesteIndice {
    public static void main(String[] args) {
        try {
            int numBuckets = 3;
            Diretorio diretorio = new Diretorio(1, numBuckets); // esperado: arquivo de indices tenha sido
            Indice indice = new Indice(1, numBuckets); // esperado: arquivo de indices tenha sido
            // criado com o número de buckets inicial baseado no tamanho do diretório
            // ou na profundidade global
            // params: pglobal, tamanho do bucket
            // params: plocal

            RandomAccessFile raf = new RandomAccessFile("indice.db", "r");

            RegistroDoBucket[] registros = indice.getBucket( indice.criarNovoBucket() );
            for (RegistroDoBucket registro : registros) {
                System.out.println("registro: " + registro);
            }
            System.out.println( indice.inserir_registro(113, 1, 1) );
            System.out.println( indice.inserir_registro(115, 1, 2) );
            System.out.println( indice.inserir_registro(117, 1, 3) );
            registros = indice.getBucket( 8 );
            for (RegistroDoBucket registro : registros) {
                System.out.println("registro: " + registro);
            }

            int cpf = 0;
            int bucket = diretorio.getPaginaIndice(cpf);
            System.out.println("bucket: " + bucket);

            System.out.println("==================================");
            if (indice.inserir_registro(119, bucket, 11) == -1) {
                diretorio.duplicar();
                diretorio.printDiretorio();
                System.out.println("novo tamanho apos duplicação do diretório: " + raf.length());

                // indice.setProfundidadeGlobal(diretorio.getProfundidade());
                // int profundidadeBucket = indice.inserir_registro(119, bucket, 11);

                // diretorio.reorganizar(bucket, indice.qtd_buckets, profundidadeBucket);
                // int profundidadeBucket = indice.inserir_registro(119, bucket, 11);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
