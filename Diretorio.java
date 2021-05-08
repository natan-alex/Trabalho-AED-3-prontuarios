package trabalho_aed_prontuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import java.lang.Math;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.nio.ByteBuffer;

public class Diretorio {
    private int profundidade;
    private String arquivo;
    private List<Integer> indices = new ArrayList<Integer>();

    private static DataOutputStream dos;
    private static DataInputStream dis;
    private static FileOutputStream fos;
    private static FileInputStream fis;

    public Diretorio(String arquivo) {
        this.arquivo = arquivo;
    }

    public Diretorio(int profundidade, String arquivo) {
        this.profundidade = profundidade;
        this.arquivo = arquivo;

        for (int i = 0; i < Math.pow(2, this.profundidade); i++) {
            this.indices.add(i);
        }
    }

    public void criarArquivo() {
        try {
            fos = new FileOutputStream(this.arquivo);
            dos = new DataOutputStream(fos);
            dos.writeInt(profundidade);

            for (int i = 0; i < Math.pow(2, this.profundidade); i++) {
                dos.writeInt(i);
            }

            dos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void carregarArquivo() {
        try {
            fis = new FileInputStream(this.arquivo);
            dis = new DataInputStream(fis);

            int dado = dis.readInt();
            this.profundidade = dado;

            this.indices.clear();
            while(dis.available() > 0) {
                 dado = dis.readInt();
                 indices.add(dado);
            }

            dis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Retornar página?
    public int getPaginaIndice(int entrada) {
        int posicao = (int) Math.round(entrada % Math.pow(2, profundidade));
        return indices.get(posicao);
    }

    public void reorganizar(int enderecoDupPagina, int enderecoNovaPagina, int profundidadePagina) {
        List<Integer> newIndices = new ArrayList<Integer>(indices);

        // FIXME: Não tem alguma forma melhor de fazer essa busca?
        // TODO: Reorgizar somente sabendo o endereço da página
        //       duplicada, não utilizando o id do página
        // TODO: Escrever em disco
        try {
            RandomAccessFile raf = new RandomAccessFile(this.arquivo, "rw");

            for (int i = 0; i < this.indices.size(); i++) {
                if (this.indices.get(i) == enderecoDupPagina) {
                    int oldReference = (int) Math.round(i % Math.pow(2, profundidadePagina - 1));
                    int newReference = (int) Math.round(i % Math.pow(2, profundidadePagina));

                    if (oldReference != newReference) {
                        raf.seek((i + 1)*4);
                        raf.writeInt(enderecoNovaPagina);
                        newIndices.set(i, enderecoNovaPagina);
                    }
                }
            }

            raf.close();
            this.indices = newIndices;
            this.printDiretorio();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void duplicar() {
        List<Integer> newIndices = new ArrayList<Integer>(indices);
        this.profundidade++;

        try {
            RandomAccessFile raf = new RandomAccessFile(this.arquivo, "rw");
            raf.seek(0);
            raf.writeInt(this.profundidade);
            raf.close();

            // Usar só RandomAccessFile
            fos = new FileOutputStream(this.arquivo, true);
            dos = new DataOutputStream(fos);
            for (int i : this.indices) {
                dos.writeInt(i);
                newIndices.add(i);
            }

            this.indices = newIndices;
            // this.printDiretorio();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void printDiretorio() {
        System.out.println("DIRETORIO");
        System.out.println("Profundidade: " + this.profundidade);
        for (int i : indices) {
            System.out.println(i);
        }
        System.out.println("============");
    }
}
