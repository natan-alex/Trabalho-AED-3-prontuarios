package trabalho_aed_prontuario.mestre;

import java.io.RandomAccessFile;

import java.io.IOException;
import java.io.EOFException;

public class ArquivoMestre {
    private RandomAccessFile raf;

    // atributos abaixo também são os metadados
    // do arquivo
    private int num_registros_no_arquivo;
    private short num_bytes_anotacoes;
    private int prox_id;

    public ArquivoMestre(short num_bytes_anotacoes) {
        try {
            raf = new RandomAccessFile("arquivo_mestre.db", "rws");
            // se o arquivo tiver algo, ignorar o argumento num_bytes_anotacoes
            // e obter o número de bytes para as anotações por meio do metadado
            // referente a ele. Caso contrário, checar se o argumento é positivo
            // e atribuir à variável num_bytes_anotacoes. Caso o argumento seja negativo o
            // tamanho default é de 100 bytes
            if (raf.length() > 0) {
                ler_metadados();
            } else {
                this.num_bytes_anotacoes = (num_bytes_anotacoes > 0) ? num_bytes_anotacoes : 100;
                escrever_metadados();
                num_registros_no_arquivo = 0;
                prox_id = 1;
            }
            System.out.println("num_bytes_anotacoes: " + this.num_bytes_anotacoes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ler metadados em caso de o arquivo já existir;
    // metadados: número de registros presentes no arquivo,
    // o número de bytes total para as anotações e o último
    // id usado
    private void ler_metadados() {
        try {
            this.num_registros_no_arquivo = raf.readInt();
            this.num_bytes_anotacoes = raf.readShort();
            this.prox_id = raf.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // escrever metadados em caso de o arquivo NÃO existir
    private void escrever_metadados() {
        try {
            raf.writeInt(0); // número de registros no arquivo inicialmente é 0
            raf.writeShort(num_bytes_anotacoes);
            raf.writeInt(1); // primeiro id a ser usado é o valor 1
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // inserir um registro no fim do arquivo de dados;
    // retorna o número de registros contidos no arquivo
    // que também diz sobre o número do registro inserido
    public int inserir_registro(Prontuario registro) {
        try {
            // if (!registroJaExiste()) {
            System.out.println("tam arquivo mestre: " + raf.length());
            raf.seek( raf.length() ); // ir para o fim do arquivo

            // escrever o id antes do registro
            raf.writeInt(prox_id);

            // obter registro em bytes para ser inserido
            byte[] registro_em_bytes = registro.toByteArray();
            raf.write(registro_em_bytes); // registro
            registro_em_bytes = null;

            // atualizar o número de registros presentes no arquivo
            raf.seek(0);
            raf.writeInt(++num_registros_no_arquivo);
            // atualizar prox id
            raf.seek(6); // pular o número de registros(int) e o número
            // de bytes para as anotações(short)
            raf.writeInt(++prox_id);

            return prox_id;
            // } else {
            // registro ja existe no arquivo
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return num_registros_no_arquivo;
    }
}
