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
    private boolean is_lapide;

    public RegistroDoBucket(byte[] data) {
        super(data);
    }

    public RegistroDoBucket() {
        chave = -1;
        num_registro = -1;
        is_lapide = false;
    }

    public RegistroDoBucket(int chave) {
        this.chave = chave;
        num_registro = -1;
        is_lapide = false;
    }

    public RegistroDoBucket(int chave, int num_registro) {
        this.chave = chave;
        this.num_registro = num_registro;
        is_lapide = false;
    }

    public RegistroDoBucket(boolean is_lapide, int chave, int num_registro) {
        this.is_lapide = is_lapide;
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

    public boolean getIsLapide() {
        return is_lapide;
    }

    public void setIsLapide(boolean is_lapide) {
        this.is_lapide = is_lapide;
    }

    @Override
    public String toString() {
        return "RegistroDoBucket: (chave = " + chave + ", num_registro = " + num_registro + ", is_lapide = " + is_lapide + ")";
    }

    @Override
    protected byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeBoolean(is_lapide);
            dos.writeInt(chave);
            dos.writeInt(num_registro);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    @Override
    protected void fromByteArray(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);
            this.is_lapide = dis.readBoolean();
            this.chave = dis.readInt();
            this.num_registro = dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
