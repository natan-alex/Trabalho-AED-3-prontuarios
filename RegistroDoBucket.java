package trabalho_aed_prontuario.indice;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RegistroDoBucket implements Serializavel {
    // Cada bucket contém o valor de profundidade local (p’) seguido por n entradas, as quais 
    // são constituídas por um CPF e o número do registro no arquivo-mestre onde o registro 
    // correspondente ao CPF se encontra (começando por 0).
    private int chave;
    private int num_registro;
    private boolean isLapide;

    public RegistroDoBucket(int chave) {
        this.chave = chave;
        num_registro = -1;
        isLapide = false;
    }

    public RegistroDoBucket(int chave, int num_registro) {
        this.chave = chave;
        this.num_registro = num_registro;
        isLapide = false;
    }

    public RegistroDoBucket(boolean isLapide, int chave, int num_registro) {
        this.isLapide = isLapide;
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
        return isLapide;
    }

    public void setIsLapide(boolean isLapide) {
        this.isLapide = isLapide;
    }

    @Override
    public String toString() {
        return "RegistroDoBucket: (chave = " + chave + ", num_registro = " + num_registro + ", is_lapide = " + isLapide + ")";
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeBoolean(isLapide);
            dos.writeInt(chave);
            dos.writeInt(num_registro);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);
            this.isLapide = dis.readBoolean();
            this.chave = dis.readInt();
            this.num_registro = dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
