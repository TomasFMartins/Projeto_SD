package Permissao;

import Herditarios.Action;
import Pesquisar.PesquisarBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

public class PermissaoAction extends Action implements SessionAware {

    public String execute() throws RemoteException, NotBoundException, InterruptedException {
        session.put("site" , "menupage");

        String resposta = this.getPermissaoBean().lista_leitores();

        if(resposta.startsWith("Erro")) {
            session.put("erro", resposta.split("!")[1]);
            return "Erro";

        }
        else{
            session.put("leitores", resposta);
            return "Sucesso";
        }
    }

    public PermissaoBean getPermissaoBean() {
        if(!session.containsKey("permissaoBean"))
            this.setPermissaoBean(new PermissaoBean());
        return (PermissaoBean) session.get("permissaoBean");
    }

    public void setPermissaoBean(PermissaoBean pesquisarBean) {
        this.session.put("permissaoBean", pesquisarBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }


}
