package trabalho_aed_prontuario.indice;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RegistroDoBucket extends Serializavel {
    // Cada bucket contém o valor de profundidade local (p’) seguido por n entradas, as quais 
    // são constituídas por um CPF e o número do registro no arquivo-mestre onde o registro 
    // correspondente ao CPF se encontra (começando por 0).
    private int chave;
    private int num_registro;

    public RegistroDoBucket(byte[] dados) {
        super(dados);
    }

    // uso para inicializar um bucket com registros
    // default
    protected RegistroDoBucket() {
        chave = -1;
        num_registro = -1;
    }

    public RegistroDoBucket(int chave) {
        this.chave = chave;
        num_registro = -1;
    }

    public RegistroDoBucket(int chave, int num_registro) {
        this.chave = chave;
        this.num_registro = num_registro;
    }

    protected RegistroDoBucket(boolean is_lapide, int chave, int num_registro) {
        this.chave = chave;
        this.num_registro = num_registro;
    }

    public int getChave() {
        return chave;
    }

    public void setChave(int chave) {
        this.chave = chave;
    }

    public int getNumRegistro() {
        return num_registro;
    }

    public void setNumRegistro(int num_registro) {
        this.num_registro = num_registro;
    }

    @Override
    public String toString() {
        return "RegistroDoBucket: (chave = " + chave + ", num_registro = " + num_registro + ")";
    }

    @Override
    protected byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(chave);
            dos.writeInt(num_registro);
            dos.flush();
            dos.close();
            dos = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    @Override
    protected void fromByteArray(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bais)
            ) {
            this.chave = dis.readInt();
            this.num_registro = dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
