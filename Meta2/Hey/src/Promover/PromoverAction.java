package Promover;

import Herditarios.Action;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class PromoverAction extends Action implements SessionAware {

    private String username;

    @Override
    public String execute() throws RemoteException {
        session.put("site", "menupage");

        String resposta = this.getPromoverBean().executa_promocao("rien");


        if(resposta.startsWith("Erro")) {
            session.put("erro" , resposta.split("!")[1]);
            return "Erro";
        }
        else
            return "Sucesso";
    }

    public PromoverBean getPromoverBean() {
        if(!session.containsKey("promoverBean"))
            this.setPromoverBean(new PromoverBean());
        return (PromoverBean) session.get("promoverBean");
    }

    public void setPromoverBean(PromoverBean promoverBean) {
        this.session.put("promoverBean", promoverBean);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
