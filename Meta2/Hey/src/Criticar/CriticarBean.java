package Criticar;

import Herditarios.Bean;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.TimeUnit;

public class CriticarBean extends Bean {

    public CriticarBean(){
        if(server == null) {
            try {
                server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace(); // TENTAR RECONECTAR...
            }
        }
    }

    public String executa_criticar(String critica, String nota, String album) throws RemoteException, InterruptedException, NotBoundException {
        String aux = album.split("_")[0];
        String artista = album.split("_")[1];
        try{
            return server.adiciona_critica(critica, nota, aux, artista);
        } catch (RemoteException e){
            TimeUnit.SECONDS.sleep(5);
            server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            return server.adiciona_critica(critica, nota, aux, artista);
        }
    }
}
