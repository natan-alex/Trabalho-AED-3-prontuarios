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

    // retornar um bucket com as informações lidas
    // do arquivo a partir da sua posição de início passada
    // como argumento
    public Bucket getBucketDoArquivoDeIndice(long pos_inicio) {
        try {
            // ir até o início do bucket
            raf.seek(pos_inicio);

            // ler metadados do bucket
            int profundidade_local = raf.readInt();
            int ocupacao = raf.readInt();

            // ler registros do bucket
            byte[] registros_em_bytes = new byte[ocupacao * SIZEOF_REGISTRO_DO_BUCKET];
            raf.read(registros_em_bytes);

            // retornar novo bucket com as informações lidas
            return new Bucket(tam_bucket, profundidade_local, ocupacao, registros_em_bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // insere um novo bucket com valores default no arquivo
    // de índice;
    // retorna a posição de início do bucket
    public long inserirNovoBucketNoArquivo(int profundidade_local) {
        long pos_inicio_bucket = 0;

        try {
            // bucket é sempre adicionado ao fim do arquivo
            // de índices, portanto o seek é feito para o
            // tamanho do arquivo, que é o ponto de início
            // do bucket
            pos_inicio_bucket = raf.length();
            raf.seek(pos_inicio_bucket);

            // bucket = new Bucket(profundidade_local, tam_bucket);
            // num_bucket_em_memoria = qtd_buckets + 1; // número do bucket
            // // corresponde ao número de buckets existentes no arquivo + 1,
            // // exemplo: se tiver 2 buckets, o próximo bucket tem número 3
            raf.write( new Bucket(profundidade_local, tam_bucket).serializarBucket() );

            // atualizar a quantidade de buckets no arquivo
            setQtdBuckets(++qtd_buckets);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pos_inicio_bucket;
    }

    // inserir um registro em um bucket
    // params: cpf, número do bucket onde inserir o registro e
    // o número do registro no arquivo de dados.
    // retorna -3 caso haja erro nas operações de IO,
    // -2 caso o registro seja inválido,
    // -1 em caso de necessidade de duplicar o bucket,
    // 0 em caso de tudo ok e
    // > 0, que corresponde à profundidade com que o novo bucket deve 
    // ser criado, caso seja necessário criar novo bucket 
    // e rearranjar as chaves do novo bucket e do bucket atual
    public int inserirRegistro(int cpf, int num_registro, int num_bucket) {
        // se dados do registro forem inválidos, o
        // registro é inválido
        if (cpf <= 0 || num_registro <= 0) 
            return -2;

        // calcular posição de inicio do bucket no arquivo
        long pos_bucket = calcularPosBucket(num_bucket);

        // status inicial considerando que haja alguma
        // IOException 
        int status = -3;

        try {
            // caminhar até o ponto de início do bucket
            raf.seek(pos_bucket);

            // ler metadados do bucket
            int profundidade_local = raf.readInt();
            int ocupacao_do_bucket = raf.readInt();
            System.out.println("ocupacao_do_bucket " + ocupacao_do_bucket);

            // checar o status de uma próxima inserção no bucket com os
            // argumentos passados ao método
            status = Bucket.obterStatusDeUmaNovaInsercao(ocupacao_do_bucket, profundidade_local, profundidade_global, tam_bucket);
            System.out.println("status: " + status);

            if (status == 0) {
                // ir até o fim do bucket no arquivo, que
                // é a posição de inserção do novo registro
                long posicao_de_insercao = pos_bucket + SIZEOF_METADADOS_BUCKET + ( ocupacao_do_bucket * SIZEOF_REGISTRO_DO_BUCKET );
                raf.seek( posicao_de_insercao );
                System.out.println("posicao_de_insercao " + posicao_de_insercao);
                // inserir novo registro no bucket
                raf.write( new RegistroDoBucket(cpf, num_registro).toByteArray() );
                // atualizar ocupacao no arquivo
                raf.seek(pos_bucket + 4);
                raf.writeInt(++ocupacao_do_bucket);
            } else if (status > 0) {
                // se status > 0, o status corresponde a profundidade local
                // com que o novo bucket deverá ser criado no arquivo
                inserirNovoBucketNoArquivo(status);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }

    // divide o bucket num_bucket e redistribui os registros.
    // primeiro altera a lápide de todos os registros para true e depois
    // muda a ocupacao para 0, depois reinsere os registros entre o
    // num_bucket e o outro bucket
    public void dividirBucket(int num_bucket, Diretorio diretorio) {
        try {
            // move o cursor do arquivo para o começo do bucket
            long pos = calcularPosBucket(num_bucket);
            raf.seek(pos);

            int profundidade_do_bucket = raf.readInt();
            int ocupacao = raf.readInt();

            int novo_num_bucket;

            // carregar bucket para a memória principal
            Bucket bucket = getBucketDoArquivoDeIndice(pos);

            RegistroDoBucket[] registros_do_bucket;
            registros_do_bucket = bucket.getRegistrosDoBucket();

            // para cada registro do bucket, o deletar alterando a lapide para true
            // for (RegistroDoBucket registro : registros_do_bucket) {
            //     raf.seek(pos + SIZEOF_METADADOS_BUCKET + (i*SIZEOF_REGISTRO_DO_BUCKET));
            //     raf.writeBoolean(true);
            // }

            // alterar a ocupacao para 0 pois todos os registros foram deletados
            raf.seek(pos + 4); // pos + tam(profundidade_do_bucket)
            raf.writeInt(0);

            // realocar registros nos buckets (o que está carregado em 
            // memória e o recém criado)
            for (RegistroDoBucket registro : registros_do_bucket) {
                // calcular hash para identificar o novo número do bucket
                // do registro a partir da sua chave
                novo_num_bucket = diretorio.getPaginaIndice(registro.getChave());
                // inserir registro no novo bucket
                inserirRegistro(registro.getChave(), registro.getNumRegistro(), novo_num_bucket);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    // calcular a posição de início do bucket com 
    // dado o número do bucket
    private long calcularPosBucket(int num_bucket) {
        // seek para
        // tamanho_dos_metadados_do_indice(SIZEOF_METADADOS_INDICE) + 
        // (num_bucket - 1) * [tam_bucket *
        // tamanho_do_registro_do_bucket(SIZEOF_REGISTRO_DO_BUCKET) + 
        // tamanho_metadados_bucket(SIZEOF_METADADOS_BUCKET) ]
        return SIZEOF_METADADOS_INDICE + (num_bucket - 1) * (tam_bucket * SIZEOF_REGISTRO_DO_BUCKET + SIZEOF_METADADOS_BUCKET);
    }
}
