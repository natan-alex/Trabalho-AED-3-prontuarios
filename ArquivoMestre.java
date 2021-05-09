package trabalho_aed_prontuario.mestre;

import java.io.RandomAccessFile;

import java.io.IOException;
import java.io.EOFException;

public class ArquivoMestre {
    private short num_bytes_anotacoes;
    private RandomAccessFile raf;
    private int num_registros_no_arquivo;

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
            }
            System.out.println("num_bytes_anotacoes: " + this.num_bytes_anotacoes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ler metadados em caso de o arquivo já existir
    // metadados: número de registros presentes no arquivo e
    // o número de bytes total para as anotações
    private void ler_metadados() {
        try {
            this.num_registros_no_arquivo = raf.readInt();
            this.num_bytes_anotacoes = raf.readShort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // escrever metadados em caso de o arquivo NÃO existir
    private void escrever_metadados() {
        try {
            raf.writeInt(0); // número de registros no arquivo inicialmente é 0
            raf.writeShort(num_bytes_anotacoes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // inserir um registro no fim do arquivo de dados
    public int inserir_registro(Prontuario registro) {
        try {
            // num_registros_no_arquivo corresponde também ao último
            // id usado, já que o primeiro id começa em 1 e é auto
            // increment, portanto pode ser usado para verificar
            // se o id, que é o número de instancias feitas em 
            // Prontuario, do registro passado como argumento 
            // possibilita a inserção do registro
            if (Prontuario.getInstancias() > num_registros_no_arquivo) {
                System.out.println("tam arquivo mestre: " + raf.length());
                raf.seek( raf.length() ); // ir para o fim do arquivo 

                // inserir o tamanho em bytes do 
                // registro e depois o registro
                byte[] registro_em_bytes = registro.toByteArray();
                raf.writeShort( registro_em_bytes.length );
                raf.write(registro_em_bytes);
                registro_em_bytes = null;

                // atualizar o número de registros presentes no arquivo
                raf.seek(0);
                raf.writeInt(++num_registros_no_arquivo);
            } else {
                // registro ja existe no arquivo
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return num_registros_no_arquivo;
    }
}
