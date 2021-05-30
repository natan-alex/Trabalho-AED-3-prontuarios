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
    private static final char AUX_CHAR = '|';

    private int cpf;
    private String nome;
    private LocalDate data;
    private char sexo;
    private String anotacoes;
    private short tam_anotacoes;


    public Prontuario(byte[] data) {
        super(data);
    }

    public Prontuario(int cpf, String nome, LocalDate data, char sexo, short tam_anotacoes) {
        setCpf(cpf);
        setNome(nome);
        setData(data);
        setSexo(sexo);
        setTamAnotacoes(tam_anotacoes);
        setAnotacoes("");
    }

    public Prontuario(int cpf, String nome, LocalDate data, char sexo, short tam_anotacoes, String anotacoes) {
        setCpf(cpf);
        setNome(nome);
        setData(data);
        setSexo(sexo);
        setTamAnotacoes(tam_anotacoes);
        setAnotacoes(anotacoes);
    }

    private void setTamAnotacoes(short tam_anotacoes) {
        if (tam_anotacoes > 0)
            this.tam_anotacoes = tam_anotacoes;
    }

    public void setCpf(int cpf) {
        if (cpf > 0)
            this.cpf = cpf;
    }

    public int getCpf() {
        return cpf;
    }

    public String getNome() {
        // antes de retornar necessário remover os AUX_CHAR
        return nome.substring(0, nome.indexOf(AUX_CHAR));
    }

    public void setNome(String nome) {
        // se o argumento tiver tamanho menor que o tamanho máximo
        // completar com espaços, senão, limitar os caracteres até
        // a quantcpfade máxima, "cortando "a string
        int nome_length = nome.length();
        if (nome_length < MAX_SIZE_NOME) {
            // completar a string com AUX_CHAR caso o tamanho do argumento
            // seja menor que MAX_SIZE_NOME
            // this.nome = String.format("%-"+MAX_SIZE_NOME+"s", nome).replace(' ', AUX_CHAR);
            this.nome = nome; //String.format("%-"+MAX_SIZE_NOME+"s", nome).replace(' ', AUX_CHAR);
        } else if (nome_length > MAX_SIZE_NOME) {
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
        if (data != null)
            this.data = data;
    }

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char _sexo) {
        _sexo = Character.toLowerCase(_sexo);
        if (_sexo == 'm' || _sexo == 'f') // ++
            this.sexo = _sexo;
    }

    public String getAnotacoes() {
        return anotacoes.substring(0, anotacoes.indexOf(AUX_CHAR));
    }

    public void setAnotacoes(String anotacoes) {
        // faz o mesmo que a setNome com relação ao tamanho e corte
        // da string que vier como argumento
        int anotacoes_length = anotacoes.length();
        if (anotacoes_length < tam_anotacoes) {
            // completar a string com AUX_CHAR caso o tamanho do argumento
            // seja menor que tam_anotacoes
            // this.anotacoes = String.format("%-"+tam_anotacoes+"s", anotacoes).replace(' ', AUX_CHAR);
            this.anotacoes = anotacoes; //String.format("%-"+tam_anotacoes+"s", anotacoes).replace(' ', AUX_CHAR);
        } else if (anotacoes_length > tam_anotacoes) {
            // cortar a string até o tamanho desejado
            // caso seja maior que o permitido
            this.anotacoes = anotacoes.substring(0, tam_anotacoes);
        } else {
            this.anotacoes = anotacoes;
        }
    }

    @Override
    public String toString() {
        return "Prontuario: (nome = " + nome + ", data = " + data + ", sexo = " + sexo + ", anotacoes = " + anotacoes + ")";
    }

    // retorna um array de bytes com os valores dos atributos
    @Override
    protected byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(cpf);
            // System.out.println("=== NOME === " + nome);
            dos.writeUTF(nome);
            dos.writeShort( (short) data.getYear() );
            dos.writeByte( (byte) data.getMonthValue() );
            dos.writeByte( (byte) data.getDayOfMonth() );
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
            this.data = LocalDate.of( dis.readShort(), dis.readByte(), dis.readByte() );
            this.sexo = dis.readChar();
            this.anotacoes = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
