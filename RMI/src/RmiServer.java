import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

public class RmiServer extends UnicastRemoteObject implements  RmiInterface {

    private static final long serialVersionUID = 1L;
    private String IP_SERVER = "224.3.2.1";
    private int PORT = 4321;

    public RmiServer() throws  RemoteException{
        super();
    }

    //METODOS
    public String verificaLogin(String username, String password) throws RemoteException{
        int contador = 0;
        for(int i = 0; i<username.length(); i++){
            if(username.charAt(i) == '|' && username.charAt(i) == ';')
                contador++;
        }
        for(int i = 0; i<password.length(); i++){
            if(password.charAt(i) == '|' || password.charAt(i) == ';')
                contador++;
        }
        if(contador != 0){
            return "Erro! Os campos nao podem conter os caracteres ; e |.";
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
                    System.out.println("Teste");
                    socket.receive(msgPacket);
                    String msg = new String(buffer, 0, buffer.length);
                    System.out.println(msg);
                    if(msg.startsWith("type|status") && msg.contains("username|"+username+";")){
                        if(msg.split(";")[1].contains("off")) {
                            return msg.split(";")[3].substring(4);
                        }else{
                            return msg.split(";")[4].substring(11);
                        }
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
                return "Erro! CATCH";
            }
        }
    }


    public String verificaSignUp(String username, String password) throws RemoteException{
        if(username.length()<3 || username.length()>16){
            return "Erro! O nome de utilizador deve ter entre 3 a 16 caracteres.";
        }else if(password.length()<3 || password.length()>16){
            return "Erro! A password deve ter entre 3 a 16 caracteres.";
        }else{
            int contador = 0;
            for(int i = 0; i<username.length(); i++){
                if(username.charAt(i) == '|' && username.charAt(i) == ';')
                    contador++;
            }
            for(int i = 0; i<password.length(); i++){
                if(password.charAt(i) == '|' || password.charAt(i) == ';')
                    contador++;
            }
            if(contador != 0){
                return "Erro! Os campos nao podem conter os caracteres ; e |.";
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
                        System.out.println("Teste");
                        socket.receive(msgPacket);
                        String msg = new String(buffer, 0, buffer.length);
                        System.out.println(msg);
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

    public static void main(String args[]){
        try{
            RmiServer server = new RmiServer();
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("rmiSERVER", server);
        } catch (RemoteException re){
            System.out.println("Exception in Main " + re);
        }
    }

}
