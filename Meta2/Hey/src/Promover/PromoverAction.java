package Promover;

import Herditarios.Action;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

public class PromoverAction extends Action implements SessionAware {

    private String userApromover = null;

    @Override
    public String execute() throws RemoteException, NotBoundException, InterruptedException {
        session.put("site", "menupage");


        if(userApromover != null && !userApromover.equals("")) {
            String resposta = this.getPromoverBean().executa_promocao(userApromover);
            if (resposta.startsWith("Erro")) {
                session.put("erro", resposta.split("!")[1]);
                return "Erro";
            } else{
                session.put("promovido", userApromover);
                return "Sucesso";
            }
        }
        session.put("erro","Não selecionou um utilizador.");
        return "Erro";
    }

    public PromoverBean getPromoverBean() {
        if(!session.containsKey("promoverBean"))
            this.setPromoverBean(new PromoverBean());
        return (PromoverBean) session.get("promoverBean");
    }

    public void setPromoverBean(PromoverBean promoverBean) {
        this.session.put("promoverBean", promoverBean);
    }

    public void setUserApromover(String userApromover) {
        this.userApromover = userApromover;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
