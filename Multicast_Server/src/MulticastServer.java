import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 4321;


    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();

    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;

        System.out.println(this.getName() + " running...");
        try {

            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            HashMap<String, String> map = new HashMap<>();

            while (true) {

                //receber mensagem
                byte [] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());

                map = string_to_hash(message);

                if((((String)map.get("type")).compareTo("confirmacao")!=0) && (((String)map.get("type")).compareTo("status")!=0) && (((String)map.get("type")).compareTo("resposta")!=0) && (((String)map.get("type")).compareTo("lista")!=0) && (((String)map.get("type")).compareTo("info")!=0) && (((String)map.get("type")).compareTo("lista_utili")!=0) && (((String)map.get("type")).compareTo("notificacao")!=0) && (((String)map.get("type")).compareTo("killthread")!=0) && (((String)map.get("type")).compareTo("confIP")!=0) && (((String)map.get("type")).compareTo("listplay")!=0) && (((String)map.get("type")).compareTo("auto_res")!=0)) {
                    System.out.println("type: " + map.get("type"));

                    String resposta;
                    resposta = executa_info(map);

                    //enviar mensagem
                    buffer = resposta.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    //método que converte string em HashMap
    public HashMap string_to_hash(String string){

        HashMap<String, String> map = new HashMap<>();

        int contador=0;
        for(int i=0 ; i<string.length() ; i++)
            if(string.charAt(i) == '|' )
                contador++;

        String [] aux = new String[contador];
        String [] msg = new String[contador*2];

        aux = string.split(";");
        int controlador=0, controlador2=0;

        for(int i=0 ; i<aux.length ; i++) {
            for(int j=0; j<aux[i].length() ; j++)
                if(aux[i].charAt(j)=='|')
                    controlador = j;

            msg[controlador2] = aux[i].substring(0, controlador);
            msg[controlador2+1] = aux[i].substring(controlador+1, aux[i].length());

            controlador2+=2;
        }

        controlador2=0;
        for(int i=0 ; i<contador ; i++) {
            map.put(msg[controlador2], msg[controlador2 + 1]);
            controlador2 += 2;
        }

        return map;
    }

    //método que filtra as operações a realizar
    public String executa_info(HashMap map){
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        String mensagem = "";

        switch((String)map.get("type")){

            case "login":
                mensagem = login(map);
                break;

            case "registo":
                mensagem = registo(map);
                break;

            case "gerir":
                if(((String)map.get("operacao")).compareTo("inserir")==0)
                    mensagem = inserir_info(map);
                else if(((String)map.get("operacao")).compareTo("apresentar")==0)
                    mensagem = apresenta_info(map, null);
                else if(((String)map.get("operacao")).compareTo("alterar")==0)
                    mensagem = alterar_info(map);
                else if(((String)map.get("operacao")).compareTo("remover")==0)
                    mensagem = remover_info(map);
                break;

            case "pesquisa":
                lista = preenche_lista(map);
                mensagem = apresenta_info(map, lista);
                break;

            case "consulta":
                lista = preenche_lista(map);
                mensagem = consulta_info(map, lista);
                break;

            case "critica":
                mensagem = adiciona_critica(map);
                break;

            case "utilizadores":
                mensagem = lista_utilizadores(map, "leitor");
                break;

            case "promover":
                mensagem = promover_user(map);
                break;

            case "noticonfirma":
                notificacao_confirmada(map);
                break;

            case "pedirIP":
                String ip;
                try {
                    InetAddress IP = InetAddress.getLocalHost();
                    ip = IP.getHostAddress();
                    mensagem = "type|confIP;IP|" + ip + ";|username|" + map.get("username") + ";";

                    if(((String)map.get("acao")).compareTo("upload")==0){
                        verificacao_upload(map);
                    }
                    else{
                        mensagem += verificacao_download(map);
                    }

                    TCPThread thread = new TCPThread(map);
                    thread.start();
                }catch (UnknownHostException e){
                    System.out.println("Ocorreu a exceção " + e);
                }
                break;

            case "playlist":
                mensagem = enviar_playlist(map);
                break;

            case "autorizacao":
                mensagem = partilha_musica(map);
                break;
        }

        return mensagem;
    }

    //método que realiza login de um utilizador
    public String login(HashMap map){
        String string = "";
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        int confirmacao;

        confirmacao = verifica_dados(map, lista);

        if(confirmacao==1) {
            string = get_notificacoes(map);
            if(string.compareTo("")==0)
                return "type|status;logged|on;msg|Bem-vindo!;username|" + map.get("username") + ";privilegio|" + get_privilegio(map);
            else
                return "type|status;logged|on;msg|Bem-vindo!\n" + string + ";username|" + map.get("username") + ";privilegio|" + get_privilegio(map);
        }
        else if(confirmacao==-1)
            return "type|status;logged|off;username|" + map.get("username") + ";msg|Erro! Password incorreta";
        else
            return "type|status;logged|off;username|" + map.get("username") + ";msg|Erro! Utilizador não existe";
    }

    //método que realiza registo de um utilizador
    public String registo(HashMap map){
        String mensagem;
        try{
            File f = new File("Registos.txt");
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);

            //verificar se o utilizador já existe
            mensagem = login(map);
            if(mensagem.compareTo("type|status;logged|off;username|" + map.get("username") + ";msg|Erro! Utilizador não existe")!=0)
                return "type|confirmacao;resposta|nao;username|" + map.get("username") + ";msg|Erro! O username inserido já existe.";

            if(f.length()==0)
                pw.println("username|" + map.get("username") + ";password|" + map.get("password") + ";privilegio|editor");
            else
                pw.println("username|" + map.get("username") + ";password|" + map.get("password") + ";privilegio|leitor");

            pw.close();
            return "type|confirmacao;resposta|sim;username|" + map.get("username") + ";msg|Registo efetuado com sucesso!";

        }catch(IOException e ){
            System.out.println("Ocorreu a exceção "+e);
        }

        return "";

    }

    //método que verifica se o nome de utilziador existe e a password coicide
    public int verifica_dados(HashMap map, ArrayList<HashMap<String, String>> lista){

        String user = (String) map.get("username");
        String pass = (String) map.get("password");

        for(int i=0; i<lista.size(); i++){
            if(user.compareTo((String)lista.get(i).get("username"))==0){
                if(pass.compareTo((String)lista.get(i).get("password"))==0)
                    return 1;
                else
                    return -1;
            }
        }

        return 0;
    }

    //método que insere informação num determinado campo
    public String inserir_info(HashMap map){

        // verificações -> musica e artista // album e artista
        // musica -> nome, artista, album, duração
        // album -> nome, artista, musicas, criticas -> nota
        // artista -> nome, albuns
        int controlo=-1;
        try{

            controlo = verifica_info(map);
            if(controlo == 0)
                return "type|resposta;username|" + map.get("username") + ";msg|A informação que inseriu já existe";

            File f = new File( map.get("categoria") + ".txt");
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);

            if(((String)map.get("categoria")).compareTo("musica")==0)
                pw.println("nome|" + map.get("nome") + ";artista|" + map.get("artista") + ";album|" + map.get("album") + ";duracao|" + map.get("duracao") + ";editor|" + map.get("username"));
            else if(((String)map.get("categoria")).compareTo("album")==0)
                pw.println("nome|" + map.get("nome") + ";artista|" + map.get("artista") + ";musicas|" + map.get("musicas") + ";editor|" + map.get("username"));
            else if(((String)map.get("categoria")).compareTo("artista")==0)
                pw.println("nome|" + map.get("nome") + ";albuns|" + map.get("albuns") + ";editor|" + map.get("username"));
            pw.close();
        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }

        return "type|resposta;username|" + map.get("username") + ";msg|Informação inserida com sucesso";
    }

    //método que verifica se a informação já foi inserida
    public int verifica_info(HashMap map){

        String categoria = (String)map.get("categoria");
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);

        for(int i=0; i<lista.size(); i++){
            if(categoria.compareTo("artista")!=0) {
                if (((String) map.get("nome")).compareTo((String) lista.get(i).get("nome")) == 0 && ((String) map.get("artista")).compareTo((String) lista.get(i).get("artista")) == 0)
                    return 0;
            }
            else {
                if (((String) map.get("nome")).compareTo((String) lista.get(i).get("nome")) == 0)
                    return 0;
            }
        }

        return 1;
    }

    //método que cria uma lista com os dados da pesquisa do cliente
    public ArrayList<HashMap<String, String>> preenche_lista(HashMap map){
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        ArrayList<HashMap<String, String>> aux_lista = new ArrayList<>();
        String nome = (String)map.get("nome");

        for(int i=0; i<lista.size(); i++){
            if(((String)lista.get(i).get("nome")).contains(nome))
                aux_lista.add(lista.get(i));
        }

        return aux_lista;
    }

    //método que devolve ao cliente a informação que deseja visualizar
    public String apresenta_info(HashMap map, ArrayList<HashMap<String,String>> lista){
        StringBuilder string = new StringBuilder();
        String mensagem;

        if(lista==null)
            lista = le_ficheiro(map);

        string.append("type|lista;length|" + lista.size() + ";items|");

        for(int i=0; i<lista.size(); i++){
            if(i==0) {
                if(((String)map.get("categoria")).compareTo("artista")==0)
                    string.append((String)lista.get(i).get("nome"));
                else
                    string.append((String)lista.get(i).get("nome") + "/" + (String)lista.get(i).get("artista"));

            }
            else {
                if(((String)map.get("categoria")).compareTo("artista")==0)
                    string.append("/" + (String)lista.get(i).get("nome"));
                else
                    string.append("/" + (String)lista.get(i).get("nome") + "/" + (String)lista.get(i).get("artista"));
            }
        }
        string.append(";username|" + map.get("username") + ";");
        mensagem = string.toString();
        return mensagem;
    }

    //método que altera informação de um determinado campo
    public String alterar_info(HashMap map){
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        int index = Integer.parseInt((String)map.get("index"));
        String categoria = (String)map.get("categoria");
        int controlo = 0;

        lista.get(index).put((String)map.get("campo"), (String)map.get("info"));
        controlo = verifica_editores(map);

        if(controlo == 1) {
            lista.get(index).put("editor", lista.get(index).get("editor") + "/" + (String) map.get("username"));
        }

        gera_notificacao(map, "editor");
        escreve_ficheiro(lista, categoria);

        return "type|resposta;username|" + map.get("username") + ";msg|Informação alterada com sucesso";
    }

    //método que apaga uma determinada informação
    public String remover_info(HashMap map){
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        int index = Integer.parseInt((String)map.get("index"));
        String categoria = (String)map.get("categoria");

        lista.remove(index);

        escreve_ficheiro(lista, categoria);
        return "type|resposta;username|" + map.get("username") + ";msg|Informação removida com sucesso!";
    }

    //método que retorna a informação detalhada
    public String consulta_info(HashMap map, ArrayList<HashMap<String, String>> lista){

        int index = -1;

        String artista = (String)map.get("artista");
        String categoria = (String)map.get("categoria");
        StringBuilder string = new StringBuilder();
        String nome = (String)map.get("nome");

        if(categoria.compareTo("artista")==0)
            for(int i=0; i<lista.size(); i++) {
                if (((String)lista.get(i).get("nome")).compareTo(nome) == 0)
                    index = i;
            }
        else{
            for(int i=0; i<lista.size(); i++) {
                if (((String)lista.get(i).get("artista")).compareTo(artista) == 0 && ((String)lista.get(i).get("nome")).compareTo(nome) == 0)
                    index = i;
            }
        }

        string.append("type|info;categoria|" + map.get("categoria") + ";detalhes|");
        if(categoria.compareTo("musica")==0) {
            string.append((String)lista.get(index).get("nome"));
            string.append("/" + (String)lista.get(index).get("artista"));
            string.append("/" + (String)lista.get(index).get("album"));
            string.append("/" + (String)lista.get(index).get("duracao"));
        }
        else if(categoria.compareTo("album")==0) {
            string.append((String)lista.get(index).get("nome"));
            string.append("/" + (String) lista.get(index).get("artista"));
            String [] contador = ((String)lista.get(index).get("musicas")).split("/");
            string.append("/" + contador.length);
            string.append("/" + (String) lista.get(index).get("musicas"));
            if (((String)lista.get(index).get("critica")) == null) {
                string.append(";username|" + map.get("username") + ";msg|Ainda não existem críticas.");
                return string.toString();
            }
            else{
                string.append("/" + (String)lista.get(index).get("critica"));
                string.append("/" + (String)lista.get(index).get("nota"));
            }
        }
        else if(categoria.compareTo("artista")==0){
            for(int i=0; i<lista.size(); i++)
                if(((String)lista.get(i).get("nome")).compareTo((String)map.get("nome"))==0){
                    string.append((String)lista.get(i).get("nome"));
                    string.append("/" + (String)lista.get(i).get("albuns"));
                }

        }
        string.append(";username|" + map.get("username") + ";");

        return string.toString();
    }

    //método para adicionar critica a um album
    public String adiciona_critica(HashMap map){

        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        int index = -1;
        String categoria = (String)map.get("categoria");
        StringBuilder aux = new StringBuilder();

        for(int i=0; i<lista.size(); i++){
            if(((String)lista.get(i).get("nome")).compareTo((String)map.get("nome"))==0 && ((String)lista.get(i).get("artista")).compareTo((String)map.get("artista"))==0 )
                index = i;
        }

        System.out.println("teste " + lista.get(index).get("critica"));
        if(((String)lista.get(index).get("critica")) == null) {
            lista.get(index).put("critica", (String)map.get("msg"));
            lista.get(index).put("nota", (String)map.get("nota"));
        }
        else{
            String [] aux2 = lista.get(index).get("critica").split("/");
            System.out.println("teste " + aux2.length);
            float nota = Float.parseFloat((String)lista.get(index).get("nota"));

            nota = ((nota*aux2.length) + Integer.parseInt((String)map.get("nota"))) / (aux2.length + 1 );

            aux.append((String)lista.get(index).get("critica"));
            aux.append("/" + (String)map.get("msg"));
            lista.get(index).put("critica" , aux.toString());
            lista.get(index).put("nota" , Float.toString(nota));
        }

        escreve_ficheiro(lista, categoria);

        return "type|resposta;username|" + map.get("username") + ";msg|Critica adicionada com sucesso";
    }

    //método que retorna uma determinada lista de utilizadores (lista de editores ou leitores)
    public String lista_utilizadores(HashMap map, String flag){

        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        StringBuilder string = new StringBuilder();
        int contador=0;

        for(int i=0; i<lista.size(); i++)
            if (((String)lista.get(i).get("privilegio")).compareTo(flag) == 0)
                contador++;

        string.append("type|lista_utili;length|" + contador + ";items|");

        int controlador = 0;
        for(int i=0; i<lista.size(); i++) {
            if (((String)lista.get(i).get("privilegio")).compareTo(flag) == 0) {
                if (controlador == 0) {
                    string.append((String) lista.get(i).get("username"));
                    controlador++;
                } else
                    string.append("/" + (String) lista.get(i).get("username"));
            }
        }

        string.append(";username|" + (String)map.get("username") + ";");
        return string.toString();
    }

    //método que promove um leitor a editor
    public String promover_user(HashMap map){

        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        String utilizador = (String)map.get("utilizador");

        for(int i=0; i<lista.size(); i++){
            if(utilizador.compareTo((String)lista.get(i).get("username"))==0){
                lista.get(i).put("privilegio", "editor");
                escreve_ficheiro(lista, "Registos");
                gera_notificacao(map, "promovido");
                return "type|notificacao;username|" + utilizador + ";username2|" + map.get("username") + ";msg|Foi promovido a editor!";
            }
        }

        return "";
    }

    //método que gere notificações
    public void gera_notificacao(HashMap map, String flag){
        String utilizador = (String)map.get("username");
        String categoria = "notificacoes";
        HashMap<String, String> new_map = new HashMap<>();
        StringBuilder aux = new StringBuilder();
        int controlador=-1;
        new_map.put("type", "notificacao");

        if(flag.compareTo("promovido")==0) {
            new_map.put("username", (String)map.get("utilizador"));
            new_map.put("msg", "Foi promovido a editor!");
            controlador++;
        }
        else if(flag.compareTo("editor")==0){
            ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
            String [] aux_lista = ((String)lista.get(Integer.parseInt((String)map.get("index"))).get("editor")).split("/");

            for(int i=0; i<aux_lista.length ; i++){
                if(controlador==-1 && utilizador.compareTo(aux_lista[i])!=0){
                    aux.append(aux_lista[i]);
                    controlador++;
                }
                else if(controlador>=0 && utilizador.compareTo(aux_lista[i])!=0)
                    aux.append("/"+ aux_lista[i]);
            }

            new_map.put("username", aux.toString());


            if(((String)map.get("categoria")).compareTo("artista")==0)
                new_map.put("msg" , "O editor " + (String)map.get("username") + ", alterou a informação do seguinte item: " + (String)lista.get(Integer.parseInt((String)map.get("index"))).get("nome") + "(" + map.get("categoria") + ")");
            else
                new_map.put("msg" , "O editor " + (String)map.get("username") + ", alterou a informação do seguinte item: " + (String)lista.get(Integer.parseInt((String)map.get("index"))).get("nome") + ", " + (String)lista.get(Integer.parseInt((String)map.get("index"))).get("artista") + "(" + map.get("categoria") + ")");
        }

        if(controlador != -1)
            regista_notificao(new_map, categoria);
    }

    //método que guarda as notificações num ficheiro
    public void regista_notificao(HashMap<String, String> map, String categoria){

        try{
            File f = new File(categoria + ".txt");
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println("type|" + map.get("type") + ";username|" + map.get("username") + ";msg|" + map.get("msg"));

            pw.close();
        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }
    }

    //método que confirma que a notificação chegou ao utilizador
    public void notificacao_confirmada(HashMap<String, String> map){
        map.put("categoria" , "notificacoes");
        ArrayList<HashMap<String, String>> lista = le_ficheiro_notificacoes(map);
        String username = (String)map.get("username");
        String [] aux;
        StringBuilder string = new StringBuilder();
        int save, controlador;

        for(int i=0; i<lista.size(); i++){
            save=-1;
            controlador=0;
            string.setLength(0);
            aux = ((String)lista.get(i).get("username")).split("/");
            for(int j=0; j<aux.length; j++)
                if(username.compareTo(aux[j])==0)
                    save = j;

            if(save!=-1){
                for(int z=0; z<aux.length; z++) {
                    if (z != save) {
                        if (controlador == 0) {
                            string.append(aux[z]);
                            controlador++;
                        } else
                            string.append("/" + aux[z]);
                    }
                }

                if(controlador==0)
                    lista.remove(i);
                else
                    lista.get(i).put("username" , string.toString());

            }

        }

        escreve_ficheiro(lista, "notificacoes");
    }

    //método que verifica se o utilizador que realizou login possui notificações
    public String get_notificacoes(HashMap<String, String> map){
        ArrayList<HashMap<String, String>> lista = le_ficheiro_notificacoes(map);
        StringBuilder msg = new StringBuilder();
        String [] aux;
        int controlo = 0;
        for(int i=0; i<lista.size(); i++){
            aux = ((String)lista.get(i).get("username")).split("/");
            for(int j=0; j<aux.length; j++){
                if (aux[j].compareTo((String)map.get("username")) == 0){
                    msg.append(lista.get(i).get("msg")+"\n");
                    notificacao_confirmada(map);
                    controlo++;
                }
            }
        }
        if(controlo == 0)
            return "";
        else
            return msg.toString();
    }

    //método que retorna num ArrayList a informação lida do ficheiro
    public ArrayList<HashMap<String, String>> le_ficheiro (HashMap map){
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        String categoria;
        String s;

        if(((String)map.get("type")).compareTo("login")==0 || ((String) map.get("type")).compareTo("utilizadores")==0 || ((String) map.get("type")).compareTo("promover")==0 || ((String) map.get("type")).compareTo("autorizacao")==0)
            categoria = "Registos";
        else
            categoria = (String)map.get("categoria");
        try{
            File f = new File(categoria + ".txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine())!=null){
                HashMap<String, String> aux = string_to_hash(s);
                lista.add(aux);
            }

            br.close();

        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }

        return lista;
    }

    //método que lê ficheiro de notificações
    public ArrayList<HashMap<String, String>> le_ficheiro_notificacoes(HashMap map){
        String s;
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();

        try{
            File f = new File("notificacoes.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine())!=null){
                HashMap<String, String> aux = string_to_hash(s);
                lista.add(aux);
            }

            br.close();

        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }

        return lista;
    }

    //método que escreve informação num ficheiro
    public void escreve_ficheiro(ArrayList<HashMap<String, String>> lista, String categoria){

        try{
            File f = new File(categoria + ".txt");
            FileWriter fw = new FileWriter(f);
            PrintWriter pw = new PrintWriter(fw);

            for(int i=0; i<lista.size(); i++) {
                if(categoria.compareTo("musica")==0)
                    pw.println("nome|" + lista.get(i).get("nome") + ";artista|" + lista.get(i).get("artista") + ";album|" + lista.get(i).get("album") + ";duracao|" + lista.get(i).get("duracao") + ";editor|" + lista.get(i).get("editor"));
                else if(categoria.compareTo("album")==0) {
                    if(((String)lista.get(i).get("critica")) != null)
                        pw.println("nome|" + lista.get(i).get("nome") + ";artista|" + lista.get(i).get("artista") + ";musicas|" + lista.get(i).get("musicas") + ";critica|" + lista.get(i).get("critica") + ";nota|" + lista.get(i).get("nota") + ";editor|" + lista.get(i).get("username"));
                    else
                        pw.println("nome|" + lista.get(i).get("nome") + ";artista|" + lista.get(i).get("artista") + ";musicas|" + lista.get(i).get("musicas") + ";editor|" + lista.get(i).get("username"));
                }
                else if(categoria.compareTo("artista")==0)
                    pw.println("nome|" + lista.get(i).get("nome") + ";albuns|" + lista.get(i).get("albuns") + ";editor|" + lista.get(i).get("username"));
                else if(categoria.compareTo("Registos")==0)
                    pw.println("username|" + lista.get(i).get("username") + ";password|" + lista.get(i).get("password") + ";privilegio|" + lista.get(i).get("privilegio"));
                else if(categoria.compareTo("notificacoes")==0)
                    pw.println("type|notificacao;username|" + lista.get(i).get("username") + ";msg|" + lista.get(i).get("msg"));
                else if(categoria.compareTo("dados")==0) {
                    if((String)lista.get(i).get("permissoes") != null) {
                        System.out.println("entrou");
                        pw.println("nome|" + lista.get(i).get("nome") + ";proprietario|" + lista.get(i).get("proprietario") + ";permissoes|" + lista.get(i).get("permissoes"));
                    }
                    else
                        pw.println("nome|" + lista.get(i).get("nome") + ";proprietario|" + lista.get(i).get("proprietario"));
                }

            }

            pw.close();
        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }
    }

    //método que retorna o privilegio de um dado username
    public String get_privilegio(HashMap map){
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        String utilizador = (String)map.get("username");
        String privilegio = "";

        for(int i=0; i<lista.size(); i++){
            if(utilizador.compareTo((String)lista.get(i).get("username"))==0)
                privilegio = (String)lista.get(i).get("privilegio");
        }

        return privilegio;
    }

    public void verificacao_upload(HashMap map){

        map.put("categoria", "dados");
        HashMap<String, String> aux = new HashMap<>();
        aux.put("categoria", "musica");
        aux.put("type", "pedirIP");
        ArrayList<HashMap<String, String>> aux_lista = le_ficheiro(aux);
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        String utilizador = (String)map.get("username");
        String musica = (String)aux_lista.get(Integer.parseInt((String)map.get("index"))).get("nome");
        String artista = (String)aux_lista.get(Integer.parseInt((String)map.get("index"))).get("artista");

        musica += "#" + artista;

        int controlo = 0; //controla se já se encontra como proprietario
        int controlo2 = 0; //controla se a musica ja se encontra nos dados

        for(int i=0; i<lista.size(); i++){
            if(musica.compareTo((String)lista.get(i).get("nome"))==0){
                controlo2++;
                String [] prop = ((String)lista.get(i).get("proprietario")).split("/");
                for(int j=0; j<prop.length; j++){
                    if(prop[j].compareTo(utilizador)==0)
                        controlo=1;
                }
                if(controlo==0)
                    lista.get(i).put("proprietario", lista.get(i).get("proprietario")+ "/" + utilizador);
            }
        }

        if(controlo2==0){
            HashMap<String, String> new_map = new HashMap<>();
            new_map.put("nome", musica);
            new_map.put("proprietario", utilizador);
            lista.add(new_map);
        }

        escreve_ficheiro(lista, "dados");
    }

    public String enviar_playlist(HashMap map){
        map.put("categoria", "dados");
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        StringBuilder string = new StringBuilder();
        String proprietario = (String)map.get("username");
        String [] aux = new String[2];
        String [] lista_proprietarios;
        int contador=0, controlador=0;

        for(int i=0; i<lista.size(); i++){
            lista_proprietarios = lista.get(i).get("proprietario").split("/");
            for(int j=0; j<lista_proprietarios.length; j++)
                if(proprietario.compareTo(lista_proprietarios[j])==0)
                    contador++;
        }

        string.append("type|listplay;length|" + contador + ";items|");

        for(int i=0; i<lista.size(); i++){
            lista_proprietarios = lista.get(i).get("proprietario").split("/");
            for(int j=0; j<lista_proprietarios.length; j++) {
                if (proprietario.compareTo(lista_proprietarios[j]) == 0) {
                    aux = lista.get(i).get("nome").split("#");
                    if (controlador == 0) {
                        string.append(aux[0] + "/" + aux[1]);
                        controlador++;
                    } else
                        string.append("/" + aux[0] + "/" + aux[1]);
                }
            }
        }

        string.append(";username|" + proprietario + ";");
        return string.toString();
    }

    public String partilha_musica(HashMap map){
        //verificar se utilizador existe
        ArrayList<HashMap<String, String>> lista_utilizadores = le_ficheiro(map);
        String utilizador = (String)map.get("utilizador");
        int controlo = 0;

        for(int i=0; i<lista_utilizadores.size(); i++){
            if(utilizador.compareTo((String)lista_utilizadores.get(i).get("username"))==0)
                controlo++;
        }

        if(controlo==0)
            return "type|auto_res;username|" + map.get("username") + ";msg|Erro! Utilizador não existe";

        HashMap<String, String> aux = new HashMap<>();
        aux.put("type" , "exemplo");
        aux.put("categoria" , "dados");
        ArrayList<HashMap<String, String>> lista = le_ficheiro(aux);
        String nome = (String)map.get("musica") + "#" + (String)map.get("artista");
        String [] permissoes;
        System.out.println("nome: " + nome);
        controlo = 0;
        for(int i=0; i<lista.size(); i++){
            if(nome.compareTo((String)lista.get(i).get("nome"))==0){
                if((String)lista.get(i).get("permissoes") == null)
                    lista.get(i).put("permissoes", utilizador);
                else{
                    permissoes = ((String)lista.get(i).get("permissoes")).split("/");
                    for(int j=0; j<permissoes.length; j++){
                        if(permissoes[j].compareTo(utilizador)==0)
                            controlo++;
                    }

                    if(controlo==0)
                        lista.get(i).put("permissoes", lista.get(i).get("permissoes") + "/" + utilizador);
                }
            }
        }
        escreve_ficheiro(lista, "dados");
        return "type|auto_res;username|" + map.get("username") + ";msg|Operação efetuada com sucesso";
    }

    public String verificacao_download(HashMap map){
        HashMap<String, String> aux = new HashMap<>();
        aux.put("type", "random");
        aux.put("categoria", "musica");
        ArrayList<HashMap<String, String>> lista = le_ficheiro(aux);
        int index = Integer.parseInt((String)map.get("index"));

        String nome = (String)lista.get(index).get("nome") + "#" + (String)lista.get(index).get("artista");
        aux.put("categoria" , "dados");
        lista = le_ficheiro(aux);

        int controlador=0, controlador2=0;
        for(int i=0; i<lista.size(); i++){
            if(((String)lista.get(i).get("nome")).compareTo(nome)==0){
                controlador++;
                //verifica se é proprietario
                String [] array_propri = ((String)lista.get(i).get("proprietario")).split("/");
                for(int j=0; j<array_propri.length; j++){
                    if(array_propri[j].compareTo((String)map.get("username"))==0)
                        controlador2++;
                }
                //verifica se tem permissão
                if((String)lista.get(i).get("permissoes") != null) {
                    String[] array_permi = ((String) lista.get(i).get("permissoes")).split("/");
                    for (int j = 0; j < array_permi.length; j++) {
                        if (array_permi[j].compareTo((String) map.get("username")) == 0)
                            controlador2++;
                    }
                }
                if (controlador2 == 0)
                    return "msg|Erro! Não possui permissão para realizar o download em questão!";
            }
        }

        if(controlador == 0)
            return "msg|Erro! O ficheiro não existe!";
        else
            return "msg|Requisitos completos!";
    }

    public int verifica_editores(HashMap map){
        String editor = (String)map.get("username");
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        int index = Integer.parseInt((String)map.get("index"));
        String [] aux = ((String)lista.get(index).get("editor")).split("/");

        for(int i=0; i<aux.length; i++)
            if(editor.compareTo(aux[i])==0)
                return 0;

        return 1;
    }
}

