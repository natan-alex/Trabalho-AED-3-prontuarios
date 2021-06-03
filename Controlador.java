package trabalho_aed_prontuario.main;

import trabalho_aed_prontuario.indice.*;
import trabalho_aed_prontuario.indice.StatusDeInsercao;
import trabalho_aed_prontuario.mestre.*;
import trabalho_aed_prontuario.mestre.ArquivoMestre;

public class Controlador {
    private final Indice indice;
    private final ArquivoMestre arquivo_mestre;
    private static final int DEFAULT_P_GLOBAL = 1;
    private static final int DEFAULT_TAM_BUCKET = 10;
    private static final int DEFAULT_TAM_ANOTACOES = 100;

    public Controlador(int profundidade_global, int tam_bucket, int tam_anotacoes) {
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
        indice = new Indice(profundidade_global, tam_bucket);
        arquivo_mestre = new ArquivoMestre((short)tam_anotacoes);
    }

    public boolean inserirRegistro(Prontuario prontuario) {
        int num_registro = arquivo_mestre.inserirRegistro(prontuario);
        StatusDeInsercao status = indice.inserirRegistro(prontuario.getCpf(), num_registro);
        return (status == StatusDeInsercao.TUDO_OK);
    }

    public boolean editarRegistro(int cpf, int opcao, String valor) {
        int num_registro = indice.getNumRegistro(cpf);
        if (num_registro == -1)
            return false;
        arquivo_mestre.editarRegistro(num_registro, opcao, valor);
        return true;
    }

    public void removerRegistro() {

    }

    public void imprimirArquivos() {
        arquivo_mestre.imprimirArquivo();
        indice.imprimirArquivo();
    }
}
