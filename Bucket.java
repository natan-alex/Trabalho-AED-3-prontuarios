package trabalho_aed_prontuario.indice;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Bucket {
    private static final byte SIZEOF_REGISTRO_BUCKET = 9;

    private int profundidade_local;
    private int ocupacao;
    private int tam_bucket;
    private RegistroDoBucket[] registros;

    public Bucket(int tam_bucket) {
        this(1, tam_bucket); // profundidade default é 1
    }

    public Bucket(int profundidade_local, int tam_bucket) {
        if (profundidade_local > 0)
            this.profundidade_local = profundidade_local;
        if (tam_bucket > 0)
            this.tam_bucket = tam_bucket;
        this.ocupacao = 0; // ocupação inicial é 0
        registros = new RegistroDoBucket[tam_bucket];
        // inicializar registros do bucket com valores
        // default
        for (int i = 0; i < this.tam_bucket; i++) {
            registros[i] = new RegistroDoBucket();
        }
    }

    public Bucket(int tam_bucket, byte[] bucket_em_bytes) {
        if (tam_bucket > 0)
            this.tam_bucket = tam_bucket;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bucket_em_bytes);
             DataInputStream dis = new DataInputStream(bais);
            ) {
            byte[] registro_em_bytes = new byte[SIZEOF_REGISTRO_BUCKET];
            // ler metadados do bucket
            this.profundidade_local = dis.readInt();
            this.ocupacao = dis.readInt();
            // ler registros do bucket
            for (int i = 0; i < ocupacao; i++) {
                dis.read(registro_em_bytes);
                registros[i] = new RegistroDoBucket(registro_em_bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bucket(int tam_bucket, int profundidade_local, int ocupacao, byte[] registros_do_bucket_em_bytes) {
        if (tam_bucket > 0)
            this.tam_bucket = tam_bucket;
        if (profundidade_local > 0)
            this.profundidade_local = profundidade_local;
        if (ocupacao > 0 && ocupacao <= tam_bucket)
            this.ocupacao = ocupacao;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(registros_do_bucket_em_bytes);
             DataInputStream dis = new DataInputStream(bais);
            ) {
            byte[] registro_em_bytes = new byte[SIZEOF_REGISTRO_BUCKET];
            // ler registros do bucket
            for (int i = 0; i < ocupacao; i++) {
                dis.read(registro_em_bytes);
                registros[i] = new RegistroDoBucket(registro_em_bytes);
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

    // obter o tamanho total em bytes de um bucket qualquer
    // quando cheio
    public int get_tamanho_total_em_bytes_de_um_bucket() {
        // 4 para a profundidade_local, 4 para a ocupacao
        // e os registros
        return 4 + 4 + (tam_bucket * SIZEOF_REGISTRO_BUCKET);
    }

    // inserir um novo registro no array de registros;
    // retorna -2 caso o registro seja inválido,
    // -1 em caso de necessidade de duplicar o bucket,
    // 0 em caso de tudo ok e
    // > 0 (profundidade com que o novo bucket deve 
    // ser criado) caso seja necessário criar novo bucket 
    // e rearranjar as chaves do novo bucket e do bucket atual
    public int inserir_registro(RegistroDoBucket registro, int profundidade_global) {
        if (registro == null) {
            return -2;
        }
        if (ocupacao == tam_bucket) {
            if (profundidade_local == profundidade_global) {
                System.out.println("necessário duplicar dir");
                return -1;
            } else {
                System.out.println("necessário criar novo bucket!");
                System.out.println("necessário rearranjar chaves!");

                return ++profundidade_local;
            }
        } else {
            // inserir registro no fim do bucket
            // e atualizar a ocupacao
            registros[ocupacao++] = registro;
        }
        return 0;
    }

    // serializar o bucket no seu estado atual:
    // insere a profundidade_local, a ocupacao
    // e os ocupacao registros existentes no bucket
    public byte[] serializar_bucket() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            // escrever metadados do bucket
            dos.writeInt(profundidade_local);
            dos.writeInt(ocupacao);
            // escrever registros do bucket
            for (int i = 0; i < ocupacao; i++) {
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
    public byte[] serializar_registro(int num_registro_no_bucket) {
        // se número do registro inválido, não há o que serializar
        if (num_registro_no_bucket <= 0 || num_registro_no_bucket > tam_bucket)
            return null;
        return registros[num_registro_no_bucket-1].toByteArray();   
    }
}
