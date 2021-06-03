package trabalho_aed_prontuario.indice;

import java.io.RandomAccessFile;

import java.io.IOException;

public class Indice {
    // tamanho, em bytes, de um único registro do bucket
    // boolean para lápide + int para chave + int para o número do registro
    private static final byte SIZEOF_REGISTRO_DO_BUCKET = RegistroDoBucket.SIZEOF_REGISTRO_DO_BUCKET;
    // tamanho, em bytes, dos metadados no arquivo de indice
    private static final byte SIZEOF_METADADOS_INDICE = 8;
    // tamanho, em bytes, dos metadados de um bucket
    private static final byte SIZEOF_METADADOS_BUCKET = 8;

    private RandomAccessFile raf;
    private Diretorio diretorio;

    private int tam_bucket;
    private int qtd_buckets; // quantidade de buckets presente no arquivo

    public Indice() {
        this(1, 1);
    }

    // caso o arquivo exista os metadados são lidos
    // e os parâmetros passados ao construtor são ignorados
    // caso o arquivo NÃO exista é necessário adicionar
    // os metadados no início do arquivo
    public Indice(int profundidade_global, int tam_bucket) {
        if (profundidade_global < 0) {
            System.out.println("Profundidade global inválida, assumindo valor 1...");
            profundidade_global = 1;
        }

        try {
            raf = new RandomAccessFile("indice.db", "rws");
            // se possuir dados, significa que o arquivo já
            // contém a estrutura básica
            if (raf.length() > 0) {
                ler_metadados();
            } else {
                this.tam_bucket = tam_bucket;
                this.qtd_buckets = 0;

                escrever_metadados();

                // criar 2^p_global buckets iniciais
                for (int i = 0; i < Math.pow(2, profundidade_global); i++) {
                    inserirNovoBucketNoArquivo(profundidade_global);
                }
            }

            // instanciar diretorio
            diretorio = new Diretorio(profundidade_global, tam_bucket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // a classe é responsável por alterar a variável qtd_buckets
    // portanto o setter é privado
    private void aumentarQtdBucketsNoArquivo() {
        try {
            raf.seek(SIZEOF_METADADOS_INDICE - 4);
            raf.writeInt(++qtd_buckets);
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
            tam_bucket = raf.readInt();
            qtd_buckets = raf.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // insere um novo bucket com valores default no arquivo
    // de índice; retorna o número do novo bucket
    private int inserirNovoBucketNoArquivo(int profundidade_local) {
        try {
            // bucket é sempre adicionado ao fim do arquivo
            // de índices, portanto o seek é feito para o
            // tamanho do arquivo, que é o ponto de início
            // do bucket
            raf.seek( raf.length() );

            // escrever no arquivo os dados do novo bucket
            raf.write( new Bucket(profundidade_local, tam_bucket).serializarBucket() );

            // atualizar a quantidade de buckets no arquivo
            aumentarQtdBucketsNoArquivo();
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
        // necessário pular os metadados do arquivo
        // e pular os buckets anteriores ao de número num_bucket;
        // para isso pula-se num_bucket - 1 buckets, que são
        // compostos por metadados de tamanho SIZEOF_METADADOS_BUCKET
        // e tam_bucket registros
        return SIZEOF_METADADOS_INDICE + (num_bucket - 1) * (tam_bucket * (long)SIZEOF_REGISTRO_DO_BUCKET + SIZEOF_METADADOS_BUCKET);
    }

    // retornar um bucket com as informações lidas
    // do arquivo a partir do seu número, que é passado
    // como argumento
    public Bucket carregarBucketDoArquivoDeIndice(int num_bucket) {
        return carregarBucketDoArquivoDeIndice( calcularPosBucket(num_bucket) );
    }

    // retornar um bucket com as informações lidas
    // do arquivo a partir da sua posição de início, passada
    // como argumento
    public Bucket carregarBucketDoArquivoDeIndice(long pos_inicio) {
        try {
            // ir até o início do bucket
            raf.seek(pos_inicio);

            // ler bucket inteiro
            byte[] bucket_em_bytes = new byte[SIZEOF_METADADOS_BUCKET + tam_bucket * SIZEOF_REGISTRO_DO_BUCKET];
            raf.read(bucket_em_bytes);

            // retornar novo bucket com as informações lidas
            return new Bucket(tam_bucket, bucket_em_bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void removerRegistro(int cpf) {
        int num_bucket = diretorio.getPaginaIndice(cpf);

        try {
            Bucket bucket = carregarBucketDoArquivoDeIndice(num_bucket);
            bucket.removerRegistro(cpf);

            escreverBucketDaMemoriaProArquivo(bucket, num_bucket);

            System.out.println("Bucket removido: " + bucket.toString());
        } catch(Exception err) {
            err.printStackTrace();
        }
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

        // carregar bucket de número num_bucket do arquivo
        // para a memória principal
        Bucket bucket = carregarBucketDoArquivoDeIndice(num_bucket);

        // checar o status de uma próxima inserção no bucket
        // que está em memória
        StatusDeInsercao status = bucket.obterStatusDeUmaNovaInsercao(diretorio.getProfundidade());

        if (status == StatusDeInsercao.TUDO_OK) {
            bucket.inserirRegistro( new RegistroDoBucket(cpf, num_registro) );
            escreverBucketDaMemoriaProArquivo(bucket, num_bucket);

            return StatusDeInsercao.TUDO_OK;

        } else if (status == StatusDeInsercao.DUPLICAR_DIRETORIO) {
            diretorio.duplicar();
        }

        // o código abaixo é executado tanto em caso de duplicação
        // do diretório quanto em caso de necessidade de rearranjar
        // os registros do bucket

        // obter novos dois buckets com os registros do
        // bucket em memória já distribuídos
        Bucket[] novos_buckets = distribuirRegistrosDoBucket(bucket, num_bucket);

        // inserir novo registro
        if ( diretorio.getPaginaIndice(cpf) == num_bucket)
            novos_buckets[0].inserirRegistro( new RegistroDoBucket(cpf, num_registro) );
        else
            novos_buckets[1].inserirRegistro( new RegistroDoBucket(cpf, num_registro) );

        // colocar os buckets com as alterações feitas no arquivo
        escreverBucketDaMemoriaProArquivo(novos_buckets[0], num_bucket);
        escreverBucketDaMemoriaProArquivo(novos_buckets[1], qtd_buckets + 1);

        // alterar a quantidade de buckets no arquivo
        aumentarQtdBucketsNoArquivo();

        return StatusDeInsercao.TUDO_OK;
    }

    // obtém os registros do bucket passado como argumento
    // e os distribui entre dois buckets;
    // retorna os dois buckets contendo os registros
    // já distribuídos
    private Bucket[] distribuirRegistrosDoBucket(Bucket bucket, int num_bucket) {
        int profundidade_novo_bucket = bucket.getProfundidadeLocal() + 1;

        // número do novo bucket corresponde
        // ao número de buckets presentes no
        // arquivo, já que os números de buckets
        // começam em 1
        int num_novo_bucket = qtd_buckets + 1;

        // carregar novo bucket para a memória principal
        Bucket novo_bucket = new Bucket(profundidade_novo_bucket, tam_bucket);

        // referenciar novo bucket no diretório
        diretorio.reorganizar(num_bucket, num_novo_bucket, profundidade_novo_bucket);

        // registros a serem reorganizados
        RegistroDoBucket[] registros = bucket.getRegistrosDoBucket();

        // "zerar" bucket para reorganizar os registros
        bucket = new Bucket(profundidade_novo_bucket, tam_bucket);

        // reorganizar registros do bucket
        for (RegistroDoBucket registro : registros) {
            // calcular hash para identificar o novo número do bucket
            // onde inserir o registro
            if (diretorio.getPaginaIndice(registro.getChave()) == num_bucket)
                bucket.inserirRegistro(registro);
            else
                novo_bucket.inserirRegistro(registro);
        }
        registros = null;

        return new Bucket[] {bucket, novo_bucket};
    }

    // escrever os bytes que representam um bucket
    // da memória para o arquivo de índices
    private void escreverBucketDaMemoriaProArquivo(Bucket bucket, int num_bucket) {
        try {
            raf.seek(calcularPosBucket(num_bucket));
            raf.write( bucket.serializarBucket() );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // obter o número do registro associado
    // ao cpf no registro do bucket; retorna -1
    // caso não encontre o cpf
    public int getNumRegistro(int cpf) {
        // obter num bucket a partir do cpf
        int num_bucket = diretorio.getPaginaIndice(cpf);
        // carregar bucket e procurar por cpf
        Bucket bucket = carregarBucketDoArquivoDeIndice(num_bucket);
        for (RegistroDoBucket registro : bucket.getRegistrosDoBucket()) {
            if (registro.getChave() == cpf) {
                return registro.getNumRegistro();
            }
        }
        return -1;
    }

    public void imprimirArquivo() {
        try {
            raf.seek(0);
            int tamBucket = raf.readInt();
            int qtdBuckets = raf.readInt();

            System.out.println("======= ÍNDICE =========");
            System.out.println("[Cabeçalho]");
            System.out.println("Tamanho do bucket: " + tamBucket);
            System.out.println("Quandidade de buckets: " + qtd_buckets);

            System.out.println("[Registros]");
            for (int i = 1; i <= qtdBuckets; i++) {
                System.out.println("[Bucket " + i + "] ");
                System.out.println( carregarBucketDoArquivoDeIndice(i) );
            }
        } catch(Exception err) {
            err.printStackTrace();
        }
    }
}
