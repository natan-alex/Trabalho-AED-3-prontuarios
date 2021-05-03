package trabalho_aed_prontuario;

import java.io.*;
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

    public Diretorio() {
        this.profundidade = 0;
        this.indices.add(0);
        this.arquivo = "/tmp/diretorio.db";
    }

    public Diretorio(int profundidade, String arquivo) {
        this.profundidade = profundidade;

        for (int i = 0; i < Math.pow(2, profundidade); i++) {
            this.indices.add(i);
        }

        this.arquivo = arquivo;
    }

    public void setCabecalho() {
        try {
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
        return indices.get(posicao);
    }

    public void reorganizar(int numPaginaDuplicada, int profundidadePagina) {
        List<Integer> newIndices = new ArrayList<Integer>(indices);

        // TODO: Não tem alguma forma melhor de fazer essa busca?
        for (int i = 0; i < this.indices.size(); i++) {
            if (this.indices.get(i) == numPaginaDuplicada) {
                int indicePagina = (int) Math.round(i % Math.pow(2, profundidadePagina));
                newIndices.set(i, indicePagina);
            }
        }

        this.indices = newIndices;
        this.printDiretorio();
    }

    public void duplicar() {
        List<Integer> indices = this.duplicarIndices(this.indices);
        this.profundidade++;

        this.indices = indices;
    }

    private List<Integer> duplicarIndices(List<Integer> indices) {
        List<Integer> newIndices = new ArrayList<Integer>(indices);
        for (int i : this.indices) {
            newIndices.add(i);
        }

        return newIndices;
    }

    // TODO: Remover
    private void printDiretorio() {
        System.out.println("DIRETORIO");
        for (int i : indices) {
            System.out.println(i);
        }
        System.out.println("============");
    }
}

/*
  O  diretório  é  um  arquivo  de  inteiros  de  4  bytes,  cujos  valores  apontam  para  o  número  de  uma página  do  arquivo  de  índices  (começando  por  0). O  valor  da  profundidade  global  (p)  deve  ser armazenado  no  início  do  arquivo.  Durante  o  processamento,  odiretório deve ficarsempre carregado  em  memóriaprimáriamas  será  atualizado  em  disco  sempre  que  houver  alguma mudança  em  sua  estrutura  e/ou  conteúdo.A  profundidade  global  inicial  deve  ser  parâmetro  para do programa.
*/
