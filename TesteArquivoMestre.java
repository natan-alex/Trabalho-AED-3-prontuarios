package trabalho_aed_prontuario.mestre;

import java.io.RandomAccessFile;
import java.io.IOException;

import trabalho_aed_prontuario.indice.*;
import trabalho_aed_prontuario.mestre.*;

import java.time.LocalDate;

public class TesteArquivoMestre {
    public static void main(String[] args) {
        try {
            RandomAccessFile raf = new RandomAccessFile("arquivo_mestre.db", "rw");
            ArquivoMestre arq = new ArquivoMestre((short) 100);
            Prontuario p = new Prontuario(1, "fulano", LocalDate.now(), 'm', (short) 50);
            Prontuario p2 = new Prontuario(2, "ciclano", LocalDate.now(), 'm', (short) 70);

            System.out.println(arq.inserir_registro(p));
            System.out.println(arq.inserir_registro(p2));

            int num_registros_no_arquivo = raf.readInt();
            short num_bytes_anotacoes = raf.readShort();

            Prontuario[] prontuarios = new Prontuario[num_registros_no_arquivo];
            int id_registro = 0;
            short tam_registro = 0;
            byte[] registro_bytes = null;

            for (int i = 0; i < prontuarios.length; i++) {
                id_registro = raf.readInt();
                tam_registro = raf.readShort();
                registro_bytes = new byte[tam_registro];
                raf.read(registro_bytes);
                prontuarios[i] = new Prontuario(registro_bytes);
                System.out.println("Prontuario " + id_registro + ": " + prontuarios[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

