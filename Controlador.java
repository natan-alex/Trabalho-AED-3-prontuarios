package trabalho_aed_prontuario.main;

import trabalho_aed_prontuario.indice.*;
import trabalho_aed_prontuario.indice.StatusDeInsercao;
import trabalho_aed_prontuario.mestre.*;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;

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

    public Controlador() {
        criarDiretorioDB();
        // instancia o índice e o arquivo mestre com os valores
        // default, que serão ignorados caso os arquivos já existam
        indice = new Indice(INDICE_FILENAME, DIR_FILENAME, DEFAULT_P_GLOBAL, DEFAULT_TAM_BUCKET);
        arquivo_mestre = new ArquivoMestre(MASTER_FILENAME, DEFAULT_TAM_ANOTACOES);
    }

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

    public StatusDeInsercao inserirRegistro(Prontuario prontuario) {
        Supplier<Object> insere = () -> {
            if (prontuario == null)
                return StatusDeInsercao.REGISTRO_INVALIDO;
            int num_registro = arquivo_mestre.inserirRegistro(prontuario);
            return indice.inserirRegistro(prontuario.getCpf(), num_registro);
        };

        return (StatusDeInsercao) executaEMedeTempo(insere, "inserir");
    }


    public StatusDeEdicao editarRegistro(Prontuario registro, int opcao_de_campo, String valor) {
        Supplier<Object> edita = () -> {
            int num_registro = indice.getNumRegistro(registro.getCpf());
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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

        return (StatusDeEdicao) executaEMedeTempo(edita, "editar");
    }

    public Prontuario recuperarRegistro(int cpf) {
        Supplier<Object> recupera = () -> {
            int num_registro = indice.getNumRegistro(cpf);
            if (num_registro == -1)
                return null;
            return arquivo_mestre.recuperarRegistro(num_registro);
        };

        return (Prontuario) executaEMedeTempo(recupera, "recuperar");
    }

    public void removerRegistro(int cpf) {
        Supplier<Object> remove = () -> {
            int num_registro = indice.getNumRegistro(cpf);
            if (num_registro == -1)
                return null;

            indice.removerRegistro(cpf);
            arquivo_mestre.removerRegistro(num_registro);
            return null;
        };
        executaEMedeTempo(remove, "remover");
    }

    public void imprimirArquivos() {
        Supplier<Object> imprime = () -> {
            arquivo_mestre.imprimirArquivo();
            indice.imprimirArquivo();
            return null;
        };
        executaEMedeTempo(imprime, "imprimir os arquivos");
    }

    public void simular() {
        Supplier<Object> simula = () -> {
            // 1 registro = 69 bytes
            // 15561476 registros = 1 GB
            // int lastCpf = 15561476;
            int lastCpf = 15561476;
            Prontuario prontuario;
            int numRegistro;
            int ultimo_cpf_usado = 1;
            long inicio, fim;

            for (int cpf = ultimo_cpf_usado; cpf <= lastCpf; cpf++) {
                inicio = System.currentTimeMillis();
                prontuario = new Prontuario(cpf, "Nome" + cpf, LocalDate.now(), 'm', "blablabla");
                numRegistro = arquivo_mestre.inserirRegistro(prontuario);
                indice.inserirRegistro(cpf, numRegistro);
                fim = System.currentTimeMillis();
                System.out.println("Tempo de UMA inserção: " + (fim - inicio) + "ms");
            }
            return null;
        };

        executaEMedeTempo(simula, "simulação");
    }

    private Object executaEMedeTempo(Supplier<Object> fn, String label) {
        long inicio = System.currentTimeMillis();

        Object result = fn.get();

        long fim = System.currentTimeMillis();
        long diferenca = fim - inicio; // diferenca em milisegundos

        System.out.println("=== Tempo para " + label + ": " + (diferenca) + "ms. ====");

        return result;
    }
}
