package trabalho_aed_prontuario.indice;

public abstract class Serializavel {
    protected abstract void fromByteArray(byte[] data);
    public abstract byte[] toByteArray();

    public Serializavel() {
    }

    public Serializavel(byte[] data) {
        fromByteArray(data);
    }
}
