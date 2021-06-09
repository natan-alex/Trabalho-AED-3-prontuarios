package trabalho_aed_prontuario.indice;

// IOEXCEPTION_LANCADA em caso de IOException lançada,
// REGISTRO_INVALIDO caso o registro a ser inserido seja inválido,
// DUPLICAR_DIRETORIO em caso de necessidade de duplicar o bucket,
// TUDO_OK quando a inserção puder ser feita,
// REARRANJAR_CHAVES caso seja necessário criar novo bucket 
// e rearranjar as chaves do novo bucket e do bucket atual
public enum StatusDeInsercao {
    IOEXCEPTION_LANCADA(0),
    REGISTRO_INVALIDO(1),
    DUPLICAR_DIRETORIO(2),
    TUDO_OK(3),
    REARRANJAR_CHAVES(4);

    int num_opcao;

    private StatusDeInsercao(int num_opcao) {
        this.num_opcao = num_opcao;
    }

    public int getNumOpcao() {
        return num_opcao;
    }
}
