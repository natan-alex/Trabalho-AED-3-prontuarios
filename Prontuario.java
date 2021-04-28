package trabalho_aed_prontuario;

import java.time.LocalDate;
import java.io.Serializable;

public class Prontuario implements Serializable {
    // nome, data de nascimento, sexo e uma área de m caracteres/bytes para anotações do médico
    private static final long serialVersionUID = 1L;
    private String nome;
    private LocalDate data; 
    private char sexo;
    private String anotacoes;

    public Prontuario(String nome, LocalDate data, char sexo, String anotacoes) {
        this.nome = nome;
        this.data = data;
        this.sexo = sexo;
        this.anotacoes = anotacoes;
    }

    @Override
    public String toString() {
        return "Prontuario: (nome = " + nome + ", data = " + data + ", sexo = " + sexo + ", anotacoes = " + anotacoes + ")";
    }

}
