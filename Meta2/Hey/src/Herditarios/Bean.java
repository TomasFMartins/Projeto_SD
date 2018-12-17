package Herditarios;

import rmiserver.RMIServerInterface;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Bean implements Serializable {

    public RMIServerInterface server = null;
    public String IP_RMI = "192.168.1.69";
    public int PORT_RMI = 1099;

    public Bean(){

        try{
            server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }
}