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

            RegistroDoBucket[] registros = indice.getBucket(12);
            for (RegistroDoBucket registro : registros) {
                System.out.println("registro: " + registro);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
