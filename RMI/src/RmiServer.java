import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

/**
 * É o Rmi Server.
 *
 * @author Damião Santos
 */
public class RmiServer extends UnicastRemoteObject implements  RmiInterface {

    /**
     * @param IP_SERVER É o ip para o multicast.
     * @param IP_PORT É o port para o multicast.
     * @param PORTTCP É o port para a ligação TCP.
     */
    private static final long serialVersionUID = 1L;
    private String IP_SERVER = "224.3.2.1";
    private int PORT = 4321;
    private int PORTTCP = 1904;

    /**
     * Construtor do Rmi Server.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public RmiServer() throws  RemoteException{
        super();
    }

    /**
     * Serve para realizar a verificação do Login de um utilizador comunicando com o Multicast.
     * @param username Nome do utilizador
     * @param password Password do utilizador
     * @return Retorna se foi válido ou não o Login.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String verificaLogin(String username, String password) throws RemoteException{
        int contador = 0;
        for(int i = 0; i<username.length(); i++){
            if(username.charAt(i) == '|' && username.charAt(i) == ';' || username.charAt(i) == '/')
                contador++;
        }
        for(int i = 0; i<password.length(); i++){
            if(password.charAt(i) == '|' || password.charAt(i) == ';' || password.charAt(i) == '/')
                contador++;
        }
        if(contador != 0){
            return "Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.";
        }
        else {
            try {
                MulticastSocket socket = new MulticastSocket(PORT);
                String data = "type|login;username|" + username + ";password|" + password;
                byte[] buffer = data.getBytes();
                InetAddress group = InetAddress.getByName(IP_SERVER);
                socket.joinGroup(group);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                while(true) {
                    buffer = new byte[1024];
                    DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(msgPacket);
                    String msg = new String(buffer, 0, buffer.length);
                    if(msg.startsWith("type|status") && msg.contains("username|"+username+";")){
                        if(msg.split(";")[1].contains("off")) {
                            return msg.split(";")[3].substring(4);
                        }else{
                            return msg;
                        }
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
                return "Erro! CATCH";
            }
        }
    }

    /**
     * Serve para realizar o Sign Up de um utilizador comunicando com o Multicast.
     * @param username Nome do utilizador
     * @param password Password do utilizador
     * @return Retorna se foi válido ou não o Sign Up.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String verificaSignUp(String username, String password) throws RemoteException{
        System.out.println("Entrou");
        if(username.length()<3 || username.length()>16){
            return "Erro! O nome de utilizador deve ter entre 3 a 16 caracteres.";
        }else if(password.length()<3 || password.length()>16){
            return "Erro! A password deve ter entre 3 a 16 caracteres.";
        }else{
            int contador = 0;
            for(int i = 0; i<username.length(); i++){
                if(username.charAt(i) == '|' && username.charAt(i) == ';' || username.charAt(i) == '/')
                    contador++;
            }
            for(int i = 0; i<password.length(); i++){
                if(password.charAt(i) == '|' || password.charAt(i) == ';' || password.charAt(i) == '/')
                    contador++;
            }
            if(contador != 0){
                return "Erro! Os campos nao podem conter os caracteres ';' , '|' e '/'.";
            }
            else {
                try {
                    MulticastSocket socket = new MulticastSocket(PORT);
                    String data = "type|registo;username|" + username + ";password|" + password;
                    byte[] buffer = data.getBytes();
                    InetAddress group = InetAddress.getByName(IP_SERVER);
                    socket.joinGroup(group);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);

                    while(true) {
                        buffer = new byte[1024];
                        DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                        socket.receive(msgPacket);
                        String msg = new String(buffer, 0, buffer.length);
                        if (msg.startsWith("type|confirmacao") && msg.contains("username|" + username + ";")) {
                            return msg.split(";")[3].substring(4);
                        }
                    }
                } catch(IOException e){
                    e.printStackTrace();
                    return "Erro! CATCH";
                }
            }
        }
    }

    /**
     * Serve para um editor inserir uma nova música.
     * @param nome Nome da música
     * @param artista Nome do artista
     * @param album Nome do album
     * @param duracao Duração da música
     * @param username Nome do utilizador que está a fazer o pedido
     * @return Retorna se foi efetuado com sucesso.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String inserirMusica(String nome, String artista, String album, String duracao, String username) throws RemoteException{
        int contador = 0;
        for(int i = 0; i<nome.length(); i++){
            if(nome.charAt(i) == '|' || nome.charAt(i) == ';' || nome.charAt(i) == '/')
                contador++;
        }
        for(int i = 0; i<artista.length(); i++){
            if(artista.charAt(i) == '|' || artista.charAt(i) == ';' || artista.charAt(i) == '/')
                contador++;
        }
        for(int i = 0; i<album.length(); i++){
            if(album.charAt(i) == '|' || album.charAt(i) == ';' || album.charAt(i) == '/')
                contador++;
        }
        for(int i = 0; i<duracao.length(); i++){
            if(duracao.charAt(i) == '|' || duracao.charAt(i) == ';' || duracao.charAt(i) == '/')
                contador++;
        }
        if(contador != 0){
            return "Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.";
        }
        else{
            try {
                MulticastSocket socket = new MulticastSocket(PORT);
                String data = "type|gerir;operacao|inserir;categoria|musica;nome|"+nome+";artista|"+artista+";album|"+album+";duracao|"+duracao+";username|"+username;
                byte[] buffer = data.getBytes();
                InetAddress group = InetAddress.getByName(IP_SERVER);
                socket.joinGroup(group);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                while(true) {
                    buffer = new byte[1024];
                    DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(msgPacket);
                    String msg = new String(buffer, 0, buffer.length);
                    if (msg.startsWith("type|resposta") && msg.contains("username|" + username + ";")) {
                        return msg.split(";")[2].substring(4);
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
                return "Erro! CATCH";
            }
        }
    }

    /**
     * Serve para um editor inserir um novo artista.
     * @param artista Nome do artista
     * @param album Nome dos albuns
     * @param username Nome do utilizador que está a fazer o pedido
     * @return Retorna se foi efetuado com sucesso.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String inserirArtista(String artista, String album, String username) throws RemoteException{
        int contador = 0;
        for(int i = 0; i<artista.length(); i++){
            if(artista.charAt(i) == '|' || artista.charAt(i) == ';' || artista.charAt(i) == '/')
                contador++;
        }
        if(contador != 0){
            return "Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.";
        }
        else{
            try {
                MulticastSocket socket = new MulticastSocket(PORT);
                String data = "type|gerir;operacao|inserir;categoria|artista;nome|"+artista+";albuns|"+album+";username|"+username;
                byte[] buffer = data.getBytes();
                InetAddress group = InetAddress.getByName(IP_SERVER);
                socket.joinGroup(group);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                while(true) {
                    buffer = new byte[1024];
                    DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(msgPacket);
                    String msg = new String(buffer, 0, buffer.length);
                    if (msg.startsWith("type|resposta") && msg.contains("username|" + username + ";")) {
                        return msg.split(";")[2].substring(4);
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
                return "Erro! CATCH";
            }
        }
    }

    /**
     * Serve para um editor inserir um novo album.
     * @param album Nome do album
     * @param artista Nome do artista
     * @param musicas Nome das músicas
     * @param username Nome do utilizador que está a fazer o pedido
     * @return Retorna se foi efetuado com sucesso.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String inserirAlbum(String album, String artista, String musicas, String username) throws RemoteException{
        int contador = 0;
        for(int i = 0; i<artista.length(); i++){
            if(artista.charAt(i) == '|' || artista.charAt(i) == ';' || artista.charAt(i) == '/')
                contador++;
        }
        for(int i = 0; i<album.length(); i++){
            if(album.charAt(i) == '|' || album.charAt(i) == ';' || album.charAt(i) == '/')
                contador++;
        }
        if(contador != 0){
            return "Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.";
        }
        else{
            try {
                MulticastSocket socket = new MulticastSocket(PORT);
                String data = "type|gerir;operacao|inserir;categoria|album;nome|"+album+";artista|"+artista+";musicas|"+musicas+";username|"+username;
                byte[] buffer = data.getBytes();
                InetAddress group = InetAddress.getByName(IP_SERVER);
                socket.joinGroup(group);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                while(true) {
                    buffer = new byte[1024];
                    DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(msgPacket);
                    String msg = new String(buffer, 0, buffer.length);
                    if (msg.startsWith("type|resposta") && msg.contains("username|" + username + ";")) {
                        return msg.split(";")[2].substring(4);
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
                return "Erro! CATCH";
            }
        }
    }

    /**
     * Serve para pedir a listagem de uma determinada categoria.
     * @param categoria Pode ser música, artista ou album
     * @param username Nome do utilizador que está a fazer o pedido
     * @return Retorna a lista.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String listar(String categoria, String username) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            String data = "type|gerir;operacao|apresentar;categoria|"+categoria+";username|"+username;
            byte[] buffer = data.getBytes();
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            while(true) {
                buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|lista") && msg.contains("username|" + username + ";")) {
                    return msg.substring(11);
                }
            }
        } catch(IOException e){
            e.printStackTrace();
            return "Erro! CATCH";
        }
    }

    /**
     * Serve para um editor alterar um item de uma determinada categoria.
     * @param categoria Pode ser música, artista ou album.
     * @param index A posição na lista do item que quere alterar.
     * @param campo O campo que deseja mudar.
     * @param novo O novo nome para o campo.
     * @param username Nome do utilizador que está a fazer o pedido.
     * @return Retorna se foi efetuado com sucesso.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String alterar(String categoria, String index, String campo, String novo, String username) throws RemoteException{
        int contador = 0;
        if(categoria.compareTo("musica") == 0) {
            for (int i = 0; i < novo.length(); i++) {
                if (novo.charAt(i) == '|' || novo.charAt(i) == ';' || novo.charAt(i) == '/')
                    contador++;
            }
        }
        if(contador != 0){
            return "Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.";
        }
        else{
            try {
                MulticastSocket socket = new MulticastSocket(PORT);
                String data = "type|gerir;operacao|alterar;categoria|"+categoria+";index|"+index+";username|"+username+";campo|"+campo+";info|"+novo;
                byte[] buffer = data.getBytes();
                InetAddress group = InetAddress.getByName(IP_SERVER);
                socket.joinGroup(group);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                while(true) {
                    buffer = new byte[1024];
                    DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(msgPacket);
                    String msg = new String(buffer, 0, buffer.length);
                    if (msg.startsWith("type|resposta") && msg.contains("username|" + username + ";")) {
                        return msg.split(";")[2].substring(4);
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
                return "Erro! CATCH";
            }
        }
    }

    /**
     * Serve para um editor remover um item de uma determinada categoria.
     * @param categoria Pode ser música, artista, ou album.
     * @param index A posição na lista do item que quere remover.
     * @param username Nome do utilizador que está  a fazer o pedido.
     * @return Retorna se foi efetuado com sucesso.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String remover(String categoria, String index, String username) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            String data = "type|gerir;operacao|remover;categoria|"+categoria+";index|"+index+";username|"+username;
            byte[] buffer = data.getBytes();
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            while(true) {
                buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|resposta") && msg.contains("username|" + username + ";")) {
                    return msg.split(";")[2].substring(4);
                }
            }
        } catch(IOException e){
            e.printStackTrace();
            return "Erro! CATCH";
        }
    }

    /**
     * Serve para pesquisar um item de uma determinada categoria.
     * @param nome Nome do item
     * @param categoria Pode ser música, artista ou album.
     * @param username Nome do utilizador que está a fazer o pedido.
     * @return Retorna a lista dos items correspondidos.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String pedir_pesquisa(String nome, String categoria, String username) throws RemoteException{
        int contador = 0;
        for (int i = 0; i < nome.length(); i++) {
            if (nome.charAt(i) == '|' || nome.charAt(i) == ';' || nome.charAt(i) == '/')
                contador++;
        }
        if(contador != 0){
            return "Erro! Os campos nao podem conter os caracteres ';' , '|' ou '/'.";
        }
        else {
            try {
                MulticastSocket socket = new MulticastSocket(PORT);
                String data = "type|pesquisa;categoria|" + categoria + ";nome|" + nome + ";username|" + username;
                byte[] buffer = data.getBytes();
                InetAddress group = InetAddress.getByName(IP_SERVER);
                socket.joinGroup(group);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                while (true) {
                    buffer = new byte[1024];
                    DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(msgPacket);
                    String msg = new String(buffer, 0, buffer.length);
                    if (msg.startsWith("type|lista") && msg.contains("username|" + username + ";")) {
                        if(msg.charAt(18) == '0'){
                            return "Erro! Nao existe qualquer "+categoria+" com esse nome.";
                        }
                        else {
                            return msg.substring(11);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Erro! CATCH";
            }
        }
    }

    /**
     * Serve para pedir os detalhes de um item de uma determinada categoira.
     * @param nome Nome do item
     * @param nome2 Caso seja musica ou album é preciso o nome do artista.
     * @param username Nome do utilizador que está a fazer o pedido.
     * @param categoria Pode ser música, artista ou album.
     * @return Retorna os detalhes do item.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String pedir_detalhes(String nome, String nome2, String username, String categoria) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            String data = "type|consulta;categoria|" + categoria + ";nome|" + nome + ";artista|"+nome2+";username|" + username;
            byte[] buffer = data.getBytes();
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            while (true) {
                buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|info") && msg.contains("username|" + username + ";")) {
                    if(categoria != "album")
                        return msg.split(";")[2].substring(9);
                    else
                        return msg.split(";")[2].substring(9) + msg.split(";")[3];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro! CATCH";
        }
    }

    /**
     * Serve para escrever uma crítica num album.
     * @param nome Nome do album
     * @param artista Nome do artista
     * @param critica A critica
     * @param nota Nota ao album
     * @param username Nome do utilizador que está a fazer a critica.
     * @return Retorna se foi com sucesso ou não.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String enviarCritica(String nome, String artista, String critica, String nota, String username) throws RemoteException{
        int contador = 0;
        for (int i = 0; i < critica.length(); i++) {
            if (nome.charAt(i) == '|' || nome.charAt(i) == ';' || nome.charAt(i) == '/')
                contador++;
        }
        if(contador != 0){
            return "Erro! A critica nao pode conter os caracteres ';' , '|' ou '/'.";
        }
        if(nota.compareTo("1") != 0 && nota.compareTo("2") != 0 && nota.compareTo("3") != 0 && nota.compareTo("4") != 0 && nota.compareTo("5") != 0){
            return "Erro! A nota e um numero inteiro de 1 a 5.";
        }
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            String data = "type|critica;categoria|album;nome|" + nome + ";artista|"+artista+";msg|"+critica+";nota|"+nota+";username|"+username;
            byte[] buffer = data.getBytes();
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            while (true) {
                buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|resposta") && msg.contains("username|" + username + ";")) {
                    return msg.split(";")[2].substring(4);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro! CATCH";
        }
    }

    /**
     * Serve para pedir a lista de utilizadores que sao leitores para promover.
     * @param username Nome do utilizador que está a fazer o pedido.
     * @return Retorna a lista dos utilizadores.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String pedirUtilizadores(String username) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            String data = "type|utilizadores;username|"+username;
            byte[] buffer = data.getBytes();
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            while (true) {
                buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|lista_utili") && msg.contains("username|" + username + ";")) {
                    return msg;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro! CATCH";
        }
    }

    /**
     * Serve para promover um leitor.
     * @param change O nome do utilizador para promover.
     * @param username O nome do utilizador que está a realizar o pedido.
     * @return Retorna se foi com sucesso ou não.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String promover(String change, String username) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            String data = "type|promover;utilizador|"+change+";username|"+username;
            byte[] buffer = data.getBytes();
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            while (true) {
                buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|notificacao") && msg.contains("username2|" + username + ";")) {
                    return "Operacao com Sucesso!";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro! CATCH";
        }
    }

    /**
     * Serve para o utilizador estar á espera de notificações.
     * @param username O nome do utilizador que está à espera.
     * @return Retorna a notificação.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String Notificacoes(String username) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|notificacao") && msg.contains("username|" + username + ";")) {
                    String data = "type|noticonfirma;username|"+username;
                    buffer = data.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                    if(msg.split(";").length == 3){
                        return msg.split(";")[2].substring(4);
                    }
                    else{
                        return msg.split(";")[3].substring(4);
                    }
                }
                if(msg.startsWith("type|killthread") && msg.contains("username|" + username + ";")){
                    return "Fim";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro! CATCH";
        }
    }

    /**
     * Serve para terminar a thread que está à espera de notificações.
     * @param username O nome do utilizador que está à espera.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public void killThread(String username) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            String data = "type|killthread;username|"+username+";";
            byte[] buffer = new byte[1024];
            buffer = data.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serve para estabelecer a ligação TCP para o upload/download de músicas
     * @param file Música a enviar se for upload.
     * @param action Se é upload ou download
     * @param username Nome do utilizador que quere realizar a tarefa.
     * @param index A música que deseja dar upload.
     * @param nome O nome da música que deseja dar download.
     * @param artista O nome do artista da música que deseja dar download.
     * @return Retorna se foi com sucesso ou não.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String tcp(File file, String action, String username, int index, String nome, String artista) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            String data = "type|pedirIP;acao|"+action+";index|"+index+";username|"+username;
            byte[] buffer = data.getBytes();
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            while (true) {
                buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|confIP") && msg.contains("username|" + username + ";")) {
                    return msg;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "DEU ERRO!!";
        }
    }

    /**
     * Serve para pedir a biblioteca do utilizador.
     * @param username Nome do utilizador que está a fazer o pedido.
     * @return Retorna a biblioteca.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String pedirBiblioteca(String username) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            String data = "type|playlist;username|"+username;
            byte[] buffer = data.getBytes();
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            while (true) {
                buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|listplay") && msg.contains("username|" + username + ";")) {
                    return msg.substring(14);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro! CATCH";
        }
    }

    /**
     * Serve para dar permissão a um determinado utilizador para que possa utilizar uma música da sua biblioteca.
     * @param user O nome do utilizador a quem deseja dar permissão.
     * @param username O nome do utilizador que está a dar a permissão.
     * @param musica A música que quere dar permissão.
     * @param artista O artista da música que quere dar permissão.
     * @return Retorna se foi com sucesso ou não.
     * @throws RemoteException Devido a ser Remota a ligação.
     */
    public String permissao(String user, String username, String musica, String artista) throws RemoteException{
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            String data = "type|autorizacao;musica|"+musica+";artista|"+artista+";utilizador|"+user+";username|"+username;
            byte[] buffer = data.getBytes();
            InetAddress group = InetAddress.getByName(IP_SERVER);
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            while (true) {
                buffer = new byte[1024];
                DatagramPacket msgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(msgPacket);
                String msg = new String(buffer, 0, buffer.length);
                if (msg.startsWith("type|auto_res") && msg.contains("username|" + username + ";")) {
                    return msg.split(";")[2].substring(4);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro! CATCH";
        }
    }

    /**
     * A main que inicializa o RMI Server e põe o Remoto.
     * @param args
     */
    public static void main(String args[]) {
        int controlo = 0;
        while (controlo == 0){
            try {
                RmiServer server = new RmiServer();
                Registry r = LocateRegistry.createRegistry(7000);
                r.rebind("rmiSERVER", server);
                controlo = 1;
                System.out.println("=== Rmi Server Arrancou ===");
            } catch (RemoteException re) {
                controlo = 0;
            }
        }
    }

}
