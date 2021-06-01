package trabalho_aed_prontuario;

import java.time.LocalDate;
import java.util.Scanner;

import trabalho_aed_prontuario.mestre.*;
import trabalho_aed_prontuario.indice.*;

public class Main {
    public static void main(String[] args) {
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

        Indice indice;
        Diretorio diretorio;
        ArquivoMestre mestre;
        Prontuario prontuario;

        int cpf, num_registro;
        String nome, anotacoes;
        char sexo;

        switch(opcao) {
            case 1:
                System.out.println("Qual será a profundidade inicial do hash? ");
                int profundidade = in.nextInt();

                System.out.println("Qual será o número de registros por bucket? ");
                int tam_buckets = in.nextInt();

                System.out.println("Qual será o tamanho de anotações por registro? ");
                short tam_anotacoes = in.nextShort();

                new Indice(profundidade, tam_buckets);
                new ArquivoMestre(tam_anotacoes);
                break;
            case 2:
                indice = new Indice();
                mestre = new ArquivoMestre();

                System.out.print("Qual será o cpf do paciente? ");
                cpf = in.nextInt();

                System.out.print("Qual será o nome do paciente? ");
                nome = in.next();

                System.out.print("Qual será o sexo do paciente? ");
                sexo = in.next().charAt(0);

                in.nextLine();
                System.out.print("Qual será a anotação? ");
                anotacoes = in.nextLine();

                prontuario = new Prontuario(cpf, nome, LocalDate.now(), sexo, mestre.getNumBytesAnotacoes(), anotacoes);
                num_registro = mestre.inserirRegistro(prontuario);
                indice.inserirRegistro(cpf, num_registro);
                break;
            case 3:
                indice = new Indice();
                mestre = new ArquivoMestre();

                System.out.print("Qual será o cpf do paciente a ser modificado? ");
                cpf = in.nextInt();

                System.out.print("Qual dos seguintes campos deseja alterar? [1] Nome | [2] Sexo | [3] Data | [4] Anotações: ");
                opcao = in.nextInt();

                System.out.print("Escreva o novo valor: ");
                in.nextLine();
                Object valor = in.nextLine();

                num_registro = indice.getNumRegistro(cpf);
                mestre.editarRegistro(num_registro, opcao, valor);
                break;
            case 4:
                // remover registro
                break;
            case 5:
                mestre = new ArquivoMestre();
                mestre.imprimirArquivo();

                indice = new Indice();
                indice.imprimirArquivo();

                diretorio = new Diretorio();
                diretorio.imprimirArquivo();
                break;
            case 6:
                // simulacao
                break;
            default:
                break;
        }
    }
}
