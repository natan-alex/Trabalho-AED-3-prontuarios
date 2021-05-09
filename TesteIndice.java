package trabalho_aed_prontuario.indice;

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.IOException;

public class TesteIndice {
    public static void main(String[] args) {
        try {
            Indice indice = new Indice(1, 10); // esperado: arquivo de indices tenha sido
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

            long aux = indice.criarNovoBucket();
            System.out.println(aux);
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
            System.out.println( indice.inserir_registro(119, 1, 11) );
            System.out.println( indice.inserir_registro(121, 1, 12) );

            RegistroDoBucket[] registros = indice.getBucket(12);
            for (RegistroDoBucket registro : registros) {
                System.out.println("registro: " + registro);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
