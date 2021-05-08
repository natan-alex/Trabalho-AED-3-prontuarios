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
            System.out.println(raf.readInt());
            System.out.println(raf.readInt());
            System.out.println(raf.readInt());
            indice.setProfundidadeGlobal(10);
            raf.seek(0);
            System.out.println(raf.readInt());
            System.out.println(raf.readInt());
            System.out.println(raf.readInt());

            RegistroDoBucket[] registros = indice.getBucket( indice.criarNovoBucket(0) );
            for (RegistroDoBucket registro : registros) {
                System.out.println("registro: " + registro);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
