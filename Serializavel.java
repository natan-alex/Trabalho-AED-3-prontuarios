package trabalho_aed_prontuario.indice;

public abstract class Serializavel {
    protected abstract void fromByteArray(byte[] data);
    protected abstract byte[] toByteArray();
}
