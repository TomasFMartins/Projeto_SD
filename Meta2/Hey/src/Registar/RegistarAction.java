package Registar;

import Herditarios.Action;
import Login.LoginBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class RegistarAction extends  Action implements SessionAware {

    private static final long serialVersionUID = 4L;
    private String username, password;

    @Override
    public String execute() throws RemoteException {
        session.put("site","signuppage");
        if(this.username != null && !username.equals("") && this.password != null && !password.equals("")) {
            this.getRegistarBean().setUsername(this.username);
            this.getRegistarBean().setPassword(this.password);
            String resposta = this.getRegistarBean().verificaRegisto();
            System.out.println(resposta);
            if(resposta.startsWith("Sucesso")) {
                super.session.put("username", username);
                super.session.put("loggedin", true);
                return "Editor";
            }
            else{
                session.put("erro","Esse utilizador já existe!");
                return "Erro";
            }
        }
        session.put("erro","Os campos devem estar preenchidos");
        return "Erro";
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public RegistarBean getRegistarBean() {
        if(!session.containsKey("RegistarBean"))
            this.setRegistarBean(new RegistarBean());
        return (RegistarBean) session.get("RegistarBean");
    }

    public void setRegistarBean(RegistarBean registarBean) {
        this.session.put("RegistarBean", registarBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
