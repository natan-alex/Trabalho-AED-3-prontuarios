package trabalho_aed_prontuario.indice;

// IOEXCEPTION_LANCADA em caso de IOException lançada,
// REGISTRO_INVALIDO caso o registro a ser inserido seja inválido,
// DUPLICAR_DIRETORIO em caso de necessidade de duplicar o bucket,
// TUDO_OK quando a inserção puder ser feita,
// REARRANJAR_CHAVES caso seja necessário criar novo bucket 
// e rearranjar as chaves do novo bucket e do bucket atual
public enum StatusDeInsercao {
    IOEXCEPTION_LANCADA,
    REGISTRO_INVALIDO,
    DUPLICAR_DIRETORIO,
    TUDO_OK,
    REARRANJAR_CHAVES
}
