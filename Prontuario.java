package trabalho_aed_prontuario.mestre;

import java.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import trabalho_aed_prontuario.indice.Serializavel;

public class Prontuario extends Serializavel {
    // nome, data de nascimento, sexo e uma área de m caracteres/bytes para anotações do médico
    private static final byte MAX_SIZE_NOME = (byte) 50;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int cpf;
    private String nome;
    private LocalDate data;
    private char sexo;
    private String anotacoes;

    public Prontuario(byte[] dados) {
        fromByteArray(dados);
    }

    public Prontuario(int cpf, String nome, LocalDate data, char sexo) {
        this.cpf = cpf;
        setNome(nome);
        this.data = data;
        this.sexo = sexo;
        this.anotacoes = "";
    }

    public Prontuario(int cpf, String nome, LocalDate data, char sexo, String anotacoes) {
        this.cpf = cpf;
        setNome(nome);
        this.data = data;
        this.sexo = sexo;
        this.anotacoes = anotacoes;
    }

    public int getCpf() {
        return cpf;
    }

    public void setCpf(int cpf) {
        if (cpf > 0)
            this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome.length() > MAX_SIZE_NOME) {
            // cortar a string até o tamanho desejado
            // caso seja maior que o permitido
            this.nome = nome.substring(0, MAX_SIZE_NOME);
        } else {
            this.nome = nome;
        }
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    @Override
    public String toString() {
        return "Prontuario: (cpf = " + cpf +  ", nome = " + nome + ", data = " + data.format(formatter) + ", sexo = " + sexo + ", anotacoes = " + anotacoes + ")";
    }

    public byte[] toByteArrayComCabecalho(int id, int prox_id_vazio, boolean lapide) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(id);
            dos.writeInt(prox_id_vazio);
            dos.writeBoolean(lapide);
            dos.write(toByteArray());
            dos.flush();
            dos.close();
            dos = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }


    // retorna um array de bytes com os valores dos atributos
    @Override
    protected byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(cpf);
            dos.writeUTF(nome);
            dos.writeShort((short) data.getYear());
            dos.writeByte((byte) data.getMonthValue());
            dos.writeByte((byte) data.getDayOfMonth());
            dos.writeChar(sexo);
            dos.writeUTF(anotacoes);
            dos.flush();
            dos.close();
            dos = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    // preenche os valores dos atributos com base nos bytes
    // vindos de um array de bytes
    @Override
    protected void fromByteArray(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             DataInputStream dis = new DataInputStream(bais)
        ) {
            this.cpf = dis.readInt();
            this.nome = dis.readUTF();
            this.data = LocalDate.of(dis.readShort(), dis.readByte(), dis.readByte());
            this.sexo = dis.readChar();
            this.anotacoes = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
