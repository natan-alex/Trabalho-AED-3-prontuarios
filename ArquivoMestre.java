package trabalho_aed_prontuario.mestre;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.RandomAccessFile;

import java.io.IOException;
import java.io.EOFException;

public class ArquivoMestre {
    private short num_bytes_anotacoes;
    private RandomAccessFile raf;

    public ArquivoMestre(short num_bytes_anotacoes) {
        try {
            raf = new RandomAccessFile("arquivo_mestre.db", "rws"); 
            if (raf.length() > 0) {
                this.num_bytes_anotacoes = raf.readShort();
            } else {
                this.num_bytes_anotacoes = (num_bytes_anotacoes > 0) ? num_bytes_anotacoes : 100;
                raf.writeShort(num_bytes_anotacoes);
            }
            System.out.println("num_bytes_anotacoes: " + this.num_bytes_anotacoes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
