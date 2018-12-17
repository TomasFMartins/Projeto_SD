package Registar;

import Herditarios.Bean;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.TimeUnit;

public class RegistarBean extends Bean {

    private String username; // username and password supplied by the user
    private String password;

    public RegistarBean() {
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String verificaRegisto() throws RemoteException, InterruptedException, NotBoundException {
        if(this.password != null && this.username != null) {
            try {
                return server.Registo(this.username, this.password);
            }
            catch (RemoteException e){
                TimeUnit.SECONDS.sleep(5);
                server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
                return server.Registo(this.username, this.password);
            }
        }
        else
            return "Erro";
    }
}
