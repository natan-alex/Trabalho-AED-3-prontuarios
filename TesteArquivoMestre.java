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
            Prontuario p = new Prontuario(1, "fulano", LocalDate.now(), 'm', (short) 50, "anot");
            Prontuario p2 = new Prontuario(2, "ciclano", LocalDate.now(), 'm', (short) 50, "anot");

            System.out.println(arq.inserirRegistro(p));
            System.out.println(arq.inserirRegistro(p2));

            raf.seek(0);
            int num_registros_no_arquivo = raf.readInt();
            short num_bytes_anotacoes = raf.readShort();
            int prox_id = raf.readInt();

            Prontuario[] prontuarios = new Prontuario[num_registros_no_arquivo];
            int id_registro = 0;
            byte[] registro = new byte[114]; // 114 é o tamanho de um registro de prontuario
            // com 50 como sendo o tam_anotacoes

            for (int i = 0; i < prontuarios.length; i++) {
                id_registro = raf.readInt();
                raf.read(registro);
                prontuarios[i] = new Prontuario(registro);
                System.out.println("Prontuario de id=" + id_registro + ": " + prontuarios[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

