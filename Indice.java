package trabalho_aed_prontuario.indice;

import java.io.RandomAccessFile;
import trabalho_aed_prontuario.diretorio.Diretorio;
import java.lang.reflect.Executable;

import java.io.IOException;
import java.io.EOFException;

public class Indice {
    // boolean para lápide + int para chave + int para o número do registro
    private static final byte SIZEOF_REGISTRO_DO_BUCKET = 9;
    private static final byte SIZEOF_METADADOS_INDICE = 12;
    private static final byte SIZEOF_METADADOS_BUCKET = 8;

    private RandomAccessFile raf;

    private int profundidade_global;
    private int tam_bucket;
    private int qtd_buckets;

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
                this.qtd_buckets = 0;
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
        if (profundidade_global > 0)
            this.profundidade_global = profundidade_global;
        try {
            raf.seek(0);
            raf.writeInt(profundidade_global);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // a classe é responsável por alterar a variável qtd_buckets
    // portanto o setter é privado
    private void setQtdBuckets(int qtd_buckets) {
        if (qtd_buckets > 0)
            this.qtd_buckets = qtd_buckets;
        try {
            raf.seek(SIZEOF_METADADOS_INDICE - 4);
            raf.writeInt(qtd_buckets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getQtdBuckets() {
        return qtd_buckets;
    }

    // escrever no inicio do arquivo a profundiade global,
    // o tamanho do bucket e a qtd_buckets total no arquivo;
    // os metadados serão escritos caso o arquivo não exista
    private void escrever_metadados() {
        try {
            raf.writeInt(profundidade_global);
            raf.writeInt(tam_bucket);
            raf.writeInt(qtd_buckets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ler metadados(profundidade_global, tam_bucket,
    // qtd_buckets) inseridos ao criar o arquivo de indices;
    // metadados serão lidos uma única vez em caso de o arquivo
    // já existir ao instanciar a classe
    private void ler_metadados() {
        try {
            profundidade_global = raf.readInt();
            tam_bucket = raf.readInt();
            qtd_buckets = raf.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // debug
    public Bucket getBucket(long pos_inicio) {
        Bucket registros = null;

        try {
            raf.seek(pos_inicio);

            int profundidade_local = raf.readInt();
            int ocupacao = raf.readInt();
            byte[] registros = new byte[ocupacao * SIZEOF_REGISTRO_DO_BUCKET];
            registros = new Bucket(tam_bucket, profundidade_local, ocupacao, registros);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return registros;
    }

    // insere um novo bucket com valores default no arquivo
    // de índice;
    // retorna a posição de início do bucket
    public long inserir_novo_bucket_no_arquivo(int profundidade_local) {
        long endereco_inicio_bucket = 0;

        try {
            // bucket é sempre adicionado ao fim do arquivo
            // de índices, portanto o seek é feito para o
            // tamanho do arquivo, que é o ponto de início
            // do bucket
            endereco_inicio_bucket = raf.length();
            raf.seek(endereco_inicio_bucket);

            raf.write( new Bucket(profundidade_local, tam_bucket).serializar_bucket() );

            // atualizar a quantidade de buckets no arquivo
            setQtdBuckets(++qtd_buckets);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return endereco_inicio_bucket;
    }

    // inserir um registro em um bucket
    // params: cpf, número do bucket onde inserir o registro e
    // o número do registro no arquivo de dados
    public int inserir_registro(int cpf, int num_bucket, int num_registro) {
        // caminhar até o ponto de início do bucket:
        // após os metadados, necessário percorrer todos os buckets até o bucket de interesse.
        // bucket tem tam_bucket registros de tamanho SIZEOF_REGISTRO_DO_BUCKET, portanto:
        // tamanho_dos_metadados_do_indice(SIZEOF_METADADOS_INDICE) + (num_bucket - 1) * (tam_bucket * tamanho_do_registro_do_bucket + tamanho_metadados_bucket)
        long calcular_pos_bucket = calcular_pos_bucket(num_bucket);

        int profundidade_do_bucket = -1;
        int ocupacao = -1;
        long pos_apos_metadados_do_bucket = 0;

        try {
            raf.seek(calcular_pos_bucket);

            // ler metadados do bucket
            profundidade_do_bucket = raf.readInt();
            ocupacao = raf.readInt();
            // armazenar posição após os metadados do bucket:
            // indica onde começa o armazenamento dos registros
            // no bucket
            pos_apos_metadados_do_bucket = raf.getFilePointer();
            System.out.println("tam_bucket: " + tam_bucket);

            // se o bucket estiver cheio
            if (ocupacao == tam_bucket) {
                if (profundidade_do_bucket == profundidade_global) {
                    return -1;
                } else {
                    criarNovoBucket(++profundidade_do_bucket);

                    raf.seek(calcular_pos_bucket);
                    raf.writeInt(profundidade_do_bucket);

                    return profundidade_do_bucket;
                }
            } else {
                // ir até o fim do último registro armazenado
                // no bucket, que é o ponto de inserção do novo registro
                raf.seek(pos_apos_metadados_do_bucket + (ocupacao * SIZEOF_REGISTRO_DO_BUCKET));

                // escrever registro na posição encontrada
                raf.write( new RegistroDoBucket(false, cpf, num_registro).toByteArray() );

                // atualizar ocupacao
                raf.seek(pos_apos_metadados_do_bucket - 4);
                raf.writeInt(++ocupacao);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // divide o bucket num_bucket e redistribui os registros.
    // primeiro altera a lápide de todos os registros para true e depois
    // muda a ocupacao para 0, depois reinsere os registros entre o
    // num_bucket e o outro bucket
    public void dividir_bucket(int num_bucket, Diretorio diretorio) {
        try {
            // move o cursor do arquivo para o começo do bucket
            long pos = calcular_pos_bucket(num_bucket);
            raf.seek(pos);

            int profundidade_do_bucket = raf.readInt();
            int ocupacao = raf.readInt();

            int _ocupacao = ocupacao;
            int novo_num_bucket;

            RegistroDoBucket[] registros = getBucket(pos);

            // para cada registro do bucket, o deletar alterando a lapide para true
            for (int i = 0; i < ocupacao; i++) {
                raf.seek(pos + SIZEOF_METADADOS_BUCKET + (i*SIZEOF_REGISTRO_DO_BUCKET));
                raf.writeBoolean(true);
            }

            // alterar a ocupacao para 0 pois todos os registros foram deletados
            raf.seek(pos + 4); // pos + tam(profundidade_do_bucket)
            raf.writeInt(0);

            for (RegistroDoBucket registro : registros) {
                novo_num_bucket = diretorio.getPaginaIndice(registro.getChave());
                inserir_registro(registro.getChave(), novo_num_bucket, registro.getNumRegistro());
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private long calcular_pos_bucket(int num) {
        return SIZEOF_METADADOS_INDICE + (num - 1) * (tam_bucket * SIZEOF_REGISTRO_DO_BUCKET + SIZEOF_METADADOS_BUCKET);
    }
}
