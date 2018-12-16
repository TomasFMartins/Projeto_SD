package rmiserver;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerRMI extends UnicastRemoteObject implements RMIServerInterface {

    private static final long serialVersionUID = 20141107L;
    private HashMap<String, String> users;

    public ServerRMI() throws RemoteException {
        super();
        users = new HashMap<String, String>();
    }

    public String Login(String username, String password) throws RemoteException {
        String s;
        String [] aux;

        try {
            File f = new File("Registos.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while ((s = br.readLine()) != null) {
                aux = s.split(";");
                if(username.compareTo(aux[0])==0) {
                    if (password.compareTo(aux[1]) == 0)
                        return "Sucesso;" + aux[2];
                    else
                        return "Erro;Password incorreta.";
                }
            }
            br.close();

        } catch (IOException e) {
            System.out.println("Ocorreu a exceção " + e);
            return "Erro;IOException";
        }

        return "Erro;Utilizador inexistente";
    }

    public String Registo(String username, String password) throws RemoteException{
        int controlo=0;
        String s;
        String [] aux;

        try{
            File f = new File("Registos.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while ((s = br.readLine()) != null) {
                aux = s.split(";");
                if(username.compareTo(aux[0])==0)
                    controlo=1;
            }

            br.close();

            if(controlo==1)
                return "Erro";
            else{
                File file = new File("Registos.txt");
                FileWriter fw = new FileWriter(file, true);
                PrintWriter pw = new PrintWriter(fw);

                pw.println(username + ";" + password + ";leitor");
                pw.close();

                return "Sucesso";
            }

        }catch (IOException e){
            System.out.println("Ocorreu a exceção " + e);
        }

        return "Erro";
    }

    public String inserir_musica(String nome, String album, String artista, String duracao) throws RemoteException{

        String msg = verifica_info(nome, album, artista, "Musicas");

        if(msg.startsWith("Erro"))
            return msg;

        try{
            File f = new File("Musicas.txt");
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(nome+";"+album+";"+artista+";"+duracao);

            pw.close();
        }catch(IOException e){
            System.out.println("Ocorreu a exceção" + e);
        }

        return "Sucesso";
    }

    public String inserir_album(String album, String artista , String musicas) throws RemoteException{
        String msg = verifica_info(null, album, artista, "Albuns");

        if(msg.startsWith("Erro"))
            return msg;

        try{
            File f = new File("Albuns.txt");
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(album+";"+artista+";"+musicas);

            pw.close();
        }catch(IOException e){
            System.out.println("Ocorreu a exceção" + e);
        }

        return "Sucesso";
    }

    public String inserir_artista(String artista, String albuns) throws RemoteException{
        String msg = verifica_info(null, null, artista, "Artistas");

        if(msg.startsWith("Erro"))
            return msg;

        try{
            File f = new File("Artistas.txt");
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(artista+";"+albuns);

            pw.close();
        }catch(IOException e){
            System.out.println("Ocorreu a exceção" + e);
        }

        return "Sucesso";
    }

    public String verifica_info(String musica, String album, String artista, String flag){
        String s;
        String [] aux;
        try{
            File f = new File(flag+".txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            if(flag.compareTo("Musicas")==0){
                while((s=br.readLine())!= null){
                    aux = s.split(";");
                    if(musica.compareTo(aux[0])==0 && album.compareTo(aux[1])==0 && artista.compareTo(aux[2])==0 )
                        return "Erro|Música já existe";
                }
            }
            else if(flag.compareTo("Albuns")==0){
                while((s=br.readLine())!= null){
                    aux = s.split(";");
                    if(album.compareTo(aux[0])==0 && artista.compareTo(aux[1])==0 )
                        return "Erro|Álbum já existe";
                }
            }else{
                while((s=br.readLine())!= null){
                    aux = s.split(";");
                    if(artista.compareTo(aux[0])==0 )
                        return "Erro|Artista já existe";
                }
            }

            br.close();

        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
            return "Erro|IOException";
        }

        return "Sucesso!";
    }

    public String pesquisa_info(String pesquisa) throws RemoteException{
        String s;
        String resposta="";
        String [] aux;
        try{
            File f = new File("Musicas.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine()) != null){
                if(s.split(";")[0].toUpperCase().contains(pesquisa.toUpperCase()))
                    resposta += "musica;" + s.split(";")[0] + ";" + s.split(";")[1] + ";" + s.split(";")[2] + ";"+ s.split(";")[3] + "#";
            }

            br.close();

            f = new File("Albuns.txt");
            fr = new FileReader(f);
            br = new BufferedReader(fr);

            while((s = br.readLine()) != null){
                if(s.split(";")[0].toUpperCase().contains(pesquisa.toUpperCase())) {
                    //resposta += "album;" + s.split(";")[0] + ";" + s.split(";")[1] + ";" + s.split(";")[2] + "#";
                    aux = s.split(";");
                    if(aux.length<5)
                        resposta += "album;" + aux[0] + ";" + aux[1] + ";" + aux[2] + ";;#";
                    else
                        resposta += "album;" + aux[0] + ";" + aux[1] + ";" + aux[2] + ";" + aux[3] + ";" + aux[4] +"#";
                }
            }

            br.close();

            f = new File("Artistas.txt");
            fr = new FileReader(f);
            br = new BufferedReader(fr);

            while((s = br.readLine()) != null){
                if(s.split(";")[0].toUpperCase().contains(pesquisa.toUpperCase()))
                    resposta += "artista;" + s.split(";")[0] + ";" + s.split(";")[1] + "#";
            }

            br.close();

        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
            return "Erro!";
        }

        return resposta;

    }

    public String get_leitores() throws RemoteException{
        String s;
        String leitores = "";

        try{
            File f = new File("Registos.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine()) != null){
                if(s.split(";")[2].compareTo("leitor")==0)
                    leitores += s.split(";")[0] + ";";
            }

            br.close();

        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
            return "Erro!IOException.";
        }

        if(leitores.equals(""))
            return "Erro!Não existem utilizadores com permissão de leitor.";

        return leitores;
    }

    public String update_leitor(String username) throws RemoteException{
        String s;
        ArrayList<String> utilizadores = new ArrayList<>();
        String aux_utilizador = "";

        try{
            File f = new File("Registos.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine()) != null){
                utilizadores.add(s);
                if(s.split(";")[0].compareTo(username)==0)
                    aux_utilizador += s.split(";")[0] + ";" + s.split(";")[1] + ";editor";
            }

            br.close();

            f = new File("Registos.txt");
            FileWriter fw = new FileWriter(f);
            PrintWriter pw = new PrintWriter(fw);

            for(int i=0; i<utilizadores.size(); i++){
                if(utilizadores.get(i).split(";")[0].compareTo(username)==0)
                    pw.println(aux_utilizador);
                else
                    pw.println(utilizadores.get(i));
            }

            pw.close();

        }catch(IOException e){
            System.out.println("Ocorreu a exceção " + e);
            return "Erro!IOException.";
        }

        return "Sucesso";
    }




    public static void main(String args[]) {
        int controlo = 0;
        while (controlo == 0){
            try {
                RMIServerInterface server = new ServerRMI();
                Registry r = LocateRegistry.createRegistry(1099);
                r.rebind("server", server);
                controlo = 1;
                System.out.println("=== Rmi Server Arrancou ===");
            } catch (RemoteException re) {
                controlo = 0;
            }
        }
    }

}
