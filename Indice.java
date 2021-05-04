package trabalho_aed_prontuario.indice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.IOException;
import java.io.EOFException;

public class Indice {
    private FileInputStream fis;
    private FileOutputStream fos;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private RandomAccessFile raf;
    private DataInputStream dis;
    private DataOutputStream dos;

    private int profundidade_global;
    private int tam_bucket;
    private int ultimo_cpf_usado;

    public Indice(int profundidade_global, int tam_bucket) {
        this.profundidade_global = profundidade_global;
        this.tam_bucket = tam_bucket;
        try {
            fos = new FileOutputStream("indice.db", true);
            dos = new DataOutputStream(fos);
            fis = new FileInputStream("indice.db");
            dis = new DataInputStream(fis);
            raf = new RandomAccessFile("indice.db", "rws");
            System.out.println("raf.length(): " + raf.length());

            if (raf.length() > 0) {
                System.out.println("tem");
                ultimo_cpf_usado = raf.readInt();
            } else {
                oos.writeInt(1);
                ultimo_cpf_usado = 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProfundidadeGlobal(int profundidade_global) {
        this.profundidade_global = profundidade_global;
    }

    public long criarNovoBucket(int profundidade_local) {
        long endereco_inicio_bucket = 0;

        try {
            // endereco_inicio_bucket = raf.length();
            System.out.println("raf.length(): " + raf.length());

            // for (int i = 0; i < tam_bucket; i++) {
            //     System.out.println("ultimo_cpf_usado: " + ultimo_cpf_usado);
            //     oos.writeObject( new RegistroDoBucket<Integer>(ultimo_cpf_usado++) );
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return endereco_inicio_bucket;
    }
}
