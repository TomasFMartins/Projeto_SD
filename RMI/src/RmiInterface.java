import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * É o Rmi Interface.
 *
 * @author Damião Santos
 */
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
    public String enviarCritica(String nome, String artista, String critica, String nota, String username) throws RemoteException;
    public String pedirUtilizadores(String username) throws RemoteException;
    public String promover(String change, String username) throws RemoteException;
    public String Notificacoes(String username) throws RemoteException;
    public void killThread(String username) throws RemoteException;
    public String tcp(File file, String action, String username, int index, String nome, String artista) throws RemoteException;
    public String pedirBiblioteca(String username) throws RemoteException;
    public String permissao(String user, String username, String musica, String artista) throws RemoteException;
}
