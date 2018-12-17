package Remover;

import Herditarios.Bean;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.TimeUnit;

public class RemoverBean extends Bean {

    public RemoverBean(){
        if(server == null) {
            try {
                server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace(); // TENTAR RECONECTAR...
            }
        }
    }

    public String executa_remover(String artista) throws RemoteException, InterruptedException, NotBoundException {
        try{
            return server.remove_artista(artista);
        } catch (RemoteException e){
            TimeUnit.SECONDS.sleep(5);
            server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            return server.remove_artista(artista);
        }
    }
}
