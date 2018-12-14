/**
 * Raul Barbosa 2014-11-07
 */
package rmiserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIServerInterface extends Remote {
	public String Login(String username, String password) throws RemoteException;
	public String Registo(String username, String password) throws RemoteException;
}
