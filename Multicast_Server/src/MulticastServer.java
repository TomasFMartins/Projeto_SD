import java.io.*;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
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

                if((((String)map.get("type")).compareTo("confirmacao")!=0) && (((String)map.get("type")).compareTo("status")!=0) && (((String)map.get("type")).compareTo("resposta")!=0) && (((String)map.get("type")).compareTo("lista")!=0) && (((String)map.get("type")).compareTo("info")!=0) && (((String)map.get("type")).compareTo("lista_utili")!=0)) {
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
        }

        return mensagem;
    }


    public String login(HashMap map){
        String string = "";
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        int confirmacao;

        confirmacao = verifica_dados(map, lista);

        if(confirmacao==1)
            return "type|status;logged|on;msg|Bem-vindo!;username|" + map.get("username") + ";privilegio|" + get_privilegio(map);
        else if(confirmacao==-1)
            return "type|status;logged|off;username|" + map.get("username") + ";msg|Erro! Password incorreta";
        else
            return "type|status;logged|off;username|" + map.get("username") + ";msg|Erro! Utilizador não existe";
    }

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
                pw.println("nome|" + map.get("nome") + ";artista|" + map.get("artista") + ";album|" + map.get("album") + ";duracao|" + map.get("duracao"));
            else if(((String)map.get("categoria")).compareTo("album")==0)
                pw.println("nome|" + map.get("nome") + ";artista|" + map.get("artista") + ";musicas|" + map.get("musicas") /*";critica|;nota|"*/);
            else if(((String)map.get("categoria")).compareTo("artista")==0)
                pw.println("nome|" + map.get("nome") + ";albuns|" + map.get("albuns"));
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
        string.append(";username|" + map.get("username"));
        mensagem = string.toString();
        return mensagem;
    }

    public String alterar_info(HashMap map){
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        int index = Integer.parseInt((String)map.get("index"));
        String categoria = (String)map.get("categoria");

        lista.get(index).put((String)map.get("campo"), (String)map.get("info"));

        escreve_ficheiro(lista, categoria);

        return "type|resposta;username|" + map.get("username") + ";msg|Informação alterada com sucesso";
    }

    public String remover_info(HashMap map){
        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        int index = Integer.parseInt((String)map.get("index"));
        String categoria = (String)map.get("categoria");

        lista.remove(index);

        escreve_ficheiro(lista, categoria);
        return "type|resposta;username|" + map.get("username") + ";msg|Informação removida com sucesso!";
    }

    public String consulta_info(HashMap map, ArrayList<HashMap<String, String>> lista){

        //ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        int index = -1;

        String artista = (String)map.get("artista");
        String categoria = (String)map.get("categoria");
        StringBuilder string = new StringBuilder();
        String nome = (String)map.get("nome");

        System.out.println("teste: " + nome + " " + artista + " " + categoria);

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
        string.append(";username|" + map.get("username"));

        return string.toString();
    }

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

    public String lista_utilizadores(HashMap map, String flag){

        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        StringBuilder string = new StringBuilder();

        string.append("type|lista_utili;length|" + lista.size() + ";items|");

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

        string.append(";username|" + (String)map.get("username"));
        return string.toString();
    }

    public String promover_user(HashMap map){

        ArrayList<HashMap<String, String>> lista = le_ficheiro(map);
        String utilizador = (String)map.get("utilizador");

        for(int i=0; i<lista.size(); i++){
            if(utilizador.compareTo((String)lista.get(i).get("username"))==0){
                lista.get(i).put("privilegio", "editor");
                escreve_ficheiro(lista, "Registos");
                gera_notificacao(map, "promovido");
                return "type|confirmacao;username|" + utilizador + ";msg|Utilizador promovido a editor!";
            }
        }

        return "";
    }

    //método que gere notificações
    public void gera_notificacao(HashMap map, String flag){
        String utilizador = (String)map.get("utilizador");
        String categoria = "notificacoes";


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

    //método que escreve informação num ficheiro
    public void escreve_ficheiro(ArrayList<HashMap<String, String>> lista, String categoria){

        try{
            File f = new File(categoria + ".txt");
            FileWriter fw = new FileWriter(f);
            PrintWriter pw = new PrintWriter(fw);

            for(int i=0; i<lista.size(); i++) {
                if(categoria.compareTo("musica")==0)
                    pw.println("nome|" + lista.get(i).get("nome") + ";artista|" + lista.get(i).get("artista") + ";album|" + lista.get(i).get("album") + ";duracao|" + lista.get(i).get("duracao"));
                else if(categoria.compareTo("album")==0) {
                    if(((String)lista.get(i).get("critica")) != null)
                        pw.println("nome|" + lista.get(i).get("nome") + ";artista|" + lista.get(i).get("artista") + ";musicas|" + lista.get(i).get("musicas") + ";critica|" + lista.get(i).get("critica") + ";nota|" + lista.get(i).get("nota"));
                    else
                        pw.println("nome|" + lista.get(i).get("nome") + ";artista|" + lista.get(i).get("artista") + ";musicas|" + lista.get(i).get("musicas"));
                }
                else if(categoria.compareTo("artista")==0)
                    pw.println("nome|" + lista.get(i).get("nome") + ";albuns|" + lista.get(i).get("albuns"));
                else if(categoria.compareTo("Registos")==0)
                    pw.println("username|" + lista.get(i).get("username") + ";password|" + lista.get(i).get("password") + ";privilegio|" + lista.get(i).get("privilegio"));
                else if(categoria.compareTo("notificacoes")==0) {
                    //escrever notificação num ficheiro
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

}
