package trabalho_aed_prontuario.indice;

public interface Serializavel<T> {
    public byte[] toByteArray();
    public T fromByteArray(byte[] data);
}

