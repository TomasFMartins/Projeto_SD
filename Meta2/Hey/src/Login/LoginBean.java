package Login;

import Herditarios.Bean;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class LoginBean extends Bean {

    private String username; // username and password supplied by the user
    private String password;

    public LoginBean() {
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

    public String verificaLogin() throws RemoteException {
        if(this.password != null && this.username != null) {
            return server.Login(this.username, this.password);
            //falta catch
        }
        else
            return "Erro";
    }
}
