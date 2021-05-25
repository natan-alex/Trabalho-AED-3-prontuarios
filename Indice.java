package trabalho_aed_prontuario.indice;

import java.io.EOFException;
import java.io.RandomAccessFile;

import java.io.IOException;

public class Indice {
    // tamanho, em bytes, de um único registro do bucket
    // boolean para lápide + int para chave + int para o número do registro
    private static final byte SIZEOF_REGISTRO_DO_BUCKET = 9;
    // tamanho, em bytes, dos metadados no arquivo de indice
    private static final byte SIZEOF_METADADOS_INDICE = 12;
    // tamanho, em bytes, dos metadados de um bucket
    private static final byte SIZEOF_METADADOS_BUCKET = 8;

    private RandomAccessFile raf;
    private Diretorio diretorio;

    private int profundidade_global;
    private int tam_bucket;
    private int qtd_buckets; // quantidade de buckets presente no arquivo

    public Indice() {
        this(0, 0);
    }

    // caso o arquivo exista os metadados são lidos
    // e os parâmetros passados ao construtor são ignorados
    // caso o arquivo NÃO exista é necessário adicionar
    // os metadados no início do arquivo
    public Indice(int profundidade_global, int tam_bucket) {
        try {
            raf = new RandomAccessFile("indice.db", "rws");
            // se possuir dados, significa que o arquivo já
            // contém a estrutura básica
            if (raf.length() > 0) {
                ler_metadados();
            } else {
                this.profundidade_global = profundidade_global;
                this.tam_bucket = tam_bucket;
                this.qtd_buckets = 0;
                escrever_metadados();

                // criar 2^p_global buckets iniciais
                for (int i = 0; i < Math.pow(2, this.profundidade_global); i++) {
                    inserirNovoBucketNoArquivo(profundidade_global);
                }
            }

            // instanciar diretorio
            diretorio = new Diretorio(profundidade_global, tam_bucket);
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
    // retorna o número do novo bucket
    private int inserirNovoBucketNoArquivo(int profundidade_local) {
        long pos_inicio_bucket = -1;

        try {
            // bucket é sempre adicionado ao fim do arquivo
            // de índices, portanto o seek é feito para o
            // tamanho do arquivo, que é o ponto de início
            // do bucket
            pos_inicio_bucket = raf.length();
            raf.seek(pos_inicio_bucket);

            // escrever no arquivo os dados do novo bucket
            raf.write( new Bucket(profundidade_local, tam_bucket).serializarBucket() );

            // atualizar a quantidade de buckets no arquivo
            setQtdBuckets(++qtd_buckets);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // qtd_buckets corresponde, também, ao número do bucket
        // que foi criado
        return qtd_buckets;
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

    // inserir um registro em um bucket;
    // params: cpf, número do bucket onde inserir o registro e
    // o número do registro no arquivo de dados.
    public StatusDeInsercao inserirRegistro(int cpf, int num_registro) {
        // se dados do registro forem inválidos, o
        // registro é inválido
        if (cpf <= 0 || num_registro <= 0)
            return StatusDeInsercao.REGISTRO_INVALIDO;

        // calcular número do bucket onde inserir o
        // registro a partir da hash feita em diretório
        int num_bucket = diretorio.getPaginaIndice(cpf);
        System.out.println("\n\n" + cpf + " % " + (Math.pow(2, profundidade_global)) + " -> bucket " + num_bucket);

        // calcular posição de inicio do bucket no arquivo
        long pos_bucket = calcularPosBucket(num_bucket);
        System.out.println("pos_bucket em inserirRegistro: " + pos_bucket);

        // status inicial considerando que haja alguma
        // IOException 
        StatusDeInsercao status = StatusDeInsercao.IOEXCEPTION_LANCADA;

        try {
            // caminhar até o ponto de início do bucket
            raf.seek(pos_bucket);
            System.out.println("pos_bucket em inserirRegistro: " + pos_bucket);

            // ler metadados do bucket
            int profundidade_local = raf.readInt();
            int ocupacao_do_bucket = raf.readInt();

            // checar o status de uma próxima inserção no bucket 
            // de número num_bucket
            status = Bucket.obterStatusDeUmaNovaInsercao(ocupacao_do_bucket, profundidade_local, profundidade_global, tam_bucket);
            System.out.println("status: " + status);

            if (status == StatusDeInsercao.TUDO_OK) {
                inserirRegistroSimples(pos_bucket, new RegistroDoBucket(cpf, num_registro));

            } else if (status == StatusDeInsercao.DUPLICAR_DIRETORIO) {
                diretorio.duplicar();
                int nova_p_global = diretorio.getProfundidade();
                // alterar a profundidade global do indice
                setProfundidadeGlobal(nova_p_global);
                // alterar a profundidade do bucket que ocasionou
                // a duplicação do diretório
                raf.seek(pos_bucket);
                raf.writeInt(nova_p_global);
                // obter o número do novo bucket a partir
                // da sua criação no arquivo de indice
                int num_novo_bucket = inserirNovoBucketNoArquivo(nova_p_global);
                // referenciar novo bucket no diretório
                diretorio.reorganizar(num_bucket, num_novo_bucket, nova_p_global);
                // reorganizar chaves do bucket
                dividirBucket(num_bucket);
                // calcular bucket onde inserir o registro
                num_bucket = diretorio.getPaginaIndice(cpf);
                // inserir novo registro
                inserirRegistroSimples( calcularPosBucket(num_bucket), new RegistroDoBucket(cpf, num_registro) );

            } else if (status == StatusDeInsercao.REARRANJAR_CHAVES) { // somente dividir
                int nova_p_local = profundidade_local + 1;
                // criar novo bucket com a nova profundidade
                int num_novo_bucket = inserirNovoBucketNoArquivo(nova_p_local);
                // alterar a profundidade do bucket atual
                raf.seek(pos_bucket);
                raf.writeInt(nova_p_local);
                // referenciar novo bucket no diretório
                diretorio.reorganizar(num_bucket, num_novo_bucket, nova_p_local);
                // reorganizar chaves do bucket
                dividirBucket(num_bucket);
                // obter número do bucket onde inserir o registro
                num_bucket = diretorio.getPaginaIndice(cpf);
                // inserir novo registro
                inserirRegistroSimples( calcularPosBucket(num_bucket), new RegistroDoBucket(cpf, num_registro) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }

    // insere um novo registro na primeira posição do bucket
    // que estiver vazia, ou seja, que o cpf == -1
    private boolean inserirRegistroSimples(long pos_inicio_bucket, RegistroDoBucket registro) {
        boolean deu_certo = false;
        try {
            // ler ocupacao do bucket
            raf.seek(pos_inicio_bucket + 4);
            int ocupacao_do_bucket = raf.readInt();

            // pular para o primeiro registro do bucket
            long posicao_primeiro_registro = pos_inicio_bucket + SIZEOF_METADADOS_BUCKET;

            long posicao_de_insercao = posicao_primeiro_registro;
            boolean is_lapide = raf.readBoolean();
            int cpf_lido = raf.readInt();
            int num_cpfs_lidos = 1;

            // percorrer o bucket até encontrar uma posição vazia
            // para inserir o novo registro;
            // posição vazia é a que contém cpf == -1
            while (cpf_lido != -1 && !is_lapide) {
                raf.seek(posicao_primeiro_registro + (num_cpfs_lidos * (long) SIZEOF_REGISTRO_DO_BUCKET) );
                posicao_de_insercao = raf.getFilePointer();
                is_lapide = raf.readBoolean();
                cpf_lido = raf.readInt();
                num_cpfs_lidos++;
            }
            // inserir novo registro no bucket
            raf.seek(posicao_de_insercao);
            raf.write(registro.toByteArray());
            // atualizar ocupacao no arquivo
            raf.seek(pos_inicio_bucket + 4);
            raf.writeInt(++ocupacao_do_bucket);
            deu_certo = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deu_certo;
    }

    // divide o bucket num_bucket e redistribui os registros.
    // primeiro altera a lápide de todos os registros para true e depois
    // muda a ocupacao para 0, depois reinsere os registros entre o
    // num_bucket e o outro bucket
    public void dividirBucket(int num_bucket) {
        try {
            // move o cursor do arquivo para o começo do bucket
            long pos = calcularPosBucket(num_bucket);

            int novo_num_bucket, i = 0;

            // carregar registros do bucket para a memória principal
            RegistroDoBucket[] registros_do_bucket = getBucketDoArquivoDeIndice(pos).getRegistrosDoBucket();

            // para cada registro do bucket, o deletar alterando a lapide para true
            for (RegistroDoBucket registro : registros_do_bucket) {
                raf.seek(pos + SIZEOF_METADADOS_BUCKET + (i * (long) SIZEOF_REGISTRO_DO_BUCKET));
                raf.writeBoolean(true);
                i++;
            }

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
                inserirRegistroSimples( calcularPosBucket(novo_num_bucket), registro);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void imprimirArquivo() {
        try {
            raf.seek(0);
            int profundidadeGlobal = raf.readInt();
            int tamBucket = raf.readInt();
            int qtdBuckets = raf.readInt();

            System.out.println("======= ÍNDICE =========");
            System.out.println("[Cabeçalho]");
            System.out.println("Profundidade global: " + profundidadeGlobal);
            System.out.println("Tamanho do bucket: " + tamBucket);
            System.out.println("Quandidade de buckets: " + qtd_buckets);

            System.out.println("[Registros]");
            Bucket bucket;
            for (int i = 0; i < qtdBuckets; i++) {
                System.out.println("[Bucket " + (i + 1) + "] ");
                bucket = getBucketDoArquivoDeIndice(SIZEOF_METADADOS_INDICE + i*(SIZEOF_METADADOS_BUCKET + (tam_bucket*SIZEOF_REGISTRO_DO_BUCKET)));
                System.out.println("Profundidade local: " + bucket.getProfundidadeLocal());
                System.out.println("Ocupacao: " + bucket.getOcupacao());
                for (RegistroDoBucket registro : bucket.getRegistrosPopuladosDoBucket()) {
                    System.out.println(registro);
                }
            }
        } catch(Exception err) {
            err.printStackTrace();
        }
    }

    public void mostrarDiretorio() {
        int i = 0;
        System.out.println("MOSTRAR DIRETORIO");
        try {
            RandomAccessFile raf_dir = new RandomAccessFile("diretorio.db", "r");
            System.out.println("Pglobal: " + raf_dir.readInt());
            while (true) {
                System.out.println(i + "->" + raf_dir.readInt());
                i++;
            }
        } catch (EOFException e) {
            System.out.println("============");
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
}
