package trabalho_aed_prontuario;

import java.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import trabalho_aed_prontuario.indice.Serializavel;

public class Prontuario implements Serializavel {
    // nome, data de nascimento, sexo e uma área de m caracteres/bytes para anotações do médico
    private static final byte MAX_SIZE_NOME = (byte) 50;
    private static final short MAX_SIZE_ANOTACOES = 255;
    private StringBuilder nome;
    private LocalDate data; 
    private char sexo;
    private StringBuilder anotacoes;

    public Prontuario(String nome, LocalDate data, char sexo, String anotacoes) {
        this.data = data;
        this.sexo = sexo;
        setNome(nome);
        setAnotacoes(anotacoes);
        System.out.println("nome: " + this.nome + " tam: " + this.nome.length());
        System.out.println("anotacoes: " + this.anotacoes + " tam: " + this.anotacoes.length() );
    }

    public void setNome(String nome) {
        this.nome = new StringBuilder(MAX_SIZE_NOME);
        if (nome.length() < MAX_SIZE_NOME) {
            System.out.println("menor");
            this.nome.append( String.format("%-"+MAX_SIZE_NOME+"s", nome) );
        } else {
            this.nome.append(nome);
            this.nome.setLength(MAX_SIZE_NOME);
        }
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = new StringBuilder(MAX_SIZE_ANOTACOES);
        if (anotacoes.length() < MAX_SIZE_ANOTACOES) {
            System.out.println("menor");
            this.anotacoes.append( String.format("%-"+MAX_SIZE_ANOTACOES+"s", anotacoes) );
        } else {
            this.anotacoes.append(anotacoes);
            this.anotacoes.setLength(MAX_SIZE_ANOTACOES);
        }
    }

    @Override
    public String toString() {
        return "Prontuario: (nome = " + nome + ", data = " + data + ", sexo = " + sexo + ", anotacoes = " + anotacoes + ")";
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // try {
        //     // dos.write
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] data) {
        
        // try {
        //     ByteArrayInputStream bais = new ByteArrayInputStream(data);
        //     DataInputStream dis = new DataInputStream(bais);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
