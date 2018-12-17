package Alterar;

import Herditarios.Bean;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.TimeUnit;

public class AlterarBean extends Bean {

    public AlterarBean(){
        if(server == null) {
            try {
                server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace(); // TENTAR RECONECTAR...
            }
        }
    }

    public String executa_alterar(String musicas, String album) throws RemoteException, InterruptedException, NotBoundException {
        String aux = album.split("_")[0];
        String artista = album.split("_")[1];
        try{
            return server.altera_album(aux, artista, musicas);
        } catch (RemoteException e){
            TimeUnit.SECONDS.sleep(5);
            server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            return server.altera_album(aux, artista, musicas);
        }
    }
}
