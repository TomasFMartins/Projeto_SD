package rmiserver;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ServerRMI extends UnicastRemoteObject implements RMIServerInterface {

    private static final long serialVersionUID = 20141107L;
    private HashMap<String, String> users;
    //static HashMap<String, RMIServerInterface> interfaces;

    public ServerRMI() throws RemoteException {
        super();
        users = new HashMap<String, String>();
    }

    public String Login(String username, String password) throws RemoteException {
        String s;
        String [] aux;
        System.out.println("username|" + username + " pass|" + password);
        try {
            File f = new File("Registos.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while ((s = br.readLine()) != null) {
                aux = s.split(";");
                if(username.compareTo(aux[0])==0 && password.compareTo(aux[1])==0) {
                    //guardar users
                    return "Sucesso;" + aux[2];
                }
            }

            br.close();

        } catch (IOException e) {}

        return "Erro";
    }

    public String Registo(String username, String password) throws RemoteException{
        int controlo=0;
        String s;
        String [] aux;
        System.out.println("entrou");
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
