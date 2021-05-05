package trabalho_aed_prontuario.mestre;

import java.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import trabalho_aed_prontuario.indice.Serializavel;

public class Prontuario extends Serializavel {
    // nome, data de nascimento, sexo e uma área de m caracteres/bytes para anotações do médico
    private static final byte MAX_SIZE_NOME = (byte) 50;
    private String nome;
    private LocalDate data; 
    private char sexo;
    private String anotacoes;
    private short tam_anotacoes;

    public Prontuario(byte[] data) {
        super(data);
    }

    public Prontuario(String nome, LocalDate data, char sexo, short tam_anotacoes) {
        setNome(nome);
        this.data = data;
        this.sexo = sexo;
        this.tam_anotacoes = tam_anotacoes;
        setAnotacoes("");
    }

    public Prontuario(String nome, LocalDate data, char sexo, short tam_anotacoes, String anotacoes) {
        setNome(nome);
        this.data = data;
        this.sexo = sexo;
        this.tam_anotacoes = tam_anotacoes;
        setAnotacoes(anotacoes);
    }

    public void setNome(String nome) {
        // se o argumento tiver tamanho menor que o tamanho máximo
        // completar com espaços, senão, limitar os caracteres até
        // a quantidade máxima, "cortando "a string
        if (nome.length() < MAX_SIZE_NOME) {
            this.nome = String.format("%-"+MAX_SIZE_NOME+"s", nome); 
        } else {
            this.nome = nome.substring(0, MAX_SIZE_NOME);
        }
        System.out.println("nome: " + this.nome + " tam: " + this.nome.length());
    }

    public void setAnotacoes(String anotacoes) {
        // faz o mesmo que a setNome com relação ao tamanho e corte
        // da string que vier como argumento
        if (anotacoes.length() < tam_anotacoes) {
            this.anotacoes = String.format("%-"+tam_anotacoes+"s", anotacoes);
        } else {
            this.anotacoes = anotacoes.substring(0, tam_anotacoes);
        }
        System.out.println("anotacoes: " + this.anotacoes + " tam: " + this.anotacoes.length());
    }

    @Override
    public String toString() {
        return "Prontuario: (nome = " + nome + ", data = " + data + ", sexo = " + sexo + ", anotacoes = " + anotacoes + ")";
    }

    // retorna um array de bytes com os valores dos atributos
    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeUTF(nome);
            // dos.writeShort( (short) data.getYear() );
            // dos.writeByte( (byte) data.getMonthValue() );
            // dos.writeByte( (byte) data.getDayOfMonth() );
            dos.writeShort( data.getYear() );
            dos.writeByte( data.getMonthValue() );
            dos.writeByte( data.getDayOfMonth() );
            dos.writeChar(sexo);
            dos.writeUTF(anotacoes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    // preenche os valores dos atributos com base nos bytes
    // vindos de um array de bytes
    protected void fromByteArray(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);

        try {
            this.nome = dis.readUTF();
            this.data = LocalDate.of( dis.readShort(), dis.readByte(), dis.readByte() );
            this.sexo = dis.readChar();
            setAnotacoes( dis.readUTF() );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
