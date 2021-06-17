package trabalho_aed_prontuario.indice;

public abstract class Serializavel {
    // método para atribuir aos atributos da subclasse
    // os valores vindos de array de bytes, de um objeto
    // serializado
    protected abstract void fromByteArray(byte[] data);
    // método serializar um objeto da subclasse
    protected abstract byte[] toByteArray();
}
