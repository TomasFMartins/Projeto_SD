package Promover;

import Herditarios.Bean;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class PromoverBean extends Bean {

    private String username = null;

    public PromoverBean(){
        if(server == null) {
            try {
                server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace(); // TENTAR RECONECTAR...
            }
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String executa_promocao(String username) throws RemoteException{
        return server.update_leitor(username);
    }
}
