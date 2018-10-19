import java.rmi.registry.LocateRegistry;
import java.sql.SQLOutput;
import java.util.Scanner;

public class RmiClient {

    static String name;
    static String privilegio;

    static void menu(RmiInterface rmiInterface){
        String escolha;
        while(true){
            System.out.println("\n=== MENU ===");
            System.out.println("1) - Pesquisar");
            System.out.println("2) - Gerir");
            System.out.println("3) - Dar Previlegios");
            System.out.println("4) - Upload");
            System.out.println("5) - Download");
            System.out.println("6) - Partilha da minha Biblioteca");
            System.out.println("7) - Log Out");
            System.out.print("Escolha: ");

            Scanner string = new Scanner(System.in);
            escolha = string.nextLine();

            if(escolha.compareTo("1") == 0){
                escolha = "0";
                while(escolha.compareTo("4") != 0) {
                    System.out.println("\n=== PESQUISAR ===");
                    System.out.println("1} - Pesquisar por nome de musica");
                    System.out.println("2) - Pesquisar por nome de artista");
                    System.out.println("3) - Pesquisar por nome de album");
                    System.out.println("4) - Voltar");
                    System.out.print("Escolha: ");
                    escolha = string.nextLine();
                }
            }
            else if(escolha.compareTo("2") == 0){
                escolha = "0";
                while(escolha.compareTo("4") != 0) {
                    System.out.println("\n=== GERIR ===");
                    System.out.println("1} - Inserir Musica/Artista/Album");
                    System.out.println("2) - Alterar Musica/Artista/Album");
                    System.out.println("3) - Remover Musica/Artista/Album");
                    System.out.println("4) - Voltar");
                    System.out.print("Escolha: ");
                    escolha = string.nextLine();
                }
            }
            else if(escolha.compareTo("3") == 0){
                //DAR PREVILEGIO
            }
            else if(escolha.compareTo("4") == 0){
                //UPLOAD
            }
            else if(escolha.compareTo("5") == 0){
                //DOWNLOAD
            }
            else if(escolha.compareTo("6") == 0){
                //GERIR SUA BIBLIOTECA
            }
            else if(escolha.compareTo("7") == 0){
                //LOGOUT
                return;
            }
        }
    }

    public static void main(String args[]){
        try{
            RmiInterface rmiInterface = (RmiInterface) LocateRegistry.getRegistry(7000).lookup("rmiSERVER");
            String escolha;
            while(true){
                System.out.println("\n=== Projeto de SD ===");
                System.out.println("1) - Fazer Login");
                System.out.println("2) - Registo");
                System.out.println("3) - Sair");
                System.out.print("Escolha: ");

                Scanner string = new Scanner(System.in);
                escolha = string.nextLine();

                if(escolha.compareTo("1") == 0){
                    System.out.println("\n=== LOGIN ===\n(Introduza zero em ambas para recuar)");
                    int controlo = 0;
                    while(controlo == 0) {
                        System.out.print("Nome de Utilizador: ");
                        String username = string.nextLine();
                        System.out.print("Password: ");
                        String password = string.nextLine();
                        if (!username.equals("0") && !password.equals("0")) {
                            String resposta = rmiInterface.verificaLogin(username, password);
                            if (resposta.startsWith("Erro!")) {
                                System.out.println(resposta);
                            } else {
                                name = username;
                                privilegio = resposta;
                                System.out.println("\n=== Bem-vindo ao DropMusic ===\n");
                                menu(rmiInterface);
                                controlo = 1;
                            }
                        }
                    }
                }
                else if(escolha.compareTo("2") == 0){
                    System.out.println("\n=== SIGN UP ===\n(Introduza zero em ambas para recuar)");
                    int controlo = 0;
                    while(controlo == 0) {
                        System.out.print("Nome de Utilizador: ");
                        String username = string.nextLine();
                        System.out.print("Password: ");
                        String password = string.nextLine();
                        if (!username.equals("0") && !password.equals("0")) {
                            String resposta = rmiInterface.verificaSignUp(username, password);
                            if (resposta.startsWith("Erro!")) {
                                System.out.println(resposta);
                            } else {
                                name = username;
                                privilegio = resposta;
                                System.out.println(resposta);
                                System.out.println("\n=== Bem-vindo ao DropMusic ===\n");
                                menu(rmiInterface);
                                controlo = 1;
                            }
                        }
                    }
                }else if(escolha.compareTo("3") == 0){
                    return;
                }else{
                    System.out.println("Escolha nao valido!");
                }
            }
        } catch(Exception e){
            System.out.println("Exception in Client: "+e);
            e.printStackTrace();
        }
    }

}
