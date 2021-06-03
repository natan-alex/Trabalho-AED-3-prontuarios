package trabalho_aed_prontuario.mestre;

import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.IOException;

public class ArquivoMestre {
    private static final int TAM_CABECALHO = 10;

    private RandomAccessFile raf;

    // cpf: 4 bytes, nome: 50 bytes, date: 4 bytes, sexo: 2 bytes
    // tamanho das anotações: ? -> 60 + ?
    private int tam_registro = 60;

    // atributos abaixo também são os metadados
    // do arquivo
    private short num_bytes_anotacoes;
    private int num_registros_no_arquivo;
    private int prox_id;

    public ArquivoMestre(String nome_do_arquivo, short num_bytes_anotacoes) {
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
                this.num_bytes_anotacoes = (num_bytes_anotacoes > 0) ? num_bytes_anotacoes : 100;
                escrever_metadados();
                num_registros_no_arquivo = 0;
                prox_id = 1;
            }
            tam_registro += this.num_bytes_anotacoes;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public short getNumBytesAnotacoes() {
        return num_bytes_anotacoes;
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
    // retorna -1 em caso de falha na inserção ou
    // o valor de prox_id antes da inserção, que indica
    // o número do registro que foi inserido
    public int inserirRegistro(Prontuario registro) {
        try {
            // ir para o fim do último registro do arquivo
            raf.seek(TAM_CABECALHO + ((long)num_registros_no_arquivo * (tam_registro + 4 + 1)) );
            raf.writeBoolean(false); // is_lapide == false
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

            return prox_id - 1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // calcular a posição, no arquivo, de um registro a
    // partir de seu número; retorna -1 caso o num_registro
    // seja inválido
    protected long calcularPosicaoDoRegistro(int num_registro) {
        if (num_registro <= 0 || num_registro > num_registros_no_arquivo) {
            System.out.println("Número de registro " + num_registro + " inválido.");
            return -1;
        }
        // o tamanho do registro não considera o id e o campo lápide,
        // o que justifica o + 4(id) + 1(lápide)
        return TAM_CABECALHO + ( (long) (num_registro - 1) * (tam_registro + 4 + 1) );
    }

    // retorna o Prontuario obtido do arquivo mestre
    // dado o seu número; retorna null se o número do
    // registro for inválido ou ocorrer alguma IOException
    // OBS: num_registro vem do índice
    public Prontuario recuperarRegistro(int num_registro) {
        if (num_registro <= 0 || num_registro > num_registros_no_arquivo) {
            System.out.println("Número de registro " + num_registro + " inválido.");
            return null;
        }

        try {
            long posicao_do_registro = calcularPosicaoDoRegistro(num_registro);
            raf.seek(posicao_do_registro);
            // checar se o campo lápide != true
            if (!raf.readBoolean()) {
                int id = raf.readInt();
                byte[] registro = new byte[tam_registro];
                raf.read(registro);
                return new Prontuario(registro);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // logica: pegar o cpf de um prontuario e usar o
    // índice pra achar o número do registro no arquivo
    // mestre com esse cpf. Printar as informações contidas atualmente
    // e se não existir(ou for lápide) retorna false. Perguntar o
    // que o usuário quer alterar, excluindo o cpf.
    public boolean editarRegistro(int num_registro, Prontuario.CampoAlterado campo_alterado, String valor) {
        boolean deu_certo = false;
        Prontuario antigo = recuperarRegistro(num_registro);

        switch(campo_alterado) {
            case NOME:
                antigo.setNome(valor);
                break;
            case SEXO:
                antigo.setSexo(valor.charAt(0));
                break;
            case DATA:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate data = LocalDate.parse(valor, formatter);
                antigo.setData(data);
                break;
            case ANOTACOES:
                antigo.setAnotacoes(valor);
                break;
            default:
                System.out.println("Opção inválida.");
                break;
        }

        try {
            long posicao_do_registro = calcularPosicaoDoRegistro(num_registro);
            raf.seek(posicao_do_registro + 5); // +5 para pular o lápide e id
            byte[] registro_em_bytes = antigo.toByteArray();
            raf.write(registro_em_bytes); // registro
            deu_certo = true;
        } catch (Exception err) {
            err.printStackTrace();
        }

        System.out.println("Informaçoes do prontuario: " + antigo);
        return deu_certo;
    }

    // imprime o cabeçalho e registros do arquivo mestre,
    // pulando de registro a registro
    public void imprimirArquivo() {
        try {
            raf.seek(0);

            int num_registros = raf.readInt();
            short num_bytes_anotacoes = raf.readShort();
            int proximo_id = raf.readInt();
            int id;
            boolean is_lapide;
            Prontuario prontuario;
            byte[] byteArray = new byte[tam_registro];

            System.out.println("========== ARQUIVO MESTRE ==========");
            System.out.println("[Cabeçalho]");
            System.out.println("Número de registros: " + num_registros);
            System.out.println("Número de bytes de anotações: " + num_bytes_anotacoes);
            System.out.println("Próximo id: " + proximo_id);

            System.out.println("[Registros]");
            for (int i = 0; i < num_registros; i++) {
                is_lapide = raf.readBoolean();
                if (!is_lapide) {
                    id = raf.readInt();
                    raf.read(byteArray);
                    prontuario = new Prontuario(byteArray);

                    System.out.print("Id: " + id + " -> ");
                    System.out.println(prontuario);
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
