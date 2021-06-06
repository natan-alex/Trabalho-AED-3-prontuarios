package trabalho_aed_prontuario.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

import trabalho_aed_prontuario.indice.StatusDeInsercao;
import trabalho_aed_prontuario.mestre.Prontuario;
import trabalho_aed_prontuario.main.Controlador;

public class Main {
    private static void cleanBuffer(Scanner sc) {
    }

    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        final Controlador controlador = new Controlador();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yy");

        int cpf;
        String nome, anotacoes, strData;
        char sexo;
        LocalDate data;
        int opcao;
        Prontuario prontuario;

        do {
            System.out.println("   MENU");
            System.out.println("[0] - Sair");
            System.out.println("[1] - Criar arquivo");
            System.out.println("[2] - Inserir registro");
            System.out.println("[3] - Editar registro");
            System.out.println("[4] - Remover registro");
            System.out.println("[5] - Imprimir arquivos");
            System.out.println("[6] - Simulacao");
            System.out.print("Opção: ");

            cpf = -1;
            nome = "";
            data = null;
            strData = "";
            sexo = ' ';
            anotacoes = "";

            if (in.hasNextInt())
                opcao = in.nextInt();
            else
                opcao = -1;

            switch (opcao) {
                case 0:
                    in.close();
                    System.out.println("Programa encerrado.");
                    break;
                case 1:
                    int profundidade = -1, tam_buckets = -1;
                    short tam_anotacoes = -1;
                    System.out.print("Qual será a profundidade inicial do hash? ");
                    if (in.hasNextInt())
                        profundidade = in.nextInt();

                    System.out.print("Qual será o número de registros por bucket? ");
                    if (in.hasNextInt())
                        tam_buckets = in.nextInt();

                    System.out.print("Qual será o tamanho de anotações por registro? ");
                    if (in.hasNextShort())
                        tam_anotacoes = in.nextShort();

                    if (controlador.criarArquivos(profundidade, tam_buckets, tam_anotacoes))
                        System.out.println("Arquivos criados com sucesso.");
                    else
                        System.out.println("Falha ao criar arquivos.");
                    break;
                case 2:
                    System.out.print("Qual será o cpf do paciente? ");
                    if (in.hasNextInt())
                        cpf = in.nextInt();

                    // jogar fora o \n depois do int lido
                    in.nextLine();

                    System.out.print("Qual será o nome do paciente? ");
                    if (in.hasNextLine())
                        nome = in.nextLine();

                    System.out.print("Qual será a data? (Digite no formato dia/mês/ano): ");
                    if (in.hasNextLine())
                        strData = in.nextLine();

                    if (strData.length() == 10)
                        data = LocalDate.parse(strData, formatter);
                    else
                        data = LocalDate.parse(strData, formatter2);

                    System.out.print("Qual será o sexo do paciente? ");
                    if (in.hasNextLine())
                        sexo = in.nextLine().charAt(0);

                    System.out.print("Qual será a anotação? ");
                    if (in.hasNextLine())
                        anotacoes = in.nextLine();

                    prontuario = new Prontuario(cpf, nome, data, sexo, (short) 10, anotacoes);

                    if (controlador.inserirRegistro(prontuario) == StatusDeInsercao.TUDO_OK)
                        System.out.println("Prontuário inserido com sucesso.");
                    else
                        System.out.println("Falha ao inserir prontuário.");
                    break;
                case 3:
                    System.out.print("Qual é o cpf vinculado ao prontuário que vai ser modificado? ");
                    if (in.hasNextInt())
                        cpf = in.nextInt();

                    prontuario = controlador.recuperarRegistro(cpf);

                    if (prontuario == null) {
                        System.out.println("Cpf não cadastrado.");
                        return;
                    }

                    System.out.println("Informações atuais do prontuário: ");
                    System.out.println(prontuario);

                    System.out.println("Qual dos seguintes campos deseja alterar?");
                    System.out.println("[1] - Nome");
                    System.out.println("[2] - Sexo");
                    System.out.println("[3] - Data");
                    System.out.println("[4] - Anotações");
                    System.out.print("Campo: ");
                    opcao = -1;

                    if (in.hasNextInt())
                        opcao = in.nextInt();

                    while (opcao < 1 || opcao > 4) {
                        System.out.print("Campo inválido. Digite novamente o número do campo: ");
                        opcao = in.nextInt();
                    }

                    // jogar fora o \n depois do int lido
                    in.nextLine();

                    String valor = "";
                    System.out.print("Escreva o novo valor: "); // problema com anotacoes
                    if (in.hasNextLine())
                        valor = in.nextLine();

                    if (controlador.editarRegistro(prontuario, opcao, valor) == StatusDeEdicao.TUDO_OK) {
                        System.out.println("Informações do prontuário atualizadas com sucesso.");
                    } else {
                        System.out.println("Falha ao atualizar informções do prontuário.");
                    }
                    break;
                case 4:
                    // remover registro
                    controlador.removerRegistro();
                    break;
                case 5:
                    // imprimir arquivos
                    controlador.imprimirArquivos();
                    break;
                case 6:
                    // simulacao
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } while (opcao != 0);
    }
}
