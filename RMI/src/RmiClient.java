import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.Scanner;

public class RmiClient {

    static String name;
    static String privilegio;

    static void menu(RmiInterface rmiInterface){
        try{
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
                    while(escolha.compareTo("4") != 0) {
                        System.out.println("\n=== PESQUISAR ===");
                        System.out.println("1} - Pesquisar por nome de musica");
                        System.out.println("2) - Pesquisar por nome de artista");
                        System.out.println("3) - Pesquisar por nome de album");
                        System.out.println("4) - Voltar");
                        System.out.print("Escolha: ");
                        escolha = string.nextLine();
                        if(escolha.compareTo("1") == 0){
                            int verifica = 0;
                            while(verifica == 0) {
                                System.out.print("Introduza o nome da musica que deseja pesquisar: ");
                                String nome = string.nextLine();
                                String lista = rmiInterface.pedir_pesquisa(nome, "musica", name);
                                if(lista.startsWith("Erro!")){
                                    System.out.println(lista);
                                    verifica = 1;
                                }
                                else{
                                    int verificar = 0;
                                    while(verificar == 0) {
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
                                                String detalhes = rmiInterface.pedir_detalhes(lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha) - 1],lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha)],name, "musica");
                                                System.out.println("Nome musica: "+detalhes.split("/")[0]);
                                                System.out.println("Nome Artista: "+detalhes.split("/")[1]);
                                                System.out.println("Nome Album: "+detalhes.split("/")[2]);
                                                System.out.println("Duracao: "+detalhes.split("/")[3]);
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
                        }
                        else if(escolha.compareTo("2") == 0){
                            int verifica = 0;
                            while(verifica == 0) {
                                System.out.print("Introduza o nome do artista que deseja pesquisar: ");
                                String nome = string.nextLine();
                                String lista = rmiInterface.pedir_pesquisa(nome, "artista", name);
                                if(lista.startsWith("Erro!")){
                                    System.out.println(lista);
                                    verifica = 1;
                                }
                                else{
                                    int verificar = 0;
                                    while(verificar == 0) {
                                        int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                        for (int i = 1; i <= length; i++) {
                                            System.out.println(i + ") Artista: " + lista.split(";")[1].substring(6).split("/")[i-1]);
                                        }
                                        System.out.println((length + 1) + ") Voltar");
                                        System.out.print("Escolha: ");
                                        try {
                                            escolha = string.nextLine();
                                            if (Integer.parseInt(escolha) >= 1 && Integer.parseInt(escolha) <= length) {
                                                String detalhes = rmiInterface.pedir_detalhes(lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha) - 1],"",name, "artista");
                                                System.out.println("Nome Artista: "+detalhes.split("/")[0]);
                                                for(int i = 1; i<detalhes.split("/").length; i++){
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
                        }
                        else if(escolha.compareTo("3") == 0){
                            int verifica = 0;
                            while(verifica == 0) {
                                System.out.print("Introduza o nome do album que deseja pesquisar: ");
                                String nome = string.nextLine();
                                String lista = rmiInterface.pedir_pesquisa(nome, "album", name);
                                if(lista.startsWith("Erro!")){
                                    System.out.println(lista);
                                    verifica = 1;
                                }
                                else{
                                    int verificar = 0;
                                    while(verificar == 0) {
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
                                                String detalhes = rmiInterface.pedir_detalhes(lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha)-1],lista.split(";")[1].substring(6).split("/")[Integer.parseInt(escolha)],name, "album");
                                                System.out.println("Nome Album: "+detalhes.split(";")[0].split("/")[0]);
                                                System.out.println("Nome Artista: "+detalhes.split(";")[0].split("/")[1]);
                                                for(int i = 2; i<detalhes.split(";")[0].split("/").length; i++){
                                                    System.out.println("Musica: " + detalhes.split(";")[0].split("/")[i]);
                                                }
                                                if(detalhes.split(";").length == 3) {
                                                    for (int i = 0; i < detalhes.split(";")[1].split("/").length - 1; i++) {
                                                        System.out.println("Criticas: " + detalhes.split(";")[1].split("/")[i]);
                                                    }
                                                    System.out.println("Rating: " + detalhes.split(";")[1].split("/")[detalhes.split(";")[1].split("/").length]);
                                                }
                                                int verificar2 = 0;
                                                while(verificar2 == 0) {
                                                    System.out.println("\n1) Desejo introduzir uma critica");
                                                    System.out.println("2) Voltar");
                                                    System.out.print("Escolha: ");
                                                    escolha = string.nextLine();
                                                    if (escolha.compareTo("1") == 0) {
                                                        while(verificar2 == 0) {
                                                            System.out.print("Introduza a critica (max 300 caracteres): ");
                                                            String critica = string.nextLine();
                                                            System.out.print("Introduza uma nota (1 a 5): ");
                                                            String nota = string.nextLine();
                                                            String resposta = rmiInterface.enviarCritica(detalhes.split(";")[0].split("/")[0],detalhes.split(";")[0].split("/")[1],critica,nota,name);
                                                            if(resposta.startsWith("Erro!")){
                                                                System.out.println(resposta);
                                                            }
                                                            else{
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
                        }
                        else if(escolha.compareTo("4") != 0){
                            System.out.println("Escolha nao valida.");
                        }
                    }
                }
                else if(escolha.compareTo("2") == 0){
                    if(privilegio.compareTo("editor") != 0){
                        System.out.println("\nAviso: Só os editores têm premissão para gerir.");
                    }
                    else {
                        while (escolha.compareTo("4") != 0) {
                            System.out.println("\n=== GERIR ===");
                            System.out.println("1} - Inserir Musica,Artista ou Album");
                            System.out.println("2) - Alterar Musica,Artista ou Album");
                            System.out.println("3) - Remover Musica,Artista ou Album");
                            System.out.println("4) - Voltar");
                            System.out.print("Escolha: ");
                            escolha = string.nextLine();
                            if(escolha.compareTo("1") == 0){
                                while(escolha.compareTo("4") != 0 && escolha.compareTo("5") != 0){
                                    System.out.println("\n=== GERIR (INSERIR) ===");
                                    System.out.println("1) - Inserir uma Musica");
                                    System.out.println("2) - Inserir um Artista");
                                    System.out.println("3) - Inserir um Album");
                                    System.out.println("4) - Voltar");
                                    System.out.print("Escolha: ");
                                    escolha = string.nextLine();
                                    if(escolha.compareTo("1") == 0){
                                        int verifica = 0;
                                        while(verifica == 0) {
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
                                                if(resposta.startsWith("Erro!")){
                                                    System.out.println(resposta);
                                                }
                                                else{
                                                    System.out.println(resposta);
                                                    verifica = 1;
                                                    escolha = "4";
                                                }
                                            }
                                            else{
                                                verifica = 1;
                                            }
                                        }
                                    }
                                    else if(escolha.compareTo("2") == 0){
                                        int verifica = 0;
                                        while(verifica == 0) {
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
                                                if(resposta.startsWith("Erro!")){
                                                    System.out.println(resposta);
                                                }
                                                else{
                                                    System.out.println(resposta);
                                                    verifica = 1;
                                                    escolha = "4";
                                                }
                                            }
                                            else{
                                                verifica = 1;
                                            }
                                        }
                                    }
                                    else if(escolha.compareTo("3") == 0){
                                        int verifica = 0;
                                        while(verifica == 0) {
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
                                                if(resposta.startsWith("Erro!")){
                                                    System.out.println(resposta);
                                                }
                                                else{
                                                    System.out.println(resposta);
                                                    verifica = 1;
                                                    escolha = "4";
                                                }
                                            }
                                            else{
                                                verifica = 1;
                                            }
                                        }
                                    }
                                    else if(escolha.compareTo("4") != 0){
                                        escolha = "0";
                                        System.out.println("Escolha não valida.");
                                    }
                                }
                            }
                            else if(escolha.compareTo("2") == 0){
                                while(escolha.compareTo("4") != 0) {
                                    System.out.println("\n === GERIR (ALTERAR) ===");
                                    System.out.println("1) - Alterar uma Musica");
                                    System.out.println("2) - Alterar um Artista");
                                    System.out.println("3) - Alterar um Album");
                                    System.out.println("4) - Voltar");
                                    System.out.print("Escolha: ");
                                    escolha = string.nextLine();
                                    if(escolha.compareTo("1") == 0){
                                        int verifica = 0;
                                        while(verifica == 0) {
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
                                                    while(verificar == 0) {
                                                        System.out.println("Qual o campo que prentede alterar?");
                                                        System.out.println("1) Nome");
                                                        System.out.println("2) Artista");
                                                        System.out.println("3) Album");
                                                        System.out.println("4) Duração");
                                                        System.out.println("5) Voltar");
                                                        System.out.print("Escolha: ");
                                                        escolha = string.nextLine();
                                                        if (escolha.compareTo("1") == 0){
                                                            System.out.print("Introduza o novo Nome:");
                                                            String novo = string.nextLine();
                                                            String resposta = rmiInterface.alterar("musica", Integer.toString(index), "nome", novo, name);
                                                            if(resposta.startsWith("Erro!")){
                                                                System.out.println(resposta);
                                                            }
                                                            else{
                                                                System.out.println(resposta);
                                                                verificar = 1;
                                                                verifica = 1;
                                                                escolha = "4";
                                                            }
                                                        }
                                                        else if(escolha.compareTo("2") == 0){
                                                            System.out.print("Introduza o novo Artista:");
                                                            String novo = string.nextLine();
                                                            String resposta = rmiInterface.alterar("musica", Integer.toString(index), "artista", novo, name);
                                                            if(resposta.startsWith("Erro!")){
                                                                System.out.println(resposta);
                                                            }
                                                            else{
                                                                System.out.println(resposta);
                                                                verificar = 1;
                                                                verifica = 1;
                                                                escolha = "4";
                                                            }
                                                        }
                                                        else if(escolha.compareTo("3") == 0){
                                                            System.out.print("Introduza o novo Album:");
                                                            String novo = string.nextLine();
                                                            String resposta = rmiInterface.alterar("musica", Integer.toString(index), "album", novo, name);
                                                            if(resposta.startsWith("Erro!")){
                                                                System.out.println(resposta);
                                                            }
                                                            else{
                                                                System.out.println(resposta);
                                                                verificar = 1;
                                                                verifica = 1;
                                                                escolha = "4";
                                                            }
                                                        }
                                                        else if(escolha.compareTo("4") == 0){
                                                            System.out.print("Introduza a nova Duração:");
                                                            String novo = string.nextLine();
                                                            String resposta = rmiInterface.alterar("musica", Integer.toString(index), "duracao", novo, name);
                                                            if(resposta.startsWith("Erro!")){
                                                                System.out.println(resposta);
                                                            }
                                                            else{
                                                                System.out.println(resposta);
                                                                verificar = 1;
                                                                verifica = 1;
                                                                escolha = "4";
                                                            }
                                                        }
                                                        else if(escolha.compareTo("5") == 0){
                                                            verificar = 1;
                                                            escolha = "0";
                                                        }
                                                        else{
                                                            System.out.println("Escolha não válida.");
                                                        }
                                                    }
                                                }
                                                else if(Integer.parseInt(escolha) == (length+1)){
                                                    verifica = 1;
                                                    escolha = "0";
                                                }
                                                else{
                                                    System.out.println("Escolha não válida.");
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Escolha não válida.");
                                            }
                                        }
                                    }
                                    else if(escolha.compareTo("2") == 0){
                                        int verifica = 0;
                                        while(verifica == 0) {
                                            System.out.println("\n === ALTERAR UM ARTISTA ===\n(Selecione o artista que deseja alterar)");
                                            String lista = rmiInterface.listar("artista", name);
                                            int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                            for (int i = 1; i <= length; i++) {
                                                System.out.println(i + ") Artista: " + lista.split(";")[1].substring(6).split("/")[i-1]);
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
                                                        System.out.println("2) Albuns");;
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
                                    }
                                    else if(escolha.compareTo("3") == 0){
                                        int verifica = 0;
                                        while(verifica == 0) {
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
                                    }
                                    else if(escolha.compareTo("4") != 0){
                                        escolha = "0";
                                        System.out.println("Escolha não valida.");
                                    }
                                }
                            }
                            else if(escolha.compareTo("3") == 0){
                                while(escolha.compareTo("4") != 0) {
                                    System.out.println("\n === GERIR (REMOVER) ===");
                                    System.out.println("1) - Remover uma Musica");
                                    System.out.println("2) - Remover um Artista");
                                    System.out.println("3) - Remover um Album");
                                    System.out.println("4) - Voltar");
                                    System.out.print("Escolha: ");
                                    escolha = string.nextLine();
                                    if(escolha.compareTo("1") == 0){
                                        int verifica = 0;
                                        while(verifica == 0) {
                                            System.out.println("\n === Remover UMA MÚSICA ===\n(Selecione a música que deseja remover)");
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
                                                    if(resposta.startsWith("Erro!")){
                                                        System.out.println(resposta);
                                                    }
                                                    else{
                                                        System.out.println(resposta);
                                                        verifica = 1;
                                                        escolha = "4";
                                                    }
                                                }
                                                else if(Integer.parseInt(escolha) == (length+1)){
                                                    verifica = 1;
                                                    escolha = "0";
                                                }
                                                else{
                                                    System.out.println("Escolha não válida.");
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Escolha não válida.");
                                            }
                                        }
                                    }
                                    else if(escolha.compareTo("2") == 0){
                                        int verifica = 0;
                                        while(verifica == 0) {
                                            System.out.println("\n === REMOVER UM ARTISTA ===\n(Selecione o artista que deseja remover)");
                                            String lista = rmiInterface.listar("artista", name);
                                            int length = Integer.parseInt(lista.split(";")[0].substring(7));
                                            for (int i = 1; i <= length; i++) {
                                                System.out.println(i + ") Artista: " + lista.split(";")[1].substring(6).split("/")[i-1]);
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
                                    }
                                    else if(escolha.compareTo("3") == 0){
                                        int verifica = 0;
                                        while(verifica == 0) {
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
                                    }
                                    else if(escolha.compareTo("4") != 0){
                                        escolha = "0";
                                        System.out.println("Escolha não valida.");
                                    }
                                }
                            }
                            else if(escolha.compareTo("4") != 0){
                                System.out.println("Escolha não valida.");
                                escolha = "0";
                            }
                        }
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
        } catch(Exception e){
            System.out.println("Exception in Client: "+e);
            e.printStackTrace();
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
                    System.out.println("\n=== LOGIN ===\n(Introduza '0' em todos os campos para recuar)");
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
                                System.out.println("\n=== Bem-vindo ao DropMusic ===");
                                menu(rmiInterface);
                                controlo = 1;
                            }
                        }
                        else{
                            controlo = 1;
                        }
                    }
                }
                else if(escolha.compareTo("2") == 0){
                    System.out.println("\n=== SIGN UP ===\n(Introduza '0' em todos os campos para recuar)");
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
                                privilegio = "Normal";
                                System.out.println(resposta);
                                System.out.println("\n=== Bem-vindo ao DropMusic ===\n");
                                menu(rmiInterface);
                                controlo = 1;
                            }
                        }
                        else{
                            controlo = 1;
                        }
                    }
                }else if(escolha.compareTo("3") == 0){
                    return;
                }else{
                    System.out.println("Escolha nao valida!");
                }
            }
        } catch(Exception e){
            System.out.println("Exception in Client: "+e);
            e.printStackTrace();
        }
    }

}
