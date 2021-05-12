package trabalho_aed_prontuario;

import java.time.LocalDate;
import java.util.Scanner;

import trabalho_aed_prontuario.mestre.*;
import trabalho_aed_prontuario.indice.*;
import trabalho_aed_prontuario.diretorio.*;

public class Main {
    private static Diretorio diretorio;
    private static Indice indice;

    public static void main(String[] args) {
        ArquivoMestre<Prontuario> arquivo_mestre = new ArquivoMestre<Prontuario>((short) 100);

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

                // Injetar indice?
                diretorio = new Diretorio(opcao);
                indice = new Indice(opcao, 4);
                break;
            case 2:
                // inserir registro
                int cpf = 0;
                Prontuario p = new Prontuario("fulano", LocalDate.now(), 'm', (short) 10, "teste");
                // int registro = arquivo_mestre.inserir_registro(p);
                diretorio = new Diretorio(0);

                int numBucket = diretorio.getPaginaIndice(cpf);
                indice = new Indice(0, 0); // TODO: Arrumar.
                // indice.inserirRegistro(numBucket, cpf);
                break;
            case 3:
                // editar registro
                break;
            case 4:
                // remover registro
                break;
            case 5:
                diretorio = new Diretorio(0);
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