class TCPThread extends Thread {

    private int PORT = 1904;
    HashMap <String, String> map;
    String flag;
    OutputStream out;
    InputStream in;
    ServerSocket socket;
    Socket connection;

    public TCPThread(HashMap map){
        this.map = map;
        this.flag=(String)map.get("acao");

        try{
            socket = new ServerSocket(PORT);
        }catch (IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }
    }


    public void run(){

        byte[] buffer;
        long f_length, current=0;
        int size;

        try{
            connection = socket.accept();

            //buscar nome ficheiro
            HashMap<String, String> aux = new HashMap<>();
            aux.put("categoria" , "musica");
            aux.put("type", "upload");
            ArrayList<HashMap<String, String>> lista = le_ficheiro(aux);
            String file_name = (String)lista.get(Integer.parseInt(map.get("index"))).get("nome") + "#" + (String)lista.get(Integer.parseInt(map.get("index"))).get("artista") + ".mp3";

            if(flag.compareTo("download")==0){
                out = connection.getOutputStream();
                try {
                    File file = new File(file_name);
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    f_length = file.length();
                    while (current != f_length) {
                        size = 10000;
                        if (f_length - current >= size)
                            current += size;
                        else {
                            size = (int) (f_length - current);
                            current = f_length;
                        }

                        buffer = new byte[size];
                        bis.read(buffer, 0, size);
                        out.write(buffer);
                    }

                    out.flush();
                    connection.close();
                    socket.close();
                }catch (FileNotFoundException e){
                    connection.close();
                    socket.close();
                }
            }
            else{
                in = connection.getInputStream();
                buffer = new byte[10000];
                int bytesread = 0;
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file_name));
                while((bytesread=in.read(buffer))!= -1){
                    bos.write(buffer,0,bytesread);
                }
                bos.flush();
                connection.close();
                socket.close();
            }

        }catch (IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }
    }

    //método que retorna num ArrayList a informação lida do ficheiro
    public ArrayList<HashMap<String, String>> le_ficheiro (HashMap map){
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        String categoria;
        String s;

        if(((String)map.get("type")).compareTo("login")==0 || ((String) map.get("type")).compareTo("utilizadores")==0 || ((String) map.get("type")).compareTo("promover")==0)
            categoria = "Registos";

        else
            categoria = (String)map.get("categoria");
        try{
            File f = new File(categoria + ".txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine())!=null){
                HashMap<String, String> aux = string_to_hash(s);
                lista.add(aux);
            }

            br.close();

        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }

        return lista;
    }

    //método que converte string em HashMap
    public HashMap string_to_hash(String string){

        HashMap<String, String> map = new HashMap<>();

        int contador=0;
        for(int i=0 ; i<string.length() ; i++)
            if(string.charAt(i) == '|' )
                contador++;

        String [] aux = new String[contador];
        String [] msg = new String[contador*2];

        aux = string.split(";");
        int controlador=0, controlador2=0;

        for(int i=0 ; i<aux.length ; i++) {
            for(int j=0; j<aux[i].length() ; j++)
                if(aux[i].charAt(j)=='|')
                    controlador = j;

            msg[controlador2] = aux[i].substring(0, controlador);
            msg[controlador2+1] = aux[i].substring(controlador+1, aux[i].length());

            controlador2+=2;
        }

        controlador2=0;
        for(int i=0 ; i<contador ; i++) {
            map.put(msg[controlador2], msg[controlador2 + 1]);
            controlador2 += 2;
        }

        return map;
    }
}