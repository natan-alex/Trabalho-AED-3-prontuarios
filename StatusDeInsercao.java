package trabalho_aed_prontuario.indice;
// enum para indicar os possíveis status de um método
// que faça uma inserção no arquivo de índice

// IOEXCEPTION_LANCADA em caso de IOException lançada,
// REGISTRO_INVALIDO caso o registro a ser inserido seja inválido,
// DUPLICAR_DIRETORIO em caso de necessidade de duplicar o diretório,
// TUDO_OK quando a inserção puder ser feita,
// REARRANJAR_CHAVES caso seja necessário criar novo bucket 
// e rearranjar as chaves de um bucket existente.

public enum StatusDeInsercao {
    IOEXCEPTION_LANCADA,
    REGISTRO_INVALIDO,
    DUPLICAR_DIRETORIO,
    TUDO_OK,
    REARRANJAR_CHAVES
}
