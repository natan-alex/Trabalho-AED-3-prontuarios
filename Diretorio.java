package trabalho_aed_prontuario.indice;

import java.io.RandomAccessFile;
import java.io.EOFException;
import java.io.IOException;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

public class Diretorio {
    private RandomAccessFile raf;

    private int profundidade;
    private final int[] indices;
    private int controlador;

    // caso o arquivo exista os indices e metadados são lidos
    // e os parâmetros passados ao construtor são ignorados
    // caso o arquivo NÃO exista é necessário criar os indices
    // e metadados no início do arquivo
    public Diretorio(String nome_do_arquivo, int profundidade) {
        indices = new int[1000000];
        controlador = 0;

        try {
            raf = new RandomAccessFile(nome_do_arquivo, "rws");
            if (raf.length() > 0) {
                lerInicial();
            } else {
                this.profundidade = profundidade;
                escreverInicial();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getProfundidade() {
        return profundidade;
    }

    // escrever no inicio do arquivo a profundidade global e
    // os enderecos do indices
    // escrever em memoria os enderecos do indice
    private void escreverInicial() {
        try {
            int num_buckets = (int) Math.pow(2, this.profundidade);
            raf.writeInt(profundidade);
            for (int i = 1; i <= num_buckets; i++) {
                raf.writeInt(i);
                indices[controlador++] = i;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ler desde o inicio do arquivo a profundidade global e
    // os enderecos do indices e também os escrever em memoria
    private void lerInicial() {
        try {
            profundidade = raf.readInt();

            controlador = 0;
            int num_buckets = (int) Math.pow(2, profundidade);
            int contador = 0;

            while(contador != num_buckets) {
                indices[controlador++] = raf.readInt();
                contador++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // aplica a funcao hash na entrada (cpf) e busca o
    // o endereco do bucket no diretorio
    public int getPaginaIndice(int entrada) {
        int posicao = (int) Math.round(entrada % Math.pow(2, profundidade));
        return indices[posicao];
    }

    // reorganiza os ponteiros com base no bucket que
    // foi duplicado, o endereco do novo e a profundidade
    // do bucket utiliza a profundidade para reorganizar
    // os ponteiros entre os enderecos dos dois buckets
    public void reorganizar(int adrDupBucket, int adrNovoBucket, int profBucket) {
        try {
            int oldReference, newReference;
            for (int i = 0; i < controlador; i++) {
                if (indices[i] == adrDupBucket) {
                    oldReference = (int) Math.round(i % Math.pow(2, profBucket - 1));
                    newReference = (int) Math.round(i % Math.pow(2, profBucket));

                    if (oldReference != newReference) {
                        raf.seek(4 + (long) (i + 1)*4);
                        raf.writeInt(adrNovoBucket);
                        indices[i] = adrNovoBucket;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // duplica o diretorio sem reorganizar os ponteiros e
    // escreve em disco e em memoria os novos enderecos dos indices
    public void duplicar() {
        try {
            raf.seek(0);
            raf.writeInt(++profundidade);

            raf.seek( raf.length() );

            int tam_indices = controlador;
            int indices_i;
            for (int i = 0; i < tam_indices; i++) {
                indices_i = indices[i];
                raf.writeInt(indices_i);
                indices[controlador++] = indices_i;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // fechar conexão com o arquivo
    public void fecharArquivo() {
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void imprimirArquivo() {
        try {
            raf.seek(0);
            System.out.println("========== DIRETORIO ==========");
            System.out.println("Profundidade: " + raf.readInt());
            for (int i = 0; i < controlador; i++) {
                System.out.println("[" + i + "] Bucket: " + raf.readInt());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
