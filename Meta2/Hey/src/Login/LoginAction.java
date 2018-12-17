package Login;

import Herditarios.Action;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

public class LoginAction extends Action implements SessionAware {

    private static final long serialVersionUID = 4L;
    private String username, password;

    @Override
    public String execute() throws RemoteException, NotBoundException, InterruptedException {
        session.clear();
        session.put("site","loginpage");
        session.put("loggedin", false);
        if(this.username != null && !username.equals("") && this.password != null && !password.equals("")) {
            this.getLoginBean().setUsername(this.username);
            this.getLoginBean().setPassword(this.password);
            String resposta = this.getLoginBean().verificaLogin();
            if(resposta.startsWith("Sucesso")) {
                session.put("username", username);
                session.put("loggedin", true);
                if (resposta.split(";")[1].equals("editor")) {
                    session.put("tipo","editor");
                }
                else {
                    session.put("tipo","leitor");
                }
                return "Sucesso";
            }
            else{
                session.put("erro", resposta);
                return "Erro";
            }
        }
        session.put("erro", "Os campos da página login não se encontram preenchidos!");
        return "Erro";
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public LoginBean getLoginBean() {
        if(!session.containsKey("loginBean"))
            this.setLoginBean(new LoginBean());
        return (LoginBean) session.get("loginBean");
    }

    public void setLoginBean(LoginBean loginBean) {
        this.session.put("loginBean", loginBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
