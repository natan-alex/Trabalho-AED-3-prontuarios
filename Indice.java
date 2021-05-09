package trabalho_aed_prontuario.indice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;

import java.io.IOException;
import java.io.EOFException;

public class Indice {
    private RandomAccessFile raf;

    private int profundidade_global;
    private int tam_bucket;
    private int prox_cpf;

    // caso o arquivo exista os metadados são lidos
    // e os parâmetros passados ao construtor são ignorados
    // caso o arquivo NÃO exista é necessário adicionar
    // os metadados no início do arquivo
    public Indice(int profundidade_global, int tam_bucket) {
        try {
            if ( new File("indice.db").exists() ) {
                raf = new RandomAccessFile("indice.db", "rws");
                ler_metadados();
            } else {
                raf = new RandomAccessFile("indice.db", "rws");
                this.profundidade_global = profundidade_global;
                this.tam_bucket = tam_bucket;
                escrever_metadados();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // em caso de duplicação do diretório, necessário alterar
    // a profundidade global armazenada. Só números inteiros
    // positivos são válidos. Ao alterar a profundide,
    // necessário alterá-la também no arquivo de índices
    public void setProfundidadeGlobal(int profundidade_global) {
        if (profundidade_global > 0) {
            this.profundidade_global = profundidade_global;

            try {
                raf.seek(0);
                raf.writeInt(profundidade_global);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // escrever no inicio do arquivo a profundiade global,
    // o tamanho do bucket e o proximo id(cpf) a ser usado
    // os metadados serão escritos caso o arquivo não exista
    private void escrever_metadados() {
        try {
            raf.writeInt(profundidade_global);
            raf.writeInt(tam_bucket);
            raf.writeInt(1); // primeiro cpf a ser utilizado
            prox_cpf = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ler metadados(profundidade_global, prox_cpf, 
    // tam_bucket) inseridos ao criar o arquivo de indices
    // metadados serão lidos uma única vez em caso de o arquivo
    // já existir na criação da classe
    private void ler_metadados() {
        try {
            profundidade_global = raf.readInt();
            tam_bucket = raf.readInt();
            prox_cpf = raf.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // para debug
    public RegistroDoBucket[] getBucket(long pos_inicio) {
        RegistroDoBucket[] registros = new RegistroDoBucket[tam_bucket];
        try {
            raf.seek(pos_inicio + 8);
            for (int i = 0; i < tam_bucket; i++) {
                registros[i] = new RegistroDoBucket(raf.readBoolean(), raf.readInt(), raf.readInt());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return registros;
    }

    public long criarNovoBucket() {
        return criarNovoBucket(1);
    }

    // cria um novo bucket com base no tamanho do bucket
    public long criarNovoBucket(int profundidade_local) {
        long endereco_inicio_bucket = 0;

        try {
            // bucket é sempre adicionado ao fim do arquivo
            // de índices, portanto o seek é feito para o 
            // tamanho do arquivo, que é o ponto de início
            // do bucket
            endereco_inicio_bucket = raf.length();
            raf.seek(endereco_inicio_bucket);
            System.out.println("raf.length(): " + raf.length());

            raf.writeInt(profundidade_local);
            raf.writeInt(0); // ocupacao
            for (int i = 0; i < tam_bucket; i++) {
                System.out.println("prox_cpf: " + prox_cpf);
                // escrever no arquivo os bytes que dizem
                // respeito a um registro do índice recém criado(não
                // possui chave e o num_registro é -1) e que 
                // contém o prox_cpf como id
                raf.write( new RegistroDoBucket(prox_cpf++).toByteArray() );
            }

            // escrever no arquivo o prox_cpf
            raf.seek(8);
            raf.writeInt(prox_cpf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return endereco_inicio_bucket;
    }

    public int inserir_registro(int cpf, int num_bucket, int num_registro) {
        // profundidade + ocupacao + tam_bucket * 9
        int pos_bucket = 12 + ( (num_bucket - 1) * (tam_bucket * 9) );
        System.out.println("pos_bucket = " + pos_bucket);
        int profundidade_do_bucket = -1;
        int ocupacao = -1;
        long pos_atual = 0;
        try {
            raf.seek(pos_bucket);
            profundidade_do_bucket = raf.readInt();
            System.out.println("profundidade_do_bucket: " + profundidade_do_bucket);
            ocupacao = raf.readInt();
            System.out.println("ocupacao: " + ocupacao);
            pos_atual = raf.getFilePointer();

            if (ocupacao == tam_bucket) {
                if (profundidade_do_bucket == profundidade_global) {
                    // duplicar diretorio!!
                    return -1;
                } else {
                    // novo bucket
                    criarNovoBucket(profundidade_do_bucket + 1);
                    // rearranjar chaves
                    return -2;
                }
            } else {
                // ocupacao * tam_registro
                raf.seek(pos_atual + ocupacao * 9);
                raf.write( new RegistroDoBucket(false, cpf, num_registro).toByteArray() );
                raf.seek(pos_atual - 4);
                raf.writeInt(++ocupacao);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
