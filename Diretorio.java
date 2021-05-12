package trabalho_aed_prontuario.diretorio;

import trabalho_aed_prontuario.indice.Indice;

import java.io.RandomAccessFile;
import java.io.EOFException;
import java.io.IOException;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

public class Diretorio {
    private static String arquivo = "diretorio.db";

    private RandomAccessFile raf;

    private int profundidade;
    private Indice indice;
    private List<Integer> indices = new ArrayList<Integer>();

    // caso o arquivo exista os indices e metadados são lidos
    // e os parâmetros passados ao construtor são ignorados
    // caso o arquivo NÃO exista é necessário criar os indices
    // e metadados no início do arquivo
    public Diretorio(int profundidade) {
        this.profundidade = profundidade;
        this.arquivo = arquivo;

        indice = new Indice(profundidade, 4);
        try {
            raf = new RandomAccessFile(this.arquivo, "rws");
            if (raf.length() > 0) {
                lerInicial();
            } else {
                escreverInicial();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // escrever no inicio do arquivo a profundidade global e
    // os enderecos do indices
    // escrever em memoria os enderecos do indice
    private void escreverInicial() {
        try {
            raf.writeInt(profundidade);
            for (int i = 0; i < Math.pow(2, this.profundidade); i++) {
                indice.criarNovoBucket(profundidade);
                raf.writeInt(i);
                this.indices.add(i);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ler desde o inicio do arquivo a profundidade global e
    // os enderecos do indices e também os escrever em memoria
    private void lerInicial() {
        try {
            raf.seek(0);

            int dado = raf.readInt();
            this.profundidade = dado;

            this.indices.clear();
            while(true) {
                dado = raf.readInt();
                indices.add(dado);
            }
        } catch (EOFException ex) {
            // While true ali em cima levanta um erro
            // quando chega no fim do arquivo, mas como isso
            // é esperado simplesmente ignoramos o erro
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // aplica a funcao hash na entrada (cpf) e busca o
    // o endereco do bucket no diretorio
    public int getPaginaIndice(int entrada) {
        int posicao = (int) Math.round(entrada % Math.pow(2, profundidade));
        return indices.get(posicao);
    }

    // reorganiza os ponteiros com base no bucket que
    // foi duplicado, o endereco do novo e a profundidade
    // do bucket utiliza a profundidade para reorganizar
    // os ponteiros entre os enderecos dos dois buckets
    public void reorganizar(int adrDupBucket, int adrNovoBucket, int profBucket) {
        List<Integer> newIndices = new ArrayList<Integer>(indices);

        try {
            for (int i = 0; i < this.indices.size(); i++) {
                if (this.indices.get(i) == adrDupBucket) {
                    int oldReference = (int) Math.round(i % Math.pow(2, profBucket - 1));
                    int newReference = (int) Math.round(i % Math.pow(2, profBucket));

                    if (oldReference != newReference) {
                        raf.seek((i + 1)*4);
                        raf.writeInt(adrNovoBucket);
                        newIndices.set(i, adrNovoBucket);
                    }
                }
            }

            this.indices = newIndices;
            printDiretorio();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // duplica o diretorio sem reorganizar os ponteiros e
    // escreve em disco e em memoria os novos enderecos dos indices
    public void duplicar() {
        List<Integer> newIndices = new ArrayList<Integer>(indices);
        this.profundidade++;

        try {
            RandomAccessFile raf = new RandomAccessFile(this.arquivo, "rw");
            raf.seek(0);
            raf.writeInt(this.profundidade);

            int tamArquivo = 4 + this.indices.size()*4;
            raf.seek(tamArquivo);

            for (int i : this.indices) {
                raf.writeInt(i);
                newIndices.add(i);
            }

            this.indices = newIndices;
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
