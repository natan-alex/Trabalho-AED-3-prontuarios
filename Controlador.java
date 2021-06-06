package trabalho_aed_prontuario.main;

import trabalho_aed_prontuario.indice.*;
import trabalho_aed_prontuario.mestre.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public void criarArquivos(int profundidade_global, int tam_bucket, int tam_anotacoes) {
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
        apagarArquivos();
        indice = new Indice(INDICE_FILENAME, DIR_FILENAME, profundidade_global, tam_bucket);
        arquivo_mestre = new ArquivoMestre(MASTER_FILENAME, (short) tam_anotacoes);
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
        if (prontuario == null)
            return StatusDeInsercao.REGISTRO_INVALIDO;
        int num_registro = arquivo_mestre.inserirRegistro(prontuario);
        return indice.inserirRegistro(prontuario.getCpf(), num_registro);
    }


    public StatusDeEdicao editarRegistro(Prontuario registro, int opcao_de_campo, String valor) {
        int num_registro = indice.getNumRegistro(registro.getCpf());
        if (num_registro == -1)
            return StatusDeEdicao.CPF_INVALIDO;
        Prontuario.CampoAlterado campo_alterado = getCampoByNum(opcao_de_campo);
        if (campo_alterado == null)
            return StatusDeEdicao.CAMPO_A_ALTERAR_INVALIDO;
        switch(campo_alterado) {
            case NOME:
                registro.setNome(valor);
                break;
            case SEXO:
                registro.setSexo(valor.charAt(0));
                break;
            case DATA:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate data = LocalDate.parse(valor, formatter);
                registro.setData(data);
                break;
            case ANOTACOES:
                registro.setAnotacoes(valor);
                break;
            default:
                break;
        }
        arquivo_mestre.sobrescreverRegistroNoArquivo(registro, num_registro);
        return StatusDeEdicao.TUDO_OK;
    }

    private Prontuario.CampoAlterado getCampoByNum(int opcao) {
        if (opcao == 1) {
            return Prontuario.CampoAlterado.NOME;
        } else if (opcao == 2) {
            return Prontuario.CampoAlterado.SEXO;
        } else if (opcao == 3) {
            return Prontuario.CampoAlterado.DATA;
        } else if (opcao == 4){
            return Prontuario.CampoAlterado.ANOTACOES;
        } else {
            return null;
        }
    }

    public Prontuario recuperarRegistro(int cpf) {
        int num_registro = indice.getNumRegistro(cpf);
        if (num_registro == -1)
            return null;
        Prontuario registro = arquivo_mestre.recuperarRegistro(num_registro);
        return registro;
    }

    public void removerRegistro() {

    }

    public void imprimirArquivos() {
        arquivo_mestre.imprimirArquivo();
        indice.imprimirArquivo();
    }
}
