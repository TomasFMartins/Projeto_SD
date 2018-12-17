package Alterar;

import Criticar.CriticarBean;
import Herditarios.Action;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class AlterarAction extends Action {

    private String musicas = null;
    private String album = null;

    @Override
    public String execute() throws RemoteException, NotBoundException, InterruptedException {
        session.put("site","menupage");
        if(musicas != null && !musicas.equals("") && album != null && !album.equals("")) {
            String resposta = this.getAlterarBean().executa_alterar(musicas, album);
            if (resposta.startsWith("Erro")) {
                session.put("erro", resposta.split("!")[1]);
                return "Erro";
            } else{
                session.put("sucesso", "Álbum alterado com sucesso.");
                session.put("album" , album);
                return "Sucesso";
            }
        }
        session.put("erro", "Tem que preencher todos os campos.");
        return "Erro";
    }

    public AlterarBean getAlterarBean() {
        if(!session.containsKey("alterarBean"))
            this.setAlterarBean(new AlterarBean());
        return (AlterarBean) session.get("alterarBean");
    }

    public void setAlterarBean(AlterarBean alterarBean) {
        this.session.put("alterarBean", alterarBean);
    }

    public void setMusicas(String musicas) {
        this.musicas = musicas;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
