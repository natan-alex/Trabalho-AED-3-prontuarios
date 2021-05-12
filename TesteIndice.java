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
            int tamBuckets = 10;
            Diretorio diretorio = new Diretorio(1, tamBuckets); // esperado: arquivo de indices tenha sido
            Indice indice = new Indice(1, tamBuckets); // esperado: arquivo de indices tenha sido
            // criado com o número de buckets inicial baseado no tamanho do diretório
            // ou na profundidade global
            // params: pglobal, tamanho do bucket
            // indice.criarNovoBucket();
            // params: plocal
            // indice.criarNovoBucket(1);

            // FileInputStream fis = new FileInputStream("indice.db");
            RandomAccessFile raf = new RandomAccessFile("indice.db", "r");
            // System.out.println(raf.readInt());
            // System.out.println(raf.readInt());
            // System.out.println(raf.readInt());
            // indice.setProfundidadeGlobal(10);
            // raf.seek(0);
            // System.out.println(raf.readInt());
            // System.out.println(raf.readInt());
            // System.out.println(raf.readInt());

            // long aux = indice.criarNovoBucket();
            // System.out.println(aux);
            // RegistroDoBucket[] registros = indice.getBucket(aux);
            // for (RegistroDoBucket registro : registros) {
            //     System.out.println("registro: " + registro);
            // }

            // teste inserir registro
            System.out.println( indice.inserir_registro(99, 1, 1) );
            System.out.println( indice.inserir_registro(101, 1, 2) );
            System.out.println( indice.inserir_registro(103, 1, 3) );
            System.out.println( indice.inserir_registro(105, 1, 4) );
            System.out.println( indice.inserir_registro(107, 1, 5) );
            System.out.println( indice.inserir_registro(109, 1, 6) );
            System.out.println( indice.inserir_registro(111, 1, 7) );
            System.out.println( indice.inserir_registro(113, 1, 8) );
            System.out.println( indice.inserir_registro(115, 1, 9) );
            System.out.println( indice.inserir_registro(117, 1, 10) );

            RegistroDoBucket[] registros1 = indice.getBucket(12);
            for (RegistroDoBucket registro : registros1) {
                System.out.println("registro: " + registro);
            }

            int cpf = 0;
            int bucket = diretorio.getPaginaIndice(cpf);

            System.out.println("==================================");
            System.out.println("num bucket" + bucket);

            if (indice.inserir_registro(119, bucket, 11) == -1) {
                diretorio.duplicar();

                indice.setProfundidadeGlobal(diretorio.getProfundidade());
                int profundidadeBucket = indice.inserir_registro(119, bucket, 11);

                diretorio.reorganizar(bucket, indice.qtd_buckets, profundidadeBucket);
                System.out.println("Reorganizar");
                // int profundidadeBucket = indice.inserir_registro(119, bucket, 11);
            }

            // System.out.println( indice.inserir_registro(121, 1, 12) );

            RegistroDoBucket[] registros2 = indice.getBucket(12);
            for (RegistroDoBucket registro : registros2) {
                System.out.println("registro: " + registro);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
