package trabalho_aed_prontuario.indice;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Bucket {
    // tamanho, em bytes, de um registro do bucket
    private static final byte SIZEOF_REGISTRO_BUCKET = 9;

    private int profundidade_local;
    private int ocupacao;
    private final int tam_bucket; // tamanho máximo do bucket
    private final RegistroDoBucket[] registros;

    public Bucket(int tam_bucket) {
        this(1, tam_bucket); // profundidade default é 1
    }

    public Bucket(int profundidade_local, int tam_bucket) {
        if (profundidade_local > 0)
            this.profundidade_local = profundidade_local;
        else 
            this.profundidade_local = 1;

        if (tam_bucket > 0)
            this.tam_bucket = tam_bucket;
        else 
            this.tam_bucket = 10;

        this.ocupacao = 0; // ocupação inicial é 0
        registros = new RegistroDoBucket[tam_bucket];

        // inicializar registros do bucket com valores
        // default
        for (int i = 0; i < this.tam_bucket; i++) {
            registros[i] = new RegistroDoBucket();
        }
    }

    // criar novo bucket a partir de informações vindas do arquivo
    // de índices
    protected Bucket(int tam_bucket, int profundidade_local, int ocupacao, byte[] registros_do_bucket_em_bytes) {
        this.tam_bucket = tam_bucket;
        this.profundidade_local = profundidade_local;
        this.ocupacao = ocupacao;
        registros = new RegistroDoBucket[tam_bucket];

        try (ByteArrayInputStream bais = new ByteArrayInputStream(registros_do_bucket_em_bytes);
             DataInputStream dis = new DataInputStream(bais);
            ) {
            byte[] registro_em_bytes = new byte[SIZEOF_REGISTRO_BUCKET];

            // ler registros do bucket
            for (int i = 0; i < ocupacao; i++) {
                dis.read(registro_em_bytes);
                registros[i] = new RegistroDoBucket(registro_em_bytes);
            }

            // inicializar restante dos registros do bucket
            // com valores default
            for (int i = ocupacao; i < this.tam_bucket; i++) {
                registros[i] = new RegistroDoBucket();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getProfundidadeLocal() {
        return profundidade_local;
    }

    public void setProfundidadeLocal(int profundidade_local) {
        this.profundidade_local = profundidade_local;
    }

    public int getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(int ocupacao) {
        this.ocupacao = ocupacao;
    }

    public RegistroDoBucket[] getRegistrosDoBucket() {
        return registros;
    }

    // retorna o status de uma nova inserção que 
    // aconteça no bucket com as informações passadas
    // como argumento;
    public static StatusDeInsercao obterStatusDeUmaNovaInsercao(int ocupacao, int p_local, int p_global, int tam_bucket) {
        if (ocupacao == tam_bucket) {
            if (p_local == p_global) {
                // necessário duplicar o bucket
                System.out.println("necessário duplicar dir");
                return StatusDeInsercao.DUPLICAR_DIRETORIO;
            } else {
                // necessário criar novo bucket e rearranjar 
                // os registros do bucket em questão
                System.out.println("necessário criar novo bucket!");
                System.out.println("necessário rearranjar chaves!");

                return StatusDeInsercao.REARRANJAR_CHAVES;
            }
        } else {
            // tudo ok, inserção pode ser feita
            return StatusDeInsercao.TUDO_OK;
        }
    }

    // serializar o bucket no seu estado atual:
    // insere a profundidade_local, a ocupacao
    // e os registros do bucket(todos, incluindo 
    // os que foram inicializados com valores default)
    public byte[] serializarBucket() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            // escrever metadados do bucket
            dos.writeInt(profundidade_local);
            dos.writeInt(ocupacao);
            // escrever registros do bucket
            for (int i = 0; i < tam_bucket; i++) {
                dos.write( registros[i].toByteArray() );
            }
            dos.flush();
            dos.close();
            dos = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    // serializa um único registro: retorna um registro 
    // em um array de bytes
    // recebe o número do registro no bucket(1 para o primeiro,
    // 2 para o segundo registro, etc)
    public byte[] serializarRegistro(int num_registro_no_bucket) {
        // se número do registro inválido, não há o que serializar
        if (num_registro_no_bucket <= 0 || num_registro_no_bucket > tam_bucket)
            return null;
        return registros[num_registro_no_bucket-1].toByteArray();   
    }
}
