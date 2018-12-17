package Permissao;

import Herditarios.Bean;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.TimeUnit;

public class PermissaoBean extends Bean {

    public PermissaoBean(){
        if(server == null) {
            try {
                server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace(); // TENTAR RECONECTAR...
            }
        }
    }

    public String lista_leitores() throws RemoteException, InterruptedException, NotBoundException {
        try {
            return server.get_leitores();
        }
        catch (RemoteException e){
            TimeUnit.SECONDS.sleep(5);
            server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            return server.get_leitores();
        }
    }
}
