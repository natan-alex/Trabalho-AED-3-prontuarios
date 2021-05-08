package trabalho_aed_prontuario.mestre;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.RandomAccessFile;

import java.io.IOException;
import java.io.EOFException;

import trabalho_aed_prontuario.indice.Serializavel;

public class ArquivoMestre<T extends Serializavel> {
    private short num_bytes_anotacoes;
    private RandomAccessFile raf;

    public ArquivoMestre(short num_bytes_anotacoes) {
        try {
            raf = new RandomAccessFile("arquivo_mestre.db", "rws"); 
            // se o arquivo tiver algo, ignorar o argumento num_bytes_anotacoes
            // e obter o número de bytes para as anotações por meio do metadado
            // referente a ele. Caso contrário, checar se o argumento é positivo
            // e atribuir à variável num_bytes_anotacoes. Caso o argumento seja negativo o
            // tamanho default é de 100 bytes
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
