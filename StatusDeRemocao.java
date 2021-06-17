package trabalho_aed_prontuario.main;
// enum para indicar os possíveis status de um método
// que faça uma remoção de um registro no arquivo mestre

public enum StatusDeRemocao {
    // CPF_INVALIDO acontece quando
    // o número do registro passado para o método de pesquisa
    // não for válido, o que indica que o número do registro
    // também não será válido;
    // TUDO_OK acontece quando o registro tiver sido ou puder ser removido;

    CPF_INVALIDO,
    TUDO_OK
}