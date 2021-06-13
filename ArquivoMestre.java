package trabalho_aed_prontuario.mestre;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

import java.io.IOException;

public class ArquivoMestre {
    private static final int MAX_REGISTROS_MEMORIA = 1000;
    private static int num_registros_memoria = 0;
    private ByteArrayOutputStream registros_bytes_memoria;

    // num_bytes_anotacoes: 2, num_registros_no_arquivo: 4,
    // prox_id: 4, prox_id_vazio: 4 bytes, ultimo_id_vazio: 4 bytes;
    private static final int TAM_CABECALHO = 18;

    private RandomAccessFile raf;

    // id: 4 bytes, prox_id_vazio: 4 bytes, cpf: 4 bytes,
    // nome: 50 bytes, data: 4 bytes, sexo: 2 bytes, lápide: 1 byte
    private int tam_registro_completo = 69;
    // somente o tamanho de um registro, excluindo seus "metadados"(id, prox_id_vazio, lapide)
    private int tam_registro = 60;

    // atributos abaixo também são os metadados
    // do arquivo
    private short num_bytes_anotacoes;
    private int num_registros_no_arquivo;
    private int prox_id;
    private int prox_id_vazio;
    private int ultimo_id_vazio;

    public ArquivoMestre(String nome_do_arquivo, short num_bytes_anotacoes) {
        registros_bytes_memoria = new ByteArrayOutputStream();

        try {
            raf = new RandomAccessFile(nome_do_arquivo, "rws");
            // se o arquivo tiver algo, ignorar o argumento num_bytes_anotacoes
            // e obter o número de bytes para as anotações por meio do metadado
            // referente a ele. Caso contrário, checar se o argumento é positivo
            // e atribuir à variável num_bytes_anotacoes. Caso o argumento seja negativo o
            // tamanho default é de 100 bytes
            if (raf.length() > 0) {
                ler_metadados();
            } else {
                this.num_bytes_anotacoes = num_bytes_anotacoes;
                escrever_metadados();
                prox_id = 1;
                ultimo_id_vazio = prox_id_vazio = -1;
                num_registros_no_arquivo = 0;
            }
            tam_registro_completo += this.num_bytes_anotacoes;
            tam_registro += this.num_bytes_anotacoes;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTamRegistroCompleto() {
        return tam_registro_completo;
    }

    public short getNumBytesAnotacoes() {
        return num_bytes_anotacoes;
    }

    public int getNumRegistrosMemoria() {
        return num_registros_memoria;
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
            this.prox_id_vazio = raf.readInt();
            this.ultimo_id_vazio = raf.readInt();
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
            raf.writeInt(-1); // id do primeiro registro vazio
            raf.writeInt(-1); // id do ultimo registro vazio
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flushRegistrosMemoria() {
        try {
            raf.seek(TAM_CABECALHO + ((long)num_registros_no_arquivo * tam_registro_completo));
            raf.write(registros_bytes_memoria.toByteArray());

            registros_bytes_memoria.reset();

            raf.seek(6);
            raf.writeInt(prox_id);

            raf.seek(0);
            num_registros_no_arquivo += num_registros_memoria;
            raf.writeInt(num_registros_no_arquivo);

            num_registros_memoria = 0;
        } catch(Exception err) {
            err.printStackTrace();
        }
    }

    // inserir um registro no fim do arquivo de dados ou
    // em um registro logicamente deletado;
    // verifica o prox_id_vazio do registro e insere nele
    // caso tenha um valor, caso contrário, insere no fim
    // do arquivo;
    // retorna o número do registro inserido
    public int inserirRegistro(Prontuario registro) {
        // cortar tamanho das anotacoes, caso necessario,
        // antes de inserir o registro
        String anotacoes = registro.getAnotacoes();
        if (anotacoes.length() > num_bytes_anotacoes)
            registro.setAnotacoes(anotacoes.substring(0, num_bytes_anotacoes));

        try {
            if (prox_id_vazio == -1) {
                byte[] registro_em_bytes = registro.toByteArrayComCabelho(prox_id, -1, false);
                byte[] padded_registro_bytes = new byte[tam_registro_completo];

                System.arraycopy(registro_em_bytes, 0, padded_registro_bytes, 0, registro_em_bytes.length);
                registros_bytes_memoria.write(padded_registro_bytes);
                num_registros_memoria++;

                if (num_registros_memoria == MAX_REGISTROS_MEMORIA) {
                    flushRegistrosMemoria();
                }

                prox_id++;
                return prox_id - 1;
            } else {
                // vai para o registro de prox_id_removido para pegar o
                // prox_prox_id_removido e assim modificar o cabecalho com este valor
                // assim, na proxima insercao, o proximo proximo id removido será reutilizado
                long pos_registro_removido = calcularPosicaoDoRegistro(prox_id_vazio);
                raf.seek(pos_registro_removido); // pular o id do registro removido, que será reaproveitado
                int id_registro = raf.readInt();
                prox_id_vazio = raf.readInt(); // prox_prox_id_removido

                // atualizar prox_id_removido do cabecalho
                raf.seek(TAM_CABECALHO - 8);
                raf.writeInt(prox_id_vazio);

                // atualizar número de registros
                raf.seek(0);
                raf.writeInt(++num_registros_no_arquivo);

                // voltar ao registro e alterar seus "metadados"
                raf.seek(pos_registro_removido + 4);
                raf.writeInt(-1); // prox_id_vazio
                raf.writeBoolean(false); // lapide

                // obter registro em bytes para ser inserido
                byte[] registro_em_bytes = registro.toByteArray();
                raf.write(registro_em_bytes); // registro
                registro_em_bytes = null;

                // retorna o id do registro de número prox_id_vazio,
                // que corresponde ao número do registro que foi sobrescrito
                return id_registro;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // calcular a posição, no arquivo, de um registro a
    // partir de seu número; retorna -1 caso o num_registro
    // seja inválido
    protected long calcularPosicaoDoRegistro(int num_registro) {
        if (num_registro <= 0) {
            System.out.println("Número de registro " + num_registro + " inválido.");
            return -1;
        }
        return TAM_CABECALHO + ( (long) (num_registro - 1) * (tam_registro_completo) );
    }

    // retorna o Prontuario obtido do arquivo mestre
    // dado o seu número; retorna null se o número do
    // registro for inválido ou ocorrer alguma IOException
    // OBS: num_registro vem do índice
    public Prontuario recuperarRegistro(int num_registro) {
        flushRegistrosMemoria();

        if (num_registro <= 0) {
            System.out.println("Número de registro " + num_registro + " inválido.");
            return null;
        }

        try {
            long posicao_do_registro = calcularPosicaoDoRegistro(num_registro);
            raf.seek(posicao_do_registro + 4 + 4); // +4 para pular o id, +4 para pular o prox_id_vazio
            boolean lapide = raf.readBoolean();
            if (!lapide) {
                byte[] registro = new byte[tam_registro];
                raf.read(registro);
                return new Prontuario(registro);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // remove logicamente o registro alterando o valor da lapide para true
    // atualiza o prox_id_vazio e o ultimo_id_vazio
    public void removerRegistro(int num_registro) {
        flushRegistrosMemoria();

        if (num_registro <= 0) {
            System.out.println("Número de registro " + num_registro + " inválido.");
            return;
        }

        try {
            long posicao_do_registro = calcularPosicaoDoRegistro(num_registro);
            raf.seek(posicao_do_registro);
            // ler id do registro que será deletado logicamente
            int id_registro = raf.readInt();
            int prox_id_vazio_lido = raf.readInt();
            raf.writeBoolean(true); // deleta logicamente modificando a lapide para true

            // se o prox_id_vazio for -1, nao ha nenhum registro deletado no arquivo
            // portanto, basta alterar o seu valor para o registro a ser deletado atual
            // e atribuir o mesmo valor ao ultimo_id_vazio
            if (prox_id_vazio == -1) {
                ultimo_id_vazio = prox_id_vazio = id_registro;
                raf.seek(TAM_CABECALHO - 8);
                raf.writeInt(prox_id_vazio);
                raf.writeInt(ultimo_id_vazio);
            } else {
                // vai na posicao do ultimo_id_vazio para inserir no campo de prox_id_removido
                // o id no registro a ser deletado atual
                // atribui o ultimo_id_vazio ao id do registro atual a ser deletado
                long posicao_registro_ultimo_deletado = calcularPosicaoDoRegistro(ultimo_id_vazio);
                raf.seek(posicao_registro_ultimo_deletado + 4); // +4 para pular id
                raf.writeInt(id_registro); // prox_id_deletado
                ultimo_id_vazio = id_registro;
                raf.seek(TAM_CABECALHO - 4);
                raf.writeInt(ultimo_id_vazio);
            }

            // alterar número de registros removidos
            raf.seek(0);
            raf.writeInt(--num_registros_no_arquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // com número do registro, recuperar o registro e
    // printar as informações contidas atualmente
    // e se não existir(ou for lápide) retorna false. Perguntar o
    // que o usuário quer alterar, excluindo o cpf.
    public boolean sobrescreverRegistroNoArquivo(Prontuario novo_prontuario, int num_registro) {
        flushRegistrosMemoria();

        boolean deu_certo = false;
        // cortar tamanho das anotacoes, caso necessario,
        // antes de inserir o registro
        String anotacoes = novo_prontuario.getAnotacoes();
        if (anotacoes.length() > num_bytes_anotacoes)
            novo_prontuario.setAnotacoes(anotacoes.substring(0, num_bytes_anotacoes));

        try {
            long posicao_do_registro = calcularPosicaoDoRegistro(num_registro);
            raf.seek(posicao_do_registro + 4 + 4 + 1); // +4 para pular o id, +4 para pular o prox_id_vazio, +1 lapide
            byte[] registro_em_bytes = novo_prontuario.toByteArray();
            raf.write(registro_em_bytes); // registro
            deu_certo = true;
        } catch (Exception err) {
            err.printStackTrace();
        }

        return deu_certo;
    }

    // imprime o cabeçalho e registros do arquivo mestre,
    // pulando de registro a registro
    public void imprimirArquivo() {
        flushRegistrosMemoria();

        try {
            raf.seek(0);

            int id, _prox_id_vazio;
            boolean is_lapide;
            Prontuario prontuario;
            byte[] byteArray = new byte[tam_registro];

            int num_registros = raf.readInt();
            short num_bytes_anotacoes = raf.readShort();
            int proximo_id = raf.readInt();
            int proximo_id_vazio = raf.readInt();
            int ultimo_id_vazio = raf.readInt();

            System.out.println("========== ARQUIVO MESTRE ==========");
            System.out.println("[Cabeçalho]");
            System.out.println("Número de registros: " + num_registros);
            System.out.println("Número de bytes de anotações: " + num_bytes_anotacoes);
            System.out.println("Próximo id: " + proximo_id);
            System.out.println("Próximo id vazio: " + proximo_id_vazio);
            System.out.println("Último id vazio: " + ultimo_id_vazio);

            System.out.println("[Registros]");
            for (int i = 0; i < num_registros; i++) {
                id = raf.readInt(); // 4
                _prox_id_vazio = raf.readInt(); // 4
                is_lapide = raf.readBoolean(); // 1
                System.out.print("Num registro: " + id + " | Proximo id vazio: " + _prox_id_vazio + " | Lapide: " + is_lapide + " | ");
                // ler infos do prontuario e mostrá-lo
                raf.read(byteArray);
                prontuario = new Prontuario(byteArray);
                System.out.println(prontuario);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
