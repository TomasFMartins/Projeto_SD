package Remover;

import Alterar.AlterarBean;
import Herditarios.Action;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RemoverAction extends Action {

    private String artista = null;

    @Override
    public String execute() throws RemoteException, NotBoundException, InterruptedException {
        session.put("site", "menupage");
        if (artista != null && !artista.equals("")) {
            String resposta = this.getRemoverBean().executa_remover(artista);
            if (resposta.startsWith("Erro")) {
                session.put("erro", resposta.split("!")[1]);
                return "Erro";
            } else {
                session.put("sucesso", "Artista removido com sucesso.");
                return "Sucesso";
            }
        }
        session.put("erro", "Tem que preencher todos os campos.");
        return "Erro";
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public RemoverBean getRemoverBean() {
        if(!session.containsKey("removerBean"))
            this.setRemoverBean(new RemoverBean());
        return (RemoverBean) session.get("removerBean");
    }

    public void setRemoverBean(RemoverBean removerBean) {
        this.session.put("removerBean", removerBean);
    }
}
