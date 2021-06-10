package trabalho_aed_prontuario.indice;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import java.util.Arrays;

public class Bucket {
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
    protected Bucket(int tam_bucket, byte[] bucket_em_bytes) {
        this.tam_bucket = tam_bucket;
        registros = new RegistroDoBucket[tam_bucket];

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bucket_em_bytes);
             DataInputStream dis = new DataInputStream(bais);
            ) {
            this.profundidade_local = dis.readInt();
            this.ocupacao = dis.readInt();
            byte[] registro = new byte[RegistroDoBucket.SIZEOF_REGISTRO_DO_BUCKET];

            // ler registros do bucket
            for (int i = 0; i < tam_bucket; i++) {
                dis.read(registro);
                registros[i] = new RegistroDoBucket(registro);
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
    // aconteça no bucket
    public StatusDeInsercao obterStatusDeUmaNovaInsercao(int profundidade_global) {
        if (ocupacao == tam_bucket) {
            if (profundidade_local == profundidade_global) {
                // necessário duplicar o bucket
                return StatusDeInsercao.DUPLICAR_DIRETORIO;
            } else {
                // necessário criar novo bucket e rearranjar
                // os registros do bucket em questão
                return StatusDeInsercao.REARRANJAR_CHAVES;
            }
        } else {
            // tudo ok, inserção pode ser feita
            return StatusDeInsercao.TUDO_OK;
        }
    }

    protected void removerRegistro(int chave) {
        // percorrer bucket até encontrar registro
        // vazio (cpf == -1)
        for (int i = 0; i < tam_bucket; i++) {
            if (registros[i].getChave() == chave) {
                registros[i].setChave(-1);
                setOcupacao(ocupacao - 1);
                i = tam_bucket;
            }
        }
    }

    protected void inserirRegistro(RegistroDoBucket registro) {
        // percorrer bucket até encontrar registro
        // vazio (cpf == -1)
        for (int i = 0; i < tam_bucket; i++) {
            if (registros[i].getChave() == -1) {
                registros[i] = registro;
                setOcupacao(ocupacao + 1);
                i = tam_bucket;
            }
        }
    }

    // serializar o bucket no seu estado atual:
    // insere a profundidade_local, a ocupacao
    // e os registros do bucket(todos, incluindo
    // os que foram inicializados com valores default)
    protected byte[] serializarBucket() {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bucket\n{");
        sb.append("\n\tprofundidade_local=");
        sb.append(profundidade_local);
        sb.append("\n\tocupacao=");
        sb.append(ocupacao);
        sb.append("\n\tregistros\n\t[\n");

        for (RegistroDoBucket registro : registros) {
            sb.append("\t\t");
            sb.append(registro);
            sb.append('\n');
        }

        sb.append("\t]\n}");
        return sb.toString();
    }
}
