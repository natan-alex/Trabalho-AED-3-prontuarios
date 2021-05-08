package trabalho_aed_prontuario;

import java.time.LocalDate;
import java.util.Scanner;

import trabalho_aed_prontuario.mestre.*;
import trabalho_aed_prontuario.indice.*;

public class Main {
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static FileOutputStream fos;
    private static FileInputStream fis;

    private static Diretorio diretorio;

    public static void main(String[] args) {
        ArquivoMestre arquivo_mestre = new ArquivoMestre();
        Scanner in = new Scanner(System.in);
        int opcao;

        System.out.println("MENU");
        System.out.println("[1] - Criar arquivo");
        System.out.println("[2] - Inserir registro");
        System.out.println("[3] - Editar registro");
        System.out.println("[4] - Remover registro");
        System.out.println("[5] - Imprimir arquivos");
        System.out.println("[6] - Simulacao");
        System.out.print("Opção: ");

        opcao = in.nextInt();


        switch(opcao) {
            case 1:
                System.out.println("Qual será a profundidade inicial do hash? ");
                opcao = in.nextInt();
                diretorio = new Diretorio(0, "diretorio.db");
                break;
            case 2:
                // inserir registro
                // Prontuario p = new Prontuario("fulano", LocalDate.now(), 'm', "teste");
                // arquivo_mestre.inserir_registro(p);
                break;
            case 3:
                // editar registro
                break;
            case 4:
                // remover registro
                break;
            case 5:
                diretorio = new Diretorio(0, "diretorio.db");
                diretorio.carregarArquivo();
                // imprimir arquivos
                break;
            case 6:
                // simulacao
                break;
            default:
                break;
        }
    }
}
