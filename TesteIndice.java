package trabalho_aed_prontuario.indice;

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
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

            FileInputStream fis = new FileInputStream("indice.db");
            // DataInputStream dis = new DataInputStream(fis);
            // ObjectInputStream ois = new ObjectInputStream(fis);
            // System.out.println(ois.readInt());

            // indice.criarNovoBucket(1);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
