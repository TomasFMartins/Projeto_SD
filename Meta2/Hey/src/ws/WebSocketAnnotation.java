package ws;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
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

    public WebSocketAnnotation() {}

    @OnOpen
    public void start(Session session) {
        this.session = session;
        System.out.println(session);
        this.users.add(this);
    }

    @OnClose
    public void end() throws IOException {
        this.session.close();
    	this.users.remove(this);
    }

    @OnMessage
    public void receiveMessage(String message) {
		// one should never trust the client, and sensitive HTML
        // characters should be replaced with &lt; &gt; &quot; &amp;
        username = message.split("#")[0];
        tipo = message.split("#")[1];
        if(message.split("#").length>2)
            if(message.split("#")[2].equals("promover"))
                sendPromovido(message.split("#")[3]);
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
                    user.session.getBasicRemote().sendText("Foi promovido!");
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
