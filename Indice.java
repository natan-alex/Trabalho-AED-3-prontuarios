package trabalho_aed_prontuario.indice;

import java.io.RandomAccessFile;

import java.io.IOException;
import java.io.EOFException;

public class Indice {
    public static int qtd_buckets = 0; // TODO: revisar

    // boolean para lápide + int para chave + int para o número do registro
    private static final short SIZEOF_REGISTRO_DO_BUCKET = 9;
    private static final byte SIZEOF_METADADOS_INDICE = 8;

    private RandomAccessFile raf;

    private int profundidade_global;
    private int tam_bucket;

    // caso o arquivo exista os metadados são lidos
    // e os parâmetros passados ao construtor são ignorados
    // caso o arquivo NÃO exista é necessário adicionar
    // os metadados no início do arquivo
    public Indice(int profundidade_global, int tam_bucket) {
        try {
            raf = new RandomAccessFile("indice.db", "rws");
            if (raf.length() > 0) {
                ler_metadados();
            } else {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ler metadados(profundidade_global, prox_cpf, 
    // tam_bucket) inseridos ao criar o arquivo de indices
    // metadados serão lidos uma única vez em caso de o arquivo
    // já existir ao instanciar a classe
    private void ler_metadados() {
        try {
            profundidade_global = raf.readInt();
            tam_bucket = raf.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // para debug
    public RegistroDoBucket[] getBucket(long pos_inicio) {
        RegistroDoBucket[] registros = null;

        try {
            raf.seek(pos_inicio);
            int profundidade_do_bucket = raf.readInt();
            int ocupacao = raf.readInt();
            registros = new RegistroDoBucket[ocupacao];

            for (int i = 0; i < ocupacao; i++) {
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

    // cria um novo bucket com base no tamanho do bucket (var tam_bucket)
    // retorna a posição de início do bucket
    public long criarNovoBucket(int profundidade_local) {
        long endereco_inicio_bucket = 0;
        qtd_buckets++;

        try {
            // bucket é sempre adicionado ao fim do arquivo
            // de índices, portanto o seek é feito para o
            // tamanho do arquivo, que é o ponto de início
            // do bucket
            endereco_inicio_bucket = raf.length();
            raf.seek(endereco_inicio_bucket);
            System.out.println("raf.length(): " + raf.length());

            raf.writeInt(profundidade_local);
            raf.writeInt(0); // ocupacao inicial do bucket é sempre 0

            for (int i = 0; i < tam_bucket; i++) {
                // escrever no arquivo os bytes que dizem
                // respeito a um registro do índice recém criado(não
                // possui chave e o num_registro é -1) e que
                // contém o prox_cpf como id
                byte[] aa = new RegistroDoBucket().toByteArray();
                // DEVERIA SER 17
                System.out.println("byte[] length: " + aa.length);

                raf.write(new RegistroDoBucket().toByteArray());
            }

            System.out.println("raf.length2(): " + raf.length());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return endereco_inicio_bucket;
    }

    // inserir um registro em um bucket
    // params: cpf, número do bucket onde inserir o registro e
    // o número do registro no arquivo de dados, no arquivo mestre
    public int inserir_registro(int cpf, int num_bucket, int num_registro) {
        // caminhar até o ponto de início do bucket:
        // após os metadados, necessário percorrer todos os buckets até o bucket de interesse.
        // bucket tem tam_bucket registros de tamanho SIZEOF_REGISTRO_DO_BUCKET, portanto:
        // tamanho_metadados(8) + (num_bucket - 1) * (tam_bucket * tamanho_do_registro_do_bucket)
        int pos_bucket = SIZEOF_METADADOS_INDICE + (num_bucket - 1) * (tam_bucket * SIZEOF_REGISTRO_DO_BUCKET);

        // 8 + 1*(90)

        System.out.println("pos_bucket = " + pos_bucket);
        int profundidade_do_bucket = -1;
        int ocupacao = -1;
        long pos_apos_metadados_do_bucket = 0;

        try {
            raf.seek(pos_bucket);

            // ler metadados do bucket
            profundidade_do_bucket = raf.readInt();
            System.out.println("profundidade_do_bucket: " + profundidade_do_bucket);

            ocupacao = raf.readInt();
            System.out.println("ocupacao: " + ocupacao);

            pos_apos_metadados_do_bucket = raf.getFilePointer();

            if (ocupacao == tam_bucket) {
                System.out.println("profundidade_do_bucket: " + profundidade_do_bucket);
                System.out.println("profundidade_global: " + profundidade_global);

                if (profundidade_do_bucket == profundidade_global) {
                    System.out.println("duplicar dir!");
                    return -1;
                } else {
                    System.out.println("criando novo bucket...");
                    System.out.println("necessário rearranjar chaves!");

                    criarNovoBucket(profundidade_do_bucket + 1);

                    // raf.seek(pos_apos_metadados_do_bucket - 8);
                    // raf.writeInt(profundidade_do_bucket + 1);

                    return profundidade_do_bucket + 1;
                }
            } else {
                // ir até o ponto de inserção de um novo registro:
                // "tamanho" do bucket com base no número de registros já existentes
                // no bucket (ocupacao); analogia a uma lista encadeada
                // ocupacao * tam_registro
                // System.out.println("====================== TAM BUCKET: " + (1 + (pos_apos_metadados_do_bucket + ocupacao * SIZEOF_REGISTRO_DO_BUCKET)));
                raf.seek(pos_apos_metadados_do_bucket + ocupacao * SIZEOF_REGISTRO_DO_BUCKET);

                // escrever registro na posição encontrada
                raf.write( new RegistroDoBucket(false, cpf, num_registro).toByteArray() );

                // atualizar ocupacao
                raf.seek(pos_apos_metadados_do_bucket - 4);
                raf.writeInt(++ocupacao);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
