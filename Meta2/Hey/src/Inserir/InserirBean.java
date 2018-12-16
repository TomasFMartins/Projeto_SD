package Inserir;

import Herditarios.Bean;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

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

    public String inserirMusica(String nomeMusica, String nomeArtista, String nomeAlbum, String duracao) throws RemoteException {
        return server.inserir_musica(nomeMusica, nomeAlbum, nomeArtista, duracao);
    }

    public String inserirArtista(String nomeArtista, String nomeAlbuns) throws RemoteException{
        return server.inserir_artista(nomeArtista, nomeAlbuns);
    }

    public String inserirAlbum(String nomeAlbum, String nomeArtista, String musicasAlbum) throws RemoteException{
        return server.inserir_album(nomeAlbum, nomeArtista, musicasAlbum);
    }
}
