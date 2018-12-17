package Criticar;

import Herditarios.Action;
import Promover.PromoverBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class CriticarAction extends Action implements SessionAware {

    private String critica = null;
    private String nota = null;
    private String album = null;

    @Override
    public String execute() throws RemoteException, NotBoundException, InterruptedException {
        session.put("site","menupage");
        if(critica != null && !critica.equals("") && nota != null && !nota.equals("") && album != null && !album.equals("")) {
            String resposta = this.getCriticarBean().executa_criticar(critica, nota, album);
            if (resposta.startsWith("Erro")) {
                session.put("erro", resposta.split("!")[1]);
                return "Erro";
            } else{
                session.put("sucesso", "Crítica adicionada com sucesso.");
                session.put("noti_critica" , "Nota#"+album+"#"+nota);
                return "Sucesso";
            }
        }
        session.put("erro", "Tem que preencher todos os campos.");
        return "Erro";
    }

    public CriticarBean getCriticarBean() {
        if(!session.containsKey("criticarBean"))
            this.setCriticarBean(new CriticarBean());
        return (CriticarBean) session.get("criticarBean");
    }

    public void setCriticarBean(CriticarBean criticarBean) {
        this.session.put("criticarBean", criticarBean);
    }

    public void setCritica(String critica) {
        this.critica = critica;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
