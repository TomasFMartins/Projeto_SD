package Inserir;

import Herditarios.Bean;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.TimeUnit;

public class InserirBean extends Bean {

    public InserirBean() {
        if(server == null) {
            try {
                server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace(); // TENTAR RECONECTAR...
            }
        }
    }

    public String inserirMusica(String nomeMusica, String nomeArtista, String nomeAlbum, String duracao) throws RemoteException, InterruptedException, NotBoundException {
        try {
            return server.inserir_musica(nomeMusica, nomeAlbum, nomeArtista, duracao);
        }
        catch (RemoteException e){
            TimeUnit.SECONDS.sleep(5);
            server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            return server.inserir_musica(nomeMusica, nomeAlbum, nomeArtista, duracao);
        }
    }

    public String inserirArtista(String nomeArtista, String nomeAlbuns) throws RemoteException, InterruptedException, NotBoundException {
        try {
            return server.inserir_artista(nomeArtista, nomeAlbuns);
        }
        catch (RemoteException e){
            TimeUnit.SECONDS.sleep(5);
            server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            return server.inserir_artista(nomeArtista, nomeAlbuns);
        }
    }

    public String inserirAlbum(String nomeAlbum, String nomeArtista, String musicasAlbum) throws RemoteException, NotBoundException, InterruptedException {
        try {
            return server.inserir_album(nomeAlbum, nomeArtista, musicasAlbum);
        } catch (RemoteException e){
            TimeUnit.SECONDS.sleep(5);
            server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            return server.inserir_album(nomeAlbum, nomeArtista, musicasAlbum);
        }
    }
}
