import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiInterface extends Remote {
    public String verificaLogin(String username, String password) throws RemoteException;
    public String verificaSignUp(String username, String password) throws RemoteException;
    public String inserirMusica(String nome, String artista, String album, String duracao, String username) throws RemoteException;
    public String inserirArtista(String artista, String album, String username) throws RemoteException;
    public String inserirAlbum(String album, String artista, String musicas, String username) throws RemoteException;
    public String listar(String categoria, String username) throws RemoteException;
    public String alterar(String categoria, String index, String campo, String novo, String username) throws RemoteException;
    public String remover(String categoria, String index, String username) throws RemoteException;
    public String pedir_pesquisa(String nome, String categoria, String username) throws RemoteException;
    public String pedir_detalhes(String nome, String nome2, String username, String categoria) throws RemoteException;
}
