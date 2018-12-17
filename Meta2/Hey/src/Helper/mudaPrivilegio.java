package Helper;

import Herditarios.Action;

import java.util.Map;

public class mudaPrivilegio extends Action {

    @Override
    public void setSession(Map<String,Object> map){
        this.session = map;
    }

    public String execute() throws Exception{
        session.put("tipo" , "editor");
        return "Sucesso";
    }
}
