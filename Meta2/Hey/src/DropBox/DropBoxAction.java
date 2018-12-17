package DropBox;

import Herditarios.Action;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import uc.sd.apis.DropBoxApi2;

public class DropBoxAction extends Action {

    // Access codes #1: per application used to get access codes #2
    private static final String API_APP_KEY = "tlrvwoq4eiq0zyz";
    private static final String API_APP_SECRET = "kerktra432gnc6q";

    String code = null;
    String link = null;

    @Override
    public String execute() throws Exception{
        session.put("site","menupage");

        OAuthService service = new ServiceBuilder()
                .provider(DropBoxApi2.class)
                .apiKey(API_APP_KEY)
                .apiSecret(API_APP_SECRET)
                .callback("http://localhost:8080/Hey/dropboxpage.action")
                .build();

        Token accessToken = (Token) session.get("accessToken");
        try {
            if ( code == null && accessToken == null) {
                link = service.getAuthorizationUrl(null);
                session.put("link", link);
                return "teste";
            }
            else{
                if(accessToken == null){
                    Verifier verifier = new Verifier(code);
                    accessToken = service.getAccessToken(null, verifier);
                    session.put("accessToken", accessToken);
                    listFiles(service, accessToken);
                }
                else{
                    listFiles(service, accessToken);
                }
            }
        } catch(OAuthException e) {
            e.printStackTrace();
        } finally {
        }
        session.put("sucesso", "Você está logado com sucesso!");
        return "Sucesso";
    }

    public void setCode(String code) {
        this.code = code;
    }

    private static void listFiles(OAuthService service, Token accessToken) {
        OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/files/list_folder", service);
        request.addHeader("authorization", "Bearer " + accessToken.getToken());
        request.addHeader("Content-Type",  "application/json");
        request.addPayload("{\n" +
                "    \"path\": \"\",\n" +
                "    \"recursive\": false,\n" +
                "    \"include_media_info\": false,\n" +
                "    \"include_deleted\": false,\n" +
                "    \"include_has_explicit_shared_members\": false,\n" +
                "    \"include_mounted_folders\": true\n" +
                "}");

        Response response = request.send();
        System.out.println(response.getBody());

        JSONObject rj = (JSONObject) JSONValue.parse(response.getBody());
        System.out.println(rj);
        JSONArray contents = (JSONArray) rj.get("entries");
        System.out.println(contents.size());
        for (int i=0; i<contents.size(); i++) {
            System.out.println("Entrou");
            JSONObject item = (JSONObject) contents.get(i);
            String path = (String) item.get("name");
            System.out.println(" - " + path);
        }
    }

    public void setLink(String link) {
        this.link = link;
    }
}
