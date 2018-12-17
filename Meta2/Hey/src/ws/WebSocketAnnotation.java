package ws;

import rmiserver.RMIServerInterface;

import javax.management.remote.rmi.RMIServer;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/ws")
public class WebSocketAnnotation {
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private String username = null;
    private String tipo = null;
    private Session session;

    private static final Set<WebSocketAnnotation> users = new CopyOnWriteArraySet<>();

    public RMIServerInterface server = null;
    public String IP_RMI = "192.168.1.69";
    public int PORT_RMI = 1099;

    public WebSocketAnnotation() {}

    @OnOpen
    public void start(Session session) {
        this.session = session;
        this.users.add(this);
    }

    @OnClose
    public void end() throws IOException {
        this.session.close();
    	this.users.remove(this);
    }

    @OnMessage
    public void receiveMessage(String message) throws RemoteException {
		// one should never trust the client, and sensitive HTML
        // characters should be replaced with &lt; &gt; &quot; &amp;
        username = message.split("#")[0];
        tipo = message.split("#")[1];
        if(message.split("#").length>2) {
            if (message.split("#")[2].equals("promover"))
                sendPromovido(message.split("#")[3]);
            else if (message.split("#")[2].equals("Nota"))
                sendAtualizaNota(message.split("#")[2] + "#" + message.split("#")[3] + "#" + message.split("#")[4]);
            else if (message.split("#")[2].equals("Album"))
                sendAlbum(message.split("#")[3].split("_")[0],message.split("#")[3].split("_")[1]);
        }
    }
    
    @OnError
    public void handleError(Throwable t) {
    	t.printStackTrace();
    }

    private void sendMessage(String text) {
    	// uses *this* object's session to call sendText()
    	try {
            for(WebSocketAnnotation user:users){
                System.out.println("Iterating users...");
                user.session.getBasicRemote().sendText(text);

            }
		} catch (IOException e) {
			// clean up once the WebSocket connection is closed
			try {
				this.session.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
    }

    private void sendPromovido(String username){
        try {
            for(WebSocketAnnotation user:users){
                if(user.username.equals(username))
                    user.session.getBasicRemote().sendText("Foi Promovido!");
            }
        } catch (IOException e) {
            try {
                this.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void sendAtualizaNota(String mensagem) throws RemoteException {

        if(server == null) {
            try {
                server = (RMIServerInterface) LocateRegistry.getRegistry(IP_RMI, PORT_RMI).lookup("server");
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace(); // TENTAR RECONECTAR...
            }
        }
        String album = mensagem.split("#")[1].split("_")[0];
        String artista = mensagem.split("#")[1].split("_")[1];
        String resposta = server.pesquisa_album(album, artista);

        try {
            for(WebSocketAnnotation user:users){
                user.session.getBasicRemote().sendText("Nota;"+resposta);
            }
        } catch (IOException e) {
            try {
                this.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void sendAlbum(String album, String artista){
        try {
            for(WebSocketAnnotation user:users){
                if(user.tipo.equals("editor") && !user.username.equals(username))
                    user.session.getBasicRemote().sendText("Foi alterado as músicas do álbum '"+album+"' do artista '"+artista+"'.");
            }
        } catch (IOException e) {
            try {
                this.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
