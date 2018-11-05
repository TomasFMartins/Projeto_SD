import java.io.*;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.Scanner;

/**
 * É o RMI Client.
 *
 * @author Damião Santos
 *
 */
public class RmiClient {

    /**
     * @param name É o nome do Utilizador que estará logged.
     * @param privilegio É o tipo do Utilizador (Editor ou Leitor).
     *
     */
    static String name;
    static String privilegio;
    static String ip_rmi;
    static int PORTTCP = 1904;

    /**
     * É uma thread que está à espera de Notificações.
     */
    public static class Notificacoes extends Thread{
        private RmiInterface face;
        boolean running;

        /**
         * Construtor da thread.
         * @param faceT É o registo a que está ligado ao Rmi Server.
         */
        Notificacoes(RmiInterface faceT){
            face = faceT;
            running = true;
        }

        /**
         * A funcao que corre para estar à escuta de notificações.
         */
        public void run(){
            try {
                while (running) {
                    String resposta = face.Notificacoes(name);
                    if(resposta.compareTo("Fim") == 0){
                        return;
                    }
                    else
                        if(resposta.contains("Foi promovido a editor!"))
                            privilegio = "editor";
                        System.out.println("\n=== "+resposta+" ===\n");
                }
            } catch(Exception e){
                return;
            }
        }
    }

    /**
     * É o menu do Rmi Client após ter  dado login.
     * @param rmiInterface É o registo a que está ligado ao Rmi Server.
     */
    static void menu(RmiInterface rmiInterface){
        int rmiControlo = 0;
        while(true) {
            try {
                if(rmiControlo == 1){
                    rmiInterface = (RmiInterface) LocateRegistry.getRegistry(ip_rmi, 7000).lookup("rmiSERVER");
                }
                Notificacoes thread = new Notificacoes(rmiInterface);
                thread.start();
                String escolha;
                while (true) {
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

                    if (escolha.compareTo("1") == 0) {
                        while (escolha.compareTo("4") != 0) {
                            System.out.println("\n=== PESQUISAR ===");
                            System.out.println("1} - Pesquisar por nome de musica");
                            System.out.println("2) - Pesquisar por nome de artista");
                            System.out.println("3) - Pesquisar por nome de album");
                            System.out.println("4) - Voltar");
                            System.out.print("Escolha: ");
                            escolha = string.nextLine();
                            if (escolha.compareTo("1") == 0) {
                                int verifica = 0;
                                while (verifica == 0) {
                                    System.out.print("Introduza o nome da musica que deseja pesquisar: ");
                                    String nome = string.nextLine();
                                    String lista = rmiInterface.pedir_pesquisa(nome, "musica", name);
                                    if (lista.startsWith("Erro!")) {
                                        System.out.println(lista);
                                        verifica = 1;
                                    } else {
                                        int verificar = 0;
                                        while (verificar == 0) {
                                            int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                            int contador = 0;
                                            for (int i = 1; i <= length; i++) {
                                                System.out.println(i + ") Música: " + lista.split(";")[1].substring(6).split("/")[contador] + " | Artista: " + lista.split(";")[1].substring(6).split("/")[contador + 1]);
                                                contador = contador + 2;
                                            }
                                            System.out.println((length + 1) + ") Voltar");
                                            System.out.print("Escolha: ");
                                            try {
                                                escolha = string.nextLine();
                                                if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                    String detalhes = rmiInterface.pedir_detalhes(lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha) - 1], lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha)], name, "musica");
                                                    System.out.println("Nome musica: " + detalhes.split("/")[0]);
                                                    System.out.println("Nome Artista: " + detalhes.split("/")[1]);
                                                    System.out.println("Nome Album: " + detalhes.split("/")[2]);
                                                    System.out.println("Duracao: " + detalhes.split("/")[3]);
                                                    verifica = 1;
                                                    verificar = 1;
                                                    escolha = "4";
                                                } else if (Integer.parseInt(escolha) == (length + 1)) {
                                                    verifica = 1;
                                                    verificar = 1;
                                                    escolha = "0";
                                                } else {
                                                    System.out.println("Escolha não válida.");
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Escolha não válida.");
                                            }
                                        }
                                    }
                                }
                            } else if (escolha.compareTo("2") == 0) {
                                int verifica = 0;
                                while (verifica == 0) {
                                    System.out.print("Introduza o nome do artista que deseja pesquisar: ");
                                    String nome = string.nextLine();
                                    String lista = rmiInterface.pedir_pesquisa(nome, "artista", name);
                                    if (lista.startsWith("Erro!")) {
                                        System.out.println(lista);
                                        verifica = 1;
                                    } else {
                                        int verificar = 0;
                                        while (verificar == 0) {
                                            int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                            for (int i = 1; i <= length; i++) {
                                                System.out.println(i + ") Artista: " + lista.split(";")[1].substring(6).split("/")[i - 1]);
                                            }
                                            System.out.println((length + 1) + ") Voltar");
                                            System.out.print("Escolha: ");
                                            try {
                                                escolha = string.nextLine();
                                                if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                    String detalhes = rmiInterface.pedir_detalhes(lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha) - 1], "", name, "artista");
                                                    System.out.println("Nome Artista: " + detalhes.split("/")[0]);
                                                    for (int i = 1; i < detalhes.split("/").length; i++) {
                                                        System.out.println("Album: " + detalhes.split("/")[i]);
                                                    }
                                                    verifica = 1;
                                                    verificar = 1;
                                                    escolha = "4";
                                                } else if (Integer.parseInt(escolha) == (length + 1)) {
                                                    verifica = 1;
                                                    verificar = 1;
                                                    escolha = "0";
                                                } else {
                                                    System.out.println("Escolha não válida.");
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Escolha não válida.");
                                            }
                                        }
                                    }
                                }
                            } else if (escolha.compareTo("3") == 0) {
                                int verifica = 0;
                                while (verifica == 0) {
                                    System.out.print("Introduza o nome do album que deseja pesquisar: ");
                                    String nome = string.nextLine();
                                    String lista = rmiInterface.pedir_pesquisa(nome, "album", name);
                                    if (lista.startsWith("Erro!")) {
                                        System.out.println(lista);
                                        verifica = 1;
                                    } else {
                                        int verificar = 0;
                                        while (verificar == 0) {
                                            int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                            int contador = 0;
                                            for (int i = 1; i <= length; i++) {
                                                System.out.println(i + ") Album: " + lista.split(";")[1].substring(6).split("/")[contador] + " | Artista: " + lista.split(";")[1].substring(6).split("/")[contador + 1]);
                                                contador = contador + 2;
                                            }
                                            System.out.println((length + 1) + ") Voltar");
                                            System.out.print("Escolha: ");
                                            try {
                                                escolha = string.nextLine();
                                                if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                    String detalhes = rmiInterface.pedir_detalhes(lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha) - 1], lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha)], name, "album");
                                                    System.out.println("Nome Album: " + detalhes.split("/")[0]);
                                                    System.out.println("Nome Artista: " + detalhes.split("/")[1]);
                                                    for (int i = 3; i < Integer.parseInt(detalhes.split("/")[2]) + 3; i++) {
                                                        System.out.println("Musica: " + detalhes.split("/")[i]);
                                                    }
                                                    if (detalhes.split("/").length != 3 + Integer.parseInt(detalhes.split("/")[2])) {
                                                        for (int i = 3 + Integer.parseInt(detalhes.split("/")[2]); i < detalhes.split("/").length - 1; i++) {
                                                            System.out.println("Criticas: " + detalhes.split(";")[0].split("/")[i]);
                                                        }
                                                        System.out.println("Rating: " + detalhes.split("/")[detalhes.split("/").length - 1]);
                                                    }
                                                    int verificar2 = 0;
                                                    while (verificar2 == 0) {
                                                        System.out.println("\n1) Desejo introduzir uma critica");
                                                        System.out.println("2) Voltar");
                                                        System.out.print("Escolha: ");
                                                        escolha = string.nextLine();
                                                        if (escolha.compareTo("1") == 0) {
                                                            while (verificar2 == 0) {
                                                                System.out.print("Introduza a critica (max 300 caracteres): ");
                                                                String critica = string.nextLine();
                                                                System.out.print("Introduza uma nota (1 a 5): ");
                                                                String nota = string.nextLine();
                                                                String resposta = rmiInterface.enviarCritica(detalhes.split(";")[0].split("/")[0], detalhes.split(";")[0].split("/")[1], critica, nota, name);
                                                                if (resposta.startsWith("Erro!")) {
                                                                    System.out.println(resposta);
                                                                } else {
                                                                    System.out.println(resposta);
                                                                    verificar2 = 1;
                                                                    verifica = 1;
                                                                    verificar = 1;
                                                                    escolha = "4";
                                                                }
                                                            }
                                                        } else if (escolha.compareTo("2") == 0) {
                                                            verificar2 = 1;
                                                            verifica = 1;
                                                            verificar = 1;
                                                            escolha = "4";
                                                        } else {
                                                            System.out.println("Escolha nao valida.");
                                                        }
                                                    }
                                                } else if (Integer.parseInt(escolha) == (length + 1)) {
                                                    verifica = 1;
                                                    verificar = 1;
                                                    escolha = "0";
                                                } else {
                                                    System.out.println("Escolha não válida.");
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Escolha não válida.");
                                            }
                                        }
                                    }
                                }
                            } else if (escolha.compareTo("4") != 0) {
                                System.out.println("Escolha nao valida.");
                            }
                        }
                    } else if (escolha.compareTo("2") == 0) {
                        if (privilegio.compareTo("editor") != 0) {
                            System.out.println("\nAviso: Só os editores têm premissão para gerir.");
                        } else {
                            while (escolha.compareTo("4") != 0) {
                                System.out.println("\n=== GERIR ===");
                                System.out.println("1} - Inserir Musica,Artista ou Album");
                                System.out.println("2) - Alterar Musica,Artista ou Album");
                                System.out.println("3) - Remover Musica,Artista ou Album");
                                System.out.println("4) - Voltar");
                                System.out.print("Escolha: ");
                                escolha = string.nextLine();
                                if (escolha.compareTo("1") == 0) {
                                    while (escolha.compareTo("4") != 0 && escolha.compareTo("5") != 0) {
                                        System.out.println("\n=== GERIR (INSERIR) ===");
                                        System.out.println("1) - Inserir uma Musica");
                                        System.out.println("2) - Inserir um Artista");
                                        System.out.println("3) - Inserir um Album");
                                        System.out.println("4) - Voltar");
                                        System.out.print("Escolha: ");
                                        escolha = string.nextLine();
                                        if (escolha.compareTo("1") == 0) {
                                            int verifica = 0;
                                            while (verifica == 0) {
                                                System.out.println("\n=== INSERIR UMA MUSICA ===\n(Introduza '0' em todos os campos para recuar)");
                                                System.out.print("Nome da Música: ");
                                                String nome = string.nextLine();
                                                System.out.print("Artista da Música: ");
                                                String artista = string.nextLine();
                                                System.out.print("Album da Música: ");
                                                String album = string.nextLine();
                                                System.out.print("Duração da Música (min:seg): ");
                                                String duracao = string.nextLine();
                                                if (nome.compareTo("0") != 0 || artista.compareTo("0") != 0 || album.compareTo("0") != 0 || duracao.compareTo("0") != 0) {
                                                    String resposta = rmiInterface.inserirMusica(nome, artista, album, duracao, name);
                                                    if (resposta.startsWith("Erro!")) {
                                                        System.out.println(resposta);
                                                    } else {
                                                        System.out.println(resposta);
                                                        verifica = 1;
                                                        escolha = "4";
                                                    }
                                                } else {
                                                    verifica = 1;
                                                }
                                            }
                                        } else if (escolha.compareTo("2") == 0) {
                                            int verifica = 0;
                                            while (verifica == 0) {
                                                System.out.println("\n=== INSERIR UM ARTISTA ===\n(Introduza '0' em todos os campos para recuar)");
                                                System.out.print("Nome do Artista: ");
                                                String artista = string.nextLine();
                                                int controlo = 0;
                                                String album = "";
                                                while (controlo == 0) {
                                                    System.out.print("Um album do artista: ");
                                                    String aux = string.nextLine();
                                                    if (!aux.contains("/") && !aux.contains("|") && !aux.contains(";")) {
                                                        album = album + "/" + aux;
                                                        escolha = "0";
                                                        while (escolha.compareTo("1") != 0 && escolha.compareTo("2") != 0) {
                                                            System.out.println("1) Introduzir mais um album");
                                                            System.out.println("2) Finalizar");
                                                            System.out.print("Escolha: ");
                                                            escolha = string.nextLine();
                                                            if (escolha.compareTo("2") == 0) {
                                                                controlo = 1;
                                                            } else if (escolha.compareTo("1") != 0) {
                                                                System.out.println("Escolha não valida.");
                                                            }
                                                        }
                                                    } else {
                                                        System.out.println("Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.");
                                                    }
                                                }
                                                album = album.substring(1);
                                                if (artista.compareTo("0") != 0 || album.compareTo("0") != 0) {
                                                    String resposta = rmiInterface.inserirArtista(artista, album, name);
                                                    if (resposta.startsWith("Erro!")) {
                                                        System.out.println(resposta);
                                                    } else {
                                                        System.out.println(resposta);
                                                        verifica = 1;
                                                        escolha = "4";
                                                    }
                                                } else {
                                                    verifica = 1;
                                                }
                                            }
                                        } else if (escolha.compareTo("3") == 0) {
                                            int verifica = 0;
                                            while (verifica == 0) {
                                                System.out.println("\n=== INSERIR UM ALBUM ===\n(Introduza '0' em todos os campos para recuar)");
                                                System.out.print("Nome do Album: ");
                                                String album = string.nextLine();
                                                System.out.print("Artista do Album: ");
                                                String artista = string.nextLine();
                                                int controlo = 0;
                                                String musicas = "";
                                                while (controlo == 0) {
                                                    System.out.print("Uma música do album: ");
                                                    String aux = string.nextLine();
                                                    if (!aux.contains("/") && !aux.contains("|") && !aux.contains(";")) {
                                                        musicas = musicas + "/" + aux;
                                                        escolha = "0";
                                                        while (escolha.compareTo("1") != 0 && escolha.compareTo("2") != 0) {
                                                            System.out.println("1) Introduzir mais uma música");
                                                            System.out.println("2) Finalizar");
                                                            System.out.print("Escolha: ");
                                                            escolha = string.nextLine();
                                                            if (escolha.compareTo("2") == 0) {
                                                                controlo = 1;
                                                            } else if (escolha.compareTo("1") != 0) {
                                                                System.out.println("Escolha não valida.");
                                                            }
                                                        }
                                                    } else {
                                                        System.out.println("Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.");
                                                    }
                                                }
                                                musicas = musicas.substring(1);
                                                if (artista.compareTo("0") != 0 || album.compareTo("0") != 0 || musicas.compareTo("0") != 0) {
                                                    String resposta = rmiInterface.inserirAlbum(album, artista, musicas, name);
                                                    if (resposta.startsWith("Erro!")) {
                                                        System.out.println(resposta);
                                                    } else {
                                                        System.out.println(resposta);
                                                        verifica = 1;
                                                        escolha = "4";
                                                    }
                                                } else {
                                                    verifica = 1;
                                                }
                                            }
                                        } else if (escolha.compareTo("4") != 0) {
                                            escolha = "0";
                                            System.out.println("Escolha não valida.");
                                        }
                                    }
                                } else if (escolha.compareTo("2") == 0) {
                                    while (escolha.compareTo("4") != 0) {
                                        System.out.println("\n === GERIR (ALTERAR) ===");
                                        System.out.println("1) - Alterar uma Musica");
                                        System.out.println("2) - Alterar um Artista");
                                        System.out.println("3) - Alterar um Album");
                                        System.out.println("4) - Voltar");
                                        System.out.print("Escolha: ");
                                        escolha = string.nextLine();
                                        if (escolha.compareTo("1") == 0) {
                                            int verifica = 0;
                                            while (verifica == 0) {
                                                System.out.println("\n === ALTERAR UMA MÚSICA ===\n(Selecione a música que deseja alterar)");
                                                String lista = rmiInterface.listar("musica", name);
                                                int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                                int contador = 0;
                                                for (int i = 1; i <= length; i++) {
                                                    System.out.println(i + ") Música: " + lista.split(";")[1].substring(6).split("/")[contador] + " | Artista: " + lista.split(";")[1].substring(6).split("/")[contador + 1]);
                                                    contador = contador + 2;
                                                }
                                                System.out.println((length + 1) + ") Voltar");
                                                System.out.print("Escolha: ");
                                                try {
                                                    escolha = string.nextLine();
                                                    if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                        int index = Integer.parseInt(escolha) - 1;
                                                        int verificar = 0;
                                                        while (verificar == 0) {
                                                            System.out.println("Qual o campo que prentede alterar?");
                                                            System.out.println("1) Nome");
                                                            System.out.println("2) Artista");
                                                            System.out.println("3) Album");
                                                            System.out.println("4) Duração");
                                                            System.out.println("5) Voltar");
                                                            System.out.print("Escolha: ");
                                                            escolha = string.nextLine();
                                                            if (escolha.compareTo("1") == 0) {
                                                                System.out.print("Introduza o novo Nome:");
                                                                String novo = string.nextLine();
                                                                String resposta = rmiInterface.alterar("musica", Integer.toString(index), "nome", novo, name);
                                                                if (resposta.startsWith("Erro!")) {
                                                                    System.out.println(resposta);
                                                                } else {
                                                                    System.out.println(resposta);
                                                                    verificar = 1;
                                                                    verifica = 1;
                                                                    escolha = "4";
                                                                }
                                                            } else if (escolha.compareTo("2") == 0) {
                                                                System.out.print("Introduza o novo Artista:");
                                                                String novo = string.nextLine();
                                                                String resposta = rmiInterface.alterar("musica", Integer.toString(index), "artista", novo, name);
                                                                if (resposta.startsWith("Erro!")) {
                                                                    System.out.println(resposta);
                                                                } else {
                                                                    System.out.println(resposta);
                                                                    verificar = 1;
                                                                    verifica = 1;
                                                                    escolha = "4";
                                                                }
                                                            } else if (escolha.compareTo("3") == 0) {
                                                                System.out.print("Introduza o novo Album:");
                                                                String novo = string.nextLine();
                                                                String resposta = rmiInterface.alterar("musica", Integer.toString(index), "album", novo, name);
                                                                if (resposta.startsWith("Erro!")) {
                                                                    System.out.println(resposta);
                                                                } else {
                                                                    System.out.println(resposta);
                                                                    verificar = 1;
                                                                    verifica = 1;
                                                                    escolha = "4";
                                                                }
                                                            } else if (escolha.compareTo("4") == 0) {
                                                                System.out.print("Introduza a nova Duração:");
                                                                String novo = string.nextLine();
                                                                String resposta = rmiInterface.alterar("musica", Integer.toString(index), "duracao", novo, name);
                                                                if (resposta.startsWith("Erro!")) {
                                                                    System.out.println(resposta);
                                                                } else {
                                                                    System.out.println(resposta);
                                                                    verificar = 1;
                                                                    verifica = 1;
                                                                    escolha = "4";
                                                                }
                                                            } else if (escolha.compareTo("5") == 0) {
                                                                verificar = 1;
                                                                escolha = "0";
                                                            } else {
                                                                System.out.println("Escolha não válida.");
                                                            }
                                                        }
                                                    } else if (Integer.parseInt(escolha) == (length + 1)) {
                                                        verifica = 1;
                                                        escolha = "0";
                                                    } else {
                                                        System.out.println("Escolha não válida.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Escolha não válida.");
                                                }
                                            }
                                        } else if (escolha.compareTo("2") == 0) {
                                            int verifica = 0;
                                            while (verifica == 0) {
                                                System.out.println("\n === ALTERAR UM ARTISTA ===\n(Selecione o artista que deseja alterar)");
                                                String lista = rmiInterface.listar("artista", name);
                                                int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                                for (int i = 1; i <= length; i++) {
                                                    System.out.println(i + ") Artista: " + lista.split(";")[1].substring(6).split("/")[i - 1]);
                                                }
                                                System.out.println((length + 1) + ") Voltar");
                                                System.out.print("Escolha: ");
                                                try {
                                                    escolha = string.nextLine();
                                                    if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                        int index = Integer.parseInt(escolha) - 1;
                                                        int verificar = 0;
                                                        while (verificar == 0) {
                                                            System.out.println("Qual o campo que prentede alterar?");
                                                            System.out.println("1) Nome");
                                                            System.out.println("2) Albuns");
                                                            ;
                                                            System.out.println("3) Voltar");
                                                            System.out.print("Escolha: ");
                                                            escolha = string.nextLine();
                                                            if (escolha.compareTo("1") == 0) {
                                                                System.out.print("Introduza o novo Nome:");
                                                                String novo = string.nextLine();
                                                                String resposta = rmiInterface.alterar("artista", Integer.toString(index), "nome", novo, name);
                                                                if (resposta.startsWith("Erro!")) {
                                                                    System.out.println(resposta);
                                                                } else {
                                                                    System.out.println(resposta);
                                                                    verificar = 1;
                                                                    verifica = 1;
                                                                    escolha = "4";
                                                                }
                                                            } else if (escolha.compareTo("2") == 0) {
                                                                int controlo = 0;
                                                                String album = "";
                                                                while (controlo == 0) {
                                                                    System.out.print("Introduza um album do artista: ");
                                                                    String aux = string.nextLine();
                                                                    if (!aux.contains("/") && !aux.contains("|") && !aux.contains(";")) {
                                                                        album = album + "/" + aux;
                                                                        escolha = "0";
                                                                        while (escolha.compareTo("1") != 0 && escolha.compareTo("2") != 0) {
                                                                            System.out.println("1) Introduzir mais um album");
                                                                            System.out.println("2) Finalizar");
                                                                            System.out.print("Escolha: ");
                                                                            escolha = string.nextLine();
                                                                            if (escolha.compareTo("2") == 0) {
                                                                                controlo = 1;
                                                                            } else if (escolha.compareTo("1") != 0) {
                                                                                System.out.println("Escolha não valida.");
                                                                            }
                                                                        }
                                                                    } else {
                                                                        System.out.println("Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.");
                                                                    }
                                                                }
                                                                album = album.substring(1);
                                                                String resposta = rmiInterface.alterar("artista", Integer.toString(index), "albuns", album, name);
                                                                if (resposta.startsWith("Erro!")) {
                                                                    System.out.println(resposta);
                                                                } else {
                                                                    System.out.println(resposta);
                                                                    verificar = 1;
                                                                    verifica = 1;
                                                                    escolha = "4";
                                                                }
                                                            } else if (escolha.compareTo("3") == 0) {
                                                                verificar = 1;
                                                                escolha = "0";
                                                            } else {
                                                                System.out.println("Escolha não válida.");
                                                            }
                                                        }
                                                    } else if (Integer.parseInt(escolha) == (length + 1)) {
                                                        verifica = 1;
                                                        escolha = "0";
                                                    } else {
                                                        System.out.println("Escolha não válida.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Escolha não válida.");
                                                }
                                            }
                                        } else if (escolha.compareTo("3") == 0) {
                                            int verifica = 0;
                                            while (verifica == 0) {
                                                System.out.println("\n === ALTERAR UM ALBUM ===\n(Selecione o album que deseja alterar)");
                                                String lista = rmiInterface.listar("album", name);
                                                int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                                int contador = 0;
                                                for (int i = 1; i <= length; i++) {
                                                    System.out.println(i + ") Album: " + lista.split(";")[1].substring(6).split("/")[contador] + " | Artista: " + lista.split(";")[1].substring(6).split("/")[contador + 1]);
                                                    contador = contador + 2;
                                                }
                                                System.out.println((length + 1) + ") Voltar");
                                                System.out.print("Escolha: ");
                                                try {
                                                    escolha = string.nextLine();
                                                    if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                        int index = Integer.parseInt(escolha) - 1;
                                                        int verificar = 0;
                                                        while (verificar == 0) {
                                                            System.out.println("Qual o campo que prentede alterar?");
                                                            System.out.println("1) Nome");
                                                            System.out.println("2) Músicas");
                                                            ;
                                                            System.out.println("3) Voltar");
                                                            System.out.print("Escolha: ");
                                                            escolha = string.nextLine();
                                                            if (escolha.compareTo("1") == 0) {
                                                                System.out.print("Introduza o novo Nome:");
                                                                String novo = string.nextLine();
                                                                String resposta = rmiInterface.alterar("album", Integer.toString(index), "nome", novo, name);
                                                                if (resposta.startsWith("Erro!")) {
                                                                    System.out.println(resposta);
                                                                } else {
                                                                    System.out.println(resposta);
                                                                    verificar = 1;
                                                                    verifica = 1;
                                                                    escolha = "4";
                                                                }
                                                            } else if (escolha.compareTo("2") == 0) {
                                                                int controlo = 0;
                                                                String album = "";
                                                                while (controlo == 0) {
                                                                    System.out.print("Introduza uma música do album: ");
                                                                    String aux = string.nextLine();
                                                                    if (!aux.contains("/") && !aux.contains("|") && !aux.contains(";")) {
                                                                        album = album + "/" + aux;
                                                                        escolha = "0";
                                                                        while (escolha.compareTo("1") != 0 && escolha.compareTo("2") != 0) {
                                                                            System.out.println("1) Introduzir mais uma música");
                                                                            System.out.println("2) Finalizar");
                                                                            System.out.print("Escolha: ");
                                                                            escolha = string.nextLine();
                                                                            if (escolha.compareTo("2") == 0) {
                                                                                controlo = 1;
                                                                            } else if (escolha.compareTo("1") != 0) {
                                                                                System.out.println("Escolha não valida.");
                                                                            }
                                                                        }
                                                                    } else {
                                                                        System.out.println("Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.");
                                                                    }
                                                                }
                                                                album = album.substring(1);
                                                                String resposta = rmiInterface.alterar("album", Integer.toString(index), "musicas", album, name);
                                                                if (resposta.startsWith("Erro!")) {
                                                                    System.out.println(resposta);
                                                                } else {
                                                                    System.out.println(resposta);
                                                                    verificar = 1;
                                                                    verifica = 1;
                                                                    escolha = "4";
                                                                }
                                                            } else if (escolha.compareTo("3") == 0) {
                                                                verificar = 1;
                                                                escolha = "0";
                                                            } else {
                                                                System.out.println("Escolha não válida.");
                                                            }
                                                        }
                                                    } else if (Integer.parseInt(escolha) == (length + 1)) {
                                                        verifica = 1;
                                                        escolha = "0";
                                                    } else {
                                                        System.out.println("Escolha não válida.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Escolha não válida.");
                                                }
                                            }
                                        } else if (escolha.compareTo("4") != 0) {
                                            escolha = "0";
                                            System.out.println("Escolha não valida.");
                                        }
                                    }
                                } else if (escolha.compareTo("3") == 0) {
                                    while (escolha.compareTo("4") != 0) {
                                        System.out.println("\n === GERIR (REMOVER) ===");
                                        System.out.println("1) - Remover uma Musica");
                                        System.out.println("2) - Remover um Artista");
                                        System.out.println("3) - Remover um Album");
                                        System.out.println("4) - Voltar");
                                        System.out.print("Escolha: ");
                                        escolha = string.nextLine();
                                        if (escolha.compareTo("1") == 0) {
                                            int verifica = 0;
                                            while (verifica == 0) {
                                                System.out.println("\n === REMOVER UMA MÚSICA ===\n(Selecione a música que deseja remover)");
                                                String lista = rmiInterface.listar("musica", name);
                                                int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                                int contador = 0;
                                                for (int i = 1; i <= length; i++) {
                                                    System.out.println(i + ") Música: " + lista.split(";")[1].substring(6).split("/")[contador] + " | Artista: " + lista.split(";")[1].substring(6).split("/")[contador + 1]);
                                                    contador = contador + 2;
                                                }
                                                System.out.println((length + 1) + ") Voltar");
                                                System.out.print("Escolha: ");
                                                try {
                                                    escolha = string.nextLine();
                                                    if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                        int index = Integer.parseInt(escolha) - 1;
                                                        String resposta = rmiInterface.remover("musica", Integer.toString(index), name);
                                                        if (resposta.startsWith("Erro!")) {
                                                            System.out.println(resposta);
                                                        } else {
                                                            System.out.println(resposta);
                                                            verifica = 1;
                                                            escolha = "4";
                                                        }
                                                    } else if (Integer.parseInt(escolha) == (length + 1)) {
                                                        verifica = 1;
                                                        escolha = "0";
                                                    } else {
                                                        System.out.println("Escolha não válida.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Escolha não válida.");
                                                }
                                            }
                                        } else if (escolha.compareTo("2") == 0) {
                                            int verifica = 0;
                                            while (verifica == 0) {
                                                System.out.println("\n === REMOVER UM ARTISTA ===\n(Selecione o artista que deseja remover)");
                                                String lista = rmiInterface.listar("artista", name);
                                                int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                                for (int i = 1; i <= length; i++) {
                                                    System.out.println(i + ") Artista: " + lista.split(";")[1].substring(6).split("/")[i - 1]);
                                                }
                                                System.out.println((length + 1) + ") Voltar");
                                                System.out.print("Escolha: ");
                                                try {
                                                    escolha = string.nextLine();
                                                    if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                        int index = Integer.parseInt(escolha) - 1;
                                                        String resposta = rmiInterface.remover("artista", Integer.toString(index), name);
                                                        if (resposta.startsWith("Erro!")) {
                                                            System.out.println(resposta);
                                                        } else {
                                                            System.out.println(resposta);
                                                            verifica = 1;
                                                            escolha = "4";
                                                        }
                                                    } else if (Integer.parseInt(escolha) == (length + 1)) {
                                                        verifica = 1;
                                                        escolha = "0";
                                                    } else {
                                                        System.out.println("Escolha não válida.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Escolha não válida.");
                                                }
                                            }
                                        } else if (escolha.compareTo("3") == 0) {
                                            int verifica = 0;
                                            while (verifica == 0) {
                                                System.out.println("\n === REMOVER UM ALBUM ===\n(Selecione o album que deseja remover)");
                                                String lista = rmiInterface.listar("album", name);
                                                int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                                int contador = 0;
                                                for (int i = 1; i <= length; i++) {
                                                    System.out.println(i + ") Album: " + lista.split(";")[1].substring(6).split("/")[contador] + " | Artista: " + lista.split(";")[1].substring(6).split("/")[contador + 1]);
                                                    contador = contador + 2;
                                                }
                                                System.out.println((length + 1) + ") Voltar");
                                                System.out.print("Escolha: ");
                                                try {
                                                    escolha = string.nextLine();
                                                    if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                        int index = Integer.parseInt(escolha) - 1;
                                                        String resposta = rmiInterface.remover("album", Integer.toString(index), name);
                                                        if (resposta.startsWith("Erro!")) {
                                                            System.out.println(resposta);
                                                        } else {
                                                            System.out.println(resposta);
                                                            verifica = 1;
                                                            escolha = "4";
                                                        }
                                                    } else if (Integer.parseInt(escolha) == (length + 1)) {
                                                        verifica = 1;
                                                        escolha = "0";
                                                    } else {
                                                        System.out.println("Escolha não válida.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Escolha não válida.");
                                                }
                                            }
                                        } else if (escolha.compareTo("4") != 0) {
                                            escolha = "0";
                                            System.out.println("Escolha não valida.");
                                        }
                                    }
                                } else if (escolha.compareTo("4") != 0) {
                                    System.out.println("Escolha não valida.");
                                    escolha = "0";
                                }
                            }
                        }
                    } else if (escolha.compareTo("3") == 0) {
                        if (privilegio.compareTo("editor") != 0) {
                            System.out.println("\nAviso: Só os editores têm premissão para dar privilegio de editor.");

                        } else {
                            String lista = rmiInterface.pedirUtilizadores(name);
                            int verifica = 0;
                            while (verifica == 0) {
                                System.out.println("=== DAR PRIVILEGIO ===\n(Utilizadores Leitores)");
                                for (int i = 0; i < Integer.parseInt(lista.split(";")[1].substring(7)); i++) {
                                    System.out.println((i + 1) + ") " + lista.split(";")[2].substring(6).split("/")[i]);
                                }
                                System.out.println((Integer.parseInt(lista.split(";")[1].substring(7)) + 1) + ") Voltar");
                                System.out.print("Escolha: ");
                                escolha = string.nextLine();
                                try {
                                    if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= Integer.parseInt(lista.split(";")[1].substring(7))) {
                                        String resposta = rmiInterface.promover(lista.split(";")[2].substring(6).split("/")[Integer.parseInt(escolha)-1], name);
                                        System.out.println(resposta);
                                        verifica = 1;
                                        escolha = "0";
                                    } else if (Integer.parseInt(escolha) == Integer.parseInt(lista.split(";")[1].substring(7)) + 1) {
                                        verifica = 1;
                                    } else {
                                        System.out.println("Escolha nao valida");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Escolha nao valida.");
                                }
                            }
                        }
                    } else if (escolha.compareTo("4") == 0) {
                        int controlo = 0;
                        System.out.println("\n=== UPLOAD MUSICA ===\n(Escolha a musica que deseja dar upload)\n");
                        String lista = rmiInterface.listar("musica",name);
                        int length = Integer.parseInt(lista.split(";")[0].substring(7));
                        int contador = 0;
                        for (int i = 1; i <= length; i++) {
                            System.out.println(i + ") Música: " + lista.split(";")[1].substring(6).split("/")[contador] + " | Artista: " + lista.split(";")[1].substring(6).split("/")[contador + 1]);
                            contador = contador + 2;
                        }
                        System.out.println((length + 1) + ") Voltar");
                        int verifica = 0;
                        while(verifica == 0) {
                            System.out.print("Escolha: ");
                            try {
                                escolha = string.nextLine();
                                if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                    while(controlo == 0) {
                                        try {
                                            System.out.print("Introduza o nome/localizacao da musica: ");
                                            String ficheiro = string.nextLine();
                                            File file = new File(ficheiro);
                                            String resposta = rmiInterface.tcp(file, "upload", name, Integer.parseInt(escolha)-1, "", "");
                                            if(resposta.split(";")[3].substring(4).startsWith("Erro!")){
                                                System.out.println(resposta.split(";")[3].substring(4));
                                            }
                                            else {
                                                Socket clientSocket = new Socket(resposta.split(";")[1].substring(3), PORTTCP);
                                                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                                                OutputStream os = clientSocket.getOutputStream();
                                                byte[] contents;
                                                long fileLength = file.length();
                                                long current = 0;

                                                while (current != fileLength) {
                                                    int size = 10000;
                                                    if (fileLength - current >= size)
                                                        current += size;
                                                    else {
                                                        size = (int) (fileLength - current);
                                                        current = fileLength;
                                                    }
                                                    contents = new byte[size];
                                                    bis.read(contents, 0, size);
                                                    os.write(contents);
                                                }
                                                os.flush();
                                                clientSocket.close();
                                                System.out.println("Ficheiro enviado com sucesso.");
                                                controlo = 1;
                                                verifica = 1;
                                                escolha = "0";
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else if (Integer.parseInt(escolha) == (length + 1)) {
                                    verifica = 1;
                                    escolha = "0";
                                } else {
                                    System.out.println("Escolha não válida.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Escolha não válida.");
                            }
                        }
                    } else if (escolha.compareTo("5") == 0) {
                        System.out.println("\n=== DOWNLOAD MUSICA ===\n(Escolha a musica que deseja dar download)\n");
                        String lista = rmiInterface.listar("musica",name);
                        int length = Integer.parseInt(lista.split(";")[0].substring(7));
                        int contador = 0;
                        for (int i = 1; i <= length; i++) {
                            System.out.println(i + ") Música: " + lista.split(";")[1].substring(6).split("/")[contador] + " | Artista: " + lista.split(";")[1].substring(6).split("/")[contador + 1]);
                            contador = contador + 2;
                        }
                        System.out.println((length + 1) + ") Voltar");
                        int verifica = 0;
                        while(verifica == 0) {
                            System.out.print("Escolha: ");
                            try {
                                escolha = string.nextLine();
                                if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                    String resposta = rmiInterface.tcp(null, "download", name, Integer.parseInt(escolha) - 1, lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha) * 2 - 2], lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha) * 2 - 1]);
                                    if(resposta.split(";")[3].substring(4).startsWith("Erro!")){
                                        System.out.println(resposta.split(";")[3].substring(4));
                                    }
                                    else {
                                        Socket clientSocket = new Socket(resposta.split(";")[1].substring(3), PORTTCP);
                                        byte[] mybytearray = new byte[10000];
                                        InputStream is = clientSocket.getInputStream();
                                        int bytesRead = 0;
                                        if (is.read(mybytearray) != -1) {
                                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha) * 2 - 2] + " - " + lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha) * 2 - 1] + ".mp3"));
                                            while ((bytesRead = is.read(mybytearray)) != -1)
                                                bos.write(mybytearray, 0, bytesRead);
                                            bos.flush();
                                            clientSocket.close();
                                            System.out.println("Ficheiro recebido com sucesso.");
                                            verifica = 1;
                                            escolha = "0";
                                        } else {
                                            clientSocket.close();
                                            System.out.println("Erro! A musica nao existe.");
                                        }
                                    }
                                } else if (Integer.parseInt(escolha) == (length + 1)) {
                                    verifica = 1;
                                    escolha = "0";
                                } else {
                                    System.out.println("Escolha não válida.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Escolha não válida.");
                            }
                        }
                    } else if (escolha.compareTo("6") == 0) {
                        int controlo = 0;
                        System.out.println("\n=== DAR PERMISSAO DE BIBLIOTECA ===\n(Escolha a musica que deseja dar permissao)\n");
                        String lista = rmiInterface.pedirBiblioteca(name);
                        int length = Integer.parseInt(lista.split(";")[0].substring(7));
                        int contador = 0;
                        for (int i = 1; i <= length; i++) {
                            System.out.println(i + ") Música: " + lista.split(";")[1].substring(6).split("/")[contador] + " | Artista: " + lista.split(";")[1].substring(6).split("/")[contador + 1]);
                            contador = contador + 2;
                        }
                        System.out.println((length + 1) + ") Voltar");
                        int verifica = 0;
                        while(verifica == 0) {
                            System.out.print("Escolha: ");
                            try {
                                escolha = string.nextLine();
                                if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                    while(controlo == 0) {
                                        System.out.print("Introduza o nome do utilizador com quem deseja partilhar: ");
                                        String utilizador = string.nextLine();
                                        String resposta = rmiInterface.permissao(utilizador, name, lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha)*2-2], lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha)*2-1]);
                                        if(resposta.startsWith("Erro!")){
                                            System.out.println(resposta);
                                        }
                                        else{
                                            System.out.println(resposta);
                                            controlo = 1;
                                            verifica = 1;
                                            escolha = "0";
                                        }
                                    }
                                } else if (Integer.parseInt(escolha) == (length + 1)) {
                                    verifica = 1;
                                    escolha = "0";
                                } else {
                                    System.out.println("Escolha não válida.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Escolha não válida.");
                            }
                        }
                    } else if (escolha.compareTo("7") == 0) {
                        rmiInterface.killThread(name);
                        thread.running = false;
                        return;
                    } else {
                        System.out.println("Escolha nao valida.");
                    }
                }
            } catch (Exception e) {
                System.out.println("\n!!! RmiServer foi abaixo, servidor backup foi ligado, o login nao sera perdido !!!");
                rmiControlo = 1;
            }
        }
    }

    /**
     * A função que inicia o Rmi Client apresentando o menu inicial antes do login.
     * @param args Sem valor.
     */
    public static void main(String args[]){
        ip_rmi=args[0];
        int rmiControlo = 0;
        while(rmiControlo == 0) {
            try {
                RmiInterface rmiInterface = (RmiInterface) LocateRegistry.getRegistry(ip_rmi,7000).lookup("rmiSERVER");
                String escolha;
                while (true) {
                    System.out.println("\n=== Projeto de SD ===");
                    System.out.println("1) - Fazer Login");
                    System.out.println("2) - Registo");
                    System.out.println("3) - Sair");
                    System.out.print("Escolha: ");

                    Scanner string = new Scanner(System.in);
                    escolha = string.nextLine();

                    if (escolha.compareTo("1") == 0) {
                        System.out.println("\n=== LOGIN ===\n(Introduza '0' em todos os campos para recuar)");
                        int controlo = 0;
                        while (controlo == 0) {
                            System.out.print("Nome de Utilizador: ");
                            String username = string.nextLine();
                            System.out.print("Password: ");
                            String password = string.nextLine();
                            if (!username.equals("0") || !password.equals("0")) {
                                String resposta = rmiInterface.verificaLogin(username, password);
                                if (resposta.startsWith("Erro!")) {
                                    System.out.println(resposta);
                                } else {
                                    name = username;
                                    privilegio = resposta.split(";")[4].substring(11,17);
                                    System.out.println(resposta.split(";")[2].substring(4));
                                    menu(rmiInterface);
                                    controlo = 1;
                                }
                            } else {
                                controlo = 1;
                            }
                        }
                    } else if (escolha.compareTo("2") == 0) {
                        System.out.println("\n=== SIGN UP ===\n(Introduza '0' em todos os campos para recuar)");
                        int controlo = 0;
                        while (controlo == 0) {
                            System.out.print("Nome de Utilizador: ");
                            String username = string.nextLine();
                            System.out.print("Password: ");
                            String password = string.nextLine();
                            if (!username.equals("0") || !password.equals("0")) {
                                String resposta = rmiInterface.verificaSignUp(username, password);
                                if (resposta.startsWith("Erro!")) {
                                    System.out.println(resposta);
                                } else {
                                    name = username;
                                    privilegio = "Normal";
                                    System.out.println(resposta);
                                    System.out.println("\n=== Bem-vindo ao DropMusic ===\n");
                                    menu(rmiInterface);
                                    controlo = 1;
                                }
                            } else {
                                controlo = 1;
                            }
                        }
                    } else if (escolha.compareTo("3") == 0) {
                        return;
                    } else {
                        System.out.println("Escolha nao valida!");
                    }
                }
            } catch (Exception e) {
                System.out.println("\n!!! RmiServer foi abaixo, servidor backup foi ligado !!!");
            }
        }
    }

}