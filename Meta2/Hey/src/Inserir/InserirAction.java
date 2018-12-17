package Inserir;

import Herditarios.Action;
import Login.LoginBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

public class InserirAction extends Action implements SessionAware {

    String musicaNome = null;
    String musicaArtista = null;
    String musicaAlbum = null;
    String musicaDuracao = null;

    String artistaNome = null;
    String artistaAlbuns = null;

    String albumNome = null;
    String albumArtista = null;
    String albumMusicas = null;

    @Override
    public String execute() throws RemoteException, NotBoundException, InterruptedException {
        session.put("site","inserir");
        if(musicaNome != null && !musicaNome.equals("") && musicaArtista != null && !musicaArtista.equals("") && musicaAlbum != null && !musicaAlbum.equals("") && musicaDuracao != null && !musicaDuracao.equals("")){
            String resposta = this.getInserirBean().inserirMusica(musicaNome, musicaArtista, musicaAlbum, musicaDuracao);
            if(resposta.startsWith("Erro")){
                session.put("erro","A música já se encontra inserida.");
                return "Erro";
            }
            session.put("sucesso","Música inserida com sucesso.");
            return "Sucesso";
        }
        else if(artistaNome != null && !artistaNome.equals("") && artistaAlbuns != null && !artistaAlbuns.equals("")){
            String resposta = this.getInserirBean().inserirArtista(artistaNome, artistaAlbuns);
            if(resposta.startsWith("Erro")){
                session.put("erro","O artista já se encontra inserido.");
                return "Erro";
            }
            session.put("sucesso","Artista inserido com sucesso.");
            return "Sucesso";
        }
        else if(albumNome != null && !albumNome.equals("") && albumArtista != null && !albumArtista.equals("") && albumMusicas != null && !albumMusicas.equals("")){
            String resposta = this.getInserirBean().inserirAlbum(albumNome, albumArtista, albumMusicas);
            if(resposta.startsWith("Erro")){
                session.put("erro","O Álbum já se encontra inserido.");
                return "Erro";
            }
            session.put("sucesso","Álbum inserido com sucesso.");
            return "Sucesso";
        }
        session.put("erro","Os campos estão vazios");
        return "Erro";
    }

    public InserirBean getInserirBean() {
        if(!session.containsKey("inserirBean"))
            this.setInserirBean(new InserirBean());
        return (InserirBean) session.get("inserirBean");
    }

    public void setInserirBean(InserirBean inserirBean) {
        this.session.put("inserirBean", inserirBean);
    }

    public void setMusicaNome(String musicaNome) {
        this.musicaNome = musicaNome;
    }

    public void setMusicaArtista(String musicaArtista) {
        this.musicaArtista = musicaArtista;
    }

    public void setMusicaAlbum(String musicaAlbum) {
        this.musicaAlbum = musicaAlbum;
    }

    public void setMusicaDuracao(String musicaDuracao) {
        this.musicaDuracao = musicaDuracao;
    }

    public void setArtistaNome(String artistaNome) {
        this.artistaNome = artistaNome;
    }

    public void setArtistaAlbuns(String artistaAlbuns) {
        this.artistaAlbuns = artistaAlbuns;
    }

    public void setAlbumNome(String albumNome) {
        this.albumNome = albumNome;
    }

    public void setAlbumArtista(String albumArtista) {
        this.albumArtista = albumArtista;
    }

    public void setAlbumMusicas(String albumMusicas) {
        this.albumMusicas = albumMusicas;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
