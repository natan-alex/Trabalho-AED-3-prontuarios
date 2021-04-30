package trabalho_aed_prontuario;

public class TesteBucket {
    public static void main(String[] args) {
        Bucket<Integer> bucket = new Bucket<Integer>(5, 0);
        System.out.println( bucket.adicionarRegistro( new RegistroDoBucket<Integer>(10, 100) ) );
    }
}

