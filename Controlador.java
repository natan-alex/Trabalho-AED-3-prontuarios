package trabalho_aed_prontuario.main;

import trabalho_aed_prontuario.indice.*;
import trabalho_aed_prontuario.indice.StatusDeInsercao;
import trabalho_aed_prontuario.mestre.*;

import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.function.Supplier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Controlador {
    private Indice indice;
    private ArquivoMestre arquivo_mestre;
    private static final int DEFAULT_P_GLOBAL = 1;
    private static final int DEFAULT_TAM_BUCKET = 10;
    private static final short DEFAULT_TAM_ANOTACOES = 100;
    private static final String DB_FILES_DIR_NAME = "db_files";
    private static final String INDICE_FILENAME = Paths.get(DB_FILES_DIR_NAME,"indice.db").toString();
    private static final String DIR_FILENAME = Paths.get(DB_FILES_DIR_NAME, "diretorio.db").toString();
    private static final String MASTER_FILENAME = Paths.get(DB_FILES_DIR_NAME, "arquivo_mestre.db").toString();

    // se o diretório DB_FILES_DIR_NAME não existir,
    // ele é criado.
    public Controlador() {
        criarDiretorioDB();
        // instancia o índice e o arquivo mestre com os valores
        // default, que serão ignorados caso os arquivos já existam
        indice = new Indice(INDICE_FILENAME, DIR_FILENAME, DEFAULT_P_GLOBAL, DEFAULT_TAM_BUCKET);
        arquivo_mestre = new ArquivoMestre(MASTER_FILENAME, DEFAULT_TAM_ANOTACOES);
    }

    // cria novos arquivos com as informações passadas como argumento;
    // deleta os arquivos existentes antes de criar novos arquivos.
    public boolean criarArquivos(int profundidade_global, int tam_bucket, int tam_anotacoes) {
        if (profundidade_global < 0) {
            System.out.println("Profundidade global " + profundidade_global + " inválida, assumindo valor " + DEFAULT_P_GLOBAL);
            profundidade_global = DEFAULT_P_GLOBAL;
        }
        if (tam_bucket <= 0) {
            System.out.println("Tamanho do bucket " + tam_bucket + " inválido, assumindo valor " + DEFAULT_TAM_BUCKET);
            tam_bucket = DEFAULT_TAM_BUCKET;
        }
        if (tam_anotacoes <= 0) {
            System.out.println("Tamanho " + tam_anotacoes + " para as anotações inválido, assumindo valor " + DEFAULT_TAM_ANOTACOES);
            tam_anotacoes = DEFAULT_TAM_ANOTACOES;
        }
        if (apagarArquivos()) {
            indice = new Indice(INDICE_FILENAME, DIR_FILENAME, profundidade_global, tam_bucket);
            arquivo_mestre = new ArquivoMestre(MASTER_FILENAME, (short) tam_anotacoes);
            return true;
        }
        return false;
    }

    // criar diretorio DB_FILES_DIR_NAME vazio se não existir;
    private void criarDiretorioDB() {
        try {
            Path path = Path.of(DB_FILES_DIR_NAME);
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // apaga os arquivos dentro de DB_FILES_DIR_NAME caso existirem
    private boolean apagarArquivos() {
        boolean deu_certo = false;
        try {
            Files.deleteIfExists(Path.of(INDICE_FILENAME));
            Files.deleteIfExists(Path.of(DIR_FILENAME));
            Files.deleteIfExists(Path.of(MASTER_FILENAME));
            deu_certo = true;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return deu_certo;
    }

    // faz as inserções de maneira geral: insere o novo
    // registro no arquivo mestre e a chave e o número do
    // registro no bucket correspondente. Retorna StatusDeInsercao
    // indicando o status final da operação. O tempo da operação é exibido
    // após sua conclusão.
    public StatusDeInsercao inserirRegistro(Prontuario prontuario) {
        Supplier<StatusDeInsercao> insere = () -> {
            if (prontuario == null)
                return StatusDeInsercao.REGISTRO_INVALIDO;
            int num_registro = arquivo_mestre.inserirRegistro(prontuario);
            return indice.inserirRegistro(prontuario.getCpf(), num_registro);
        };

        return executaEMedeTempo(insere, "inserir");
    }

    // altera as informações de um registro no arquivo mestre
    // dado o campo que deve ser alterado, o registro que será
    // alterado e o novo valor do campo. Retorna StatusDeEdicao
    // indicando o status final da operação. O tempo da operação é exibido
    // após sua conclusão.
    public StatusDeEdicao editarRegistro(Prontuario registro, int opcao_de_campo, String valor) {
        Supplier<StatusDeEdicao> edita = () -> {
            int num_registro = indice.obterNumeroDoRegistroAssociadoAChave(registro.getCpf());
            if (num_registro == -1)
                return StatusDeEdicao.CPF_INVALIDO;
            if (opcao_de_campo < 1 || opcao_de_campo > 4)
                return StatusDeEdicao.CAMPO_A_ALTERAR_INVALIDO;
            switch(opcao_de_campo) {
                case 1:
                    registro.setNome(valor);
                    break;
                case 2:
                    registro.setSexo(valor.charAt(0));
                    break;
                case 3:
                    DateTimeFormatter formatter;
                    if (valor.length() == 8)
                        formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
                    else
                        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate data = LocalDate.parse(valor, formatter);
                    registro.setData(data);
                    break;
                case 4:
                    registro.setAnotacoes(valor);
                    break;
                default:
                    break;
            }
            arquivo_mestre.sobrescreverRegistroNoArquivo(registro, num_registro);
            return StatusDeEdicao.TUDO_OK;
        };

        return executaEMedeTempo(edita, "editar");
    }

    // pesquisa e retorna o registro do arquivo mestre que
    // tem a chave igual a que é passada como argumento para o método.
    // caso a chave não seja encontrada retorna null; O tempo da operação é exibido
    // após sua conclusão.
    public Prontuario recuperarRegistro(int cpf) {
        Supplier<Prontuario> recupera = () -> {
            int num_registro = indice.obterNumeroDoRegistroAssociadoAChave(cpf);
            if (num_registro == -1)
                return null;
            return arquivo_mestre.recuperarRegistro(num_registro);
        };

        return executaEMedeTempo(recupera, "recuperar");
    }

    // remove logicamente um registro do arquivo mestre dada
    // a sua chave. Remove também a chave e o número do registro
    // no índice. Retorna StatusDeRemocao que indica o status final
    // da operação. O tempo da operação é exibido
    // após sua conclusão.
    public StatusDeRemocao removerRegistro(int cpf) {
        Supplier<StatusDeRemocao> remove = () -> {
            int num_registro = indice.obterNumeroDoRegistroAssociadoAChave(cpf);
            if (num_registro == -1)
                return StatusDeRemocao.CPF_INVALIDO;

            indice.removerRegistro(cpf);
            arquivo_mestre.removerRegistro(num_registro);
            return StatusDeRemocao.TUDO_OK;
        };

        return executaEMedeTempo(remove, "remover");
    }
    // imprime o conteúdo do arquivo mestre e do índice(buckets + diretório).
    // O tempo da operação é exibido após sua conclusão.
    public void imprimirArquivos() {
        Supplier<Object> imprime = () -> {
            arquivo_mestre.imprimirArquivo();
            indice.imprimirArquivo();
            return null;
        };

        executaEMedeTempo(imprime, "imprimir os arquivos");
    }

    // realiza a simulação de inserções e pesquisas com base numa coleção de
    // chaves geradas aleatoriamente. O tamanho da coleção é passado como argumento.
    // Caso o número de chaves seja 0 o número de chaves passa a ser o suficiente
    // para que o arquivo mestre tenha 1G de tamanho.
    // O tempo das operações(inserções e pesquisas) são exibidos após a conclusão.
    public void simular(int numero_chaves) {
        final int tam_registro_arq_mestre = arquivo_mestre.getTamRegistroCompleto();

        if (numero_chaves == 0) {
            numero_chaves = 1024 * 1024 * 1024 / tam_registro_arq_mestre;
        }

        final int ultimo_cpf_usado = 1;
        final List<Integer> cpfs = IntStream.rangeClosed(ultimo_cpf_usado, numero_chaves).boxed().collect(Collectors.toList());
        Collections.shuffle(cpfs);

        Supplier<Object> simulaInsere = () -> {
            Prontuario prontuario;
            int numRegistro;
            long inicio, fim;

            for (Integer cpf : cpfs) {
                inicio = System.currentTimeMillis();
                prontuario = new Prontuario(cpf, "Nome" + cpf, LocalDate.now(), 'm', "blablabla");
                numRegistro = arquivo_mestre.inserirRegistro(prontuario);
                indice.inserirRegistro(cpf, numRegistro);
                fim = System.currentTimeMillis();
                System.out.println("Tempo pra inserir o cpf " + cpf + ": " + (fim - inicio) + "ms");
            }

            if (arquivo_mestre.getNumRegistrosMemoria() > 0) {
                arquivo_mestre.flushRegistrosMemoria();
            }

            return null;
        };
        executaEMedeTempo(simulaInsere, "simulação [inserção]");

        Supplier<Object> simulaBusca = () -> {
            Prontuario prontuario;
            int numRegistro;
            long inicio, fim;

            for (Integer cpf : cpfs) {
                inicio = System.currentTimeMillis();
                numRegistro = indice.obterNumeroDoRegistroAssociadoAChave(cpf);
                prontuario = arquivo_mestre.recuperarRegistro(numRegistro);
                fim = System.currentTimeMillis();
                System.out.println("Tempo pra procurar o registro " + cpf + ": " + (fim - inicio) + "ms");
            }

            return null;
        };
        executaEMedeTempo(simulaBusca, "simulação [busca]");
    }

    // fecha a conexão com os arquivos de índice,
    // do diretório e do arquivo mestre
    public void fecharConexaoComArquivos() {
        indice.fecharConexaoComArquivos();
        arquivo_mestre.fecharConexaoComArquivo();
    }

    // executa uma determinada função passada como argumento
    // e contabiliza o tempo gasto para sua execução. O retorno
    // é dado pelo retorno da função passada como argumento.
    // label identifica o que será exibido na mensagem que
    // mostra o tempo.
    private <T> T executaEMedeTempo(Supplier<T> fn, String label) {
        long inicio = System.currentTimeMillis();

        T result = fn.get();

        long fim = System.currentTimeMillis();
        long diferenca = fim - inicio; // diferenca em milisegundos

        System.out.println("=== Tempo para " + label + ": " + (diferenca) + "ms. ====");

        return result;
    }
}
