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

                if((((String)map.get("type")).compareTo("confirmacao")!=0) && (((String)map.get("type")).compareTo("status")!=0) && (((String)map.get("type")).compareTo("resposta")!=0)) {
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

    public String executa_info(HashMap map){

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
                /*else if((String)map.get("operacao").compareTo("remover")==0)
                    mensagem = remover_info(map);
                else if((String)map.get("operacao").compareTo("alterar")==0)
                    mensagem = alterar_info(map);*/
        }

        return mensagem;
    }


    public String login(HashMap map){
        String string = "";
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        int confirmacao;

        try{
            File f = new File("Registos.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String s;

            while((s = br.readLine())!=null){
                HashMap<String, String> aux = string_to_hash(s);
                lista.add(aux);
            }

            br.close();

            confirmacao = verifica_dados(map, lista);

            if(confirmacao==1)
                return "type|status;logged|on;msg|Bem-vindo!;username|" + map.get("username") + ";privilegio|" + map.get("privilegio");
            else if(confirmacao==-1)
                return "type|status;logged|off;username|" + map.get("username") + ";msg|Erro! Password incorreta";
            else
                return "type|status;logged|off;username|" + map.get("username") + ";msg|Erro! Utilizador não existe";

        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }

        return "";
    }

    public String registo(HashMap map){
        String mensagem;
        try{
            File f = new File("Registos.txt");
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);

            //verificar se o utilizador já existe
            mensagem = login(map);
            if(mensagem.compareTo("type|status;logged|off;msg|Erro! Utilizador não existe;username|" + map.get("username"))!=0)
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


    public int verifica_dados(HashMap map, ArrayList<HashMap<String, String>> lista){

        //Função que verifica se o nome de utilziador existe e a password coicide
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
        // album -> nome, artista, musicas
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
                pw.println("nome|" + map.get("nome") + ";artista|" + map.get("artista") + ";musicas|" + map.get("musicas"));
            else if(((String)map.get("categoria")).compareTo("artista")==0)
                pw.println("nome|" + map.get("nome") + ";albuns|" + map.get("albuns"));
            pw.close();
        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }

        return "type|resposta;username|" + map.get("username") + ";msg|Informação inserida com sucesso";
    }

    public int verifica_info(HashMap map){
        String s;
        String categoria = (String)map.get("categoria");
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();

        try {
            File f = new File(categoria + ".txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine())!=null){
                HashMap<String, String> aux = string_to_hash(s);
                lista.add(aux);
            }

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

            br.close();
        }catch(IOException e){
            System.out.println("Ocorreu a exceção "+ e);
        }
        return 1;
    }
}
