package Pesquisar;

import Herditarios.Action;
import Inserir.InserirBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class PesquisarAction extends Action implements SessionAware {

    String pesquisa = null;

    @Override
    public String execute() throws RemoteException {
        session.put("site","menupage");
        if(pesquisa != null && !pesquisa.equals("")){
            String resposta = this.getPesquisarBean().pesquisar(pesquisa);
            System.out.println(resposta);
            session.put("pesquisa",resposta);
            return "Sucesso";
        }
        session.put("erro","Os campos estão vazios");
        return "Erro";
    }

    public void setPesquisa(String pesquisa) {
        this.pesquisa = pesquisa;
    }

    public PesquisarBean getPesquisarBean() {
        if(!session.containsKey("pesquisarBean"))
            this.setPesquisarBean(new PesquisarBean());
        return (PesquisarBean) session.get("pesquisarBean");
    }

    public void setPesquisarBean(PesquisarBean pesquisarBean) {
        this.session.put("pesquisarBean", pesquisarBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
