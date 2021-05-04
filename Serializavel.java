package trabalho_aed_prontuario.indice;

public interface Serializavel {
    public byte[] toByteArray();
    public void fromByteArray(byte[] data);
}
