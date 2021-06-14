package trabalho_aed_prontuario.main;

// CPF_INVALIDO autodescritivo, acontece quando o cpf
// passado para o método de edição não for válido;
// CAMPO_A_ALTERAR_INVALIDO acontece quando a opção referente ao
// campo que se deseja alterar não for válida;
// TUDO_OK acontece quando a edição puder ser feita;

public enum StatusDeEdicao {
    CPF_INVALIDO,
    CAMPO_A_ALTERAR_INVALIDO,
    TUDO_OK
}
