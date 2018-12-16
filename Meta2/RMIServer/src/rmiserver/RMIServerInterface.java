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
	public String inserir_musica(String nome, String album, String artista, String duracao) throws RemoteException;
	public String inserir_album(String album, String artista , String musicas) throws RemoteException;
	public String inserir_artista(String artista, String albuns) throws RemoteException;
	public String pesquisa_info(String pesquisa) throws RemoteException;
	public String get_leitores() throws RemoteException;
	public String update_leitor(String username) throws RemoteException;
}
