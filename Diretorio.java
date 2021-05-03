package trabalho_aed_prontuario;

import java.io.*;
import java.lang.Math;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Diretorio {
    private int profundidade;
    private String arquivo;
    private int[] indices;

    public Diretorio() {
        this.profundidade = 0;
        this.indices = new int[]{0};;
        this.arquivo = "/tmp/diretorio.db";
    }

    public Diretorio(int profundidade, String arquivo) {
        this.profundidade = profundidade;
        this.indices = IntStream.range(0, profundidade + 1).toArray();
        this.arquivo = arquivo;
    }

    public void setCabecalho() {
        try {
            InputStream inputStream = new FileInputStream(this.arquivo);
            System.out.println(inputStream.read());

            OutputStream outputStream = new FileOutputStream(this.arquivo);
            outputStream.write(profundidade);
            outputStream.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getPaginaIndice(int entrada) {
        int posicao = (int) Math.round(entrada % Math.pow(2, profundidade));
        return indices[posicao];
    }

    public void reorganizar(int numPaginaDuplicada, int numNovaPagina) {
        // Posso iterar sobre a lista de indices ou devo carregar o arquivo?
    }

    public void duplicarEReorganizar(int numPaginaDuplicada, int numNovaPagina) {
        int[] newIndices = Stream.concat(Arrays.stream(indices), Arrays.stream(indices))
                           .toArray(int[]::new);

        int indiceNumNovaPagina = numPaginaDuplicada + (int) Math.round(Math.pow(2, profundidade));
        newIndices[indiceNumNovaPagina] = numNovaPagina;
        profundidade++;

        try {
            RandomAccessFile random = new RandomAccessFile(this.arquivo, "rw");

            random.write(this.intTo4Bytes(numNovaPagina), this.intTo4Bytes(indiceNumNovaPagina + 1));
            random.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        indices = newIndices;
    }

    private byte[] intTo4Bytes(int n) {
        return ByteBuffer.allocate(4).putInt(n).array();
    }
}

/*
  O  diretório  é  um  arquivo  de  inteiros  de  4  bytes,  cujos  valores  apontam  para  o  número  de  uma página  do  arquivo  de  índices  (começando  por  0). O  valor  da  profundidade  global  (p)  deve  ser armazenado  no  início  do  arquivo.  Durante  o  processamento,  odiretório deve ficarsempre carregado  em  memóriaprimáriamas  será  atualizado  em  disco  sempre  que  houver  alguma mudança  em  sua  estrutura  e/ou  conteúdo.A  profundidade  global  inicial  deve  ser  parâmetro  para do programa.
*/
