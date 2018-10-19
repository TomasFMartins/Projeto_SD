import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiInterface extends Remote {
    public String verificaLogin(String username, String password) throws RemoteException;
    public String verificaSignUp(String username, String password) throws RemoteException;
}
