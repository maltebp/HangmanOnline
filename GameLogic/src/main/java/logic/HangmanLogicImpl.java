package logic;

import kong.unirest.Client;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.apache.http.protocol.HTTP;

import javax.jws.WebService;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@WebService(endpointInterface = "logic.HangmanLogic")
public class HangmanLogicImpl implements HangmanLogic {

    private static final long CLIENT_ID_MAX = 100000000;


    private static HashMap<Long, ClientData> clientDataMap = new HashMap<Long, ClientData>();
    private static final String DATABASE_API_URL = "http://localhost:45786";

    public long authenticate(String username, String password) {

        // Authenticate user
        int status = Unirest.post(DATABASE_API_URL+"/user/{username}/authenticate")
            .routeParam("username", username)
            .body(password)
            .asString().getStatus();

        if( status != 200 ){
            DebugPrinter.print(String.format("Authentication not successful (u=%s, p=%s)", username, password));
            return 0;
        }

        DebugPrinter.print(String.format("Authentication successful (u=%s, p=%s)", username, password));

        // Get user data
        HttpResponse<String> response = Unirest.get(DATABASE_API_URL+"/user/{username}")
                .routeParam("username", username)
                .asString();

        // Check response
        if( response.getStatus() != 200 ){
            DebugPrinter.print(String.format("ERROR: Authenticated user, but couldn't fetch data (u=%s, p=%s)", username, password));
            return 0;
        }

        // Check if user already exists
        for( ClientData clientData : clientDataMap.values() ){
            if( clientData.getUsername().equals(username) ){
                return clientData.getId();
            }
        }

        // Unpack response body JSON
        JSONObject userData = new JSONObject(response.getBody());

        // Generate client id
        long clientId = 0;
        while(clientDataMap.containsKey(clientId) || clientId == 0){
            clientId = (long) (CLIENT_ID_MAX * Math.random());
        }

        // Create client
        ClientData client = new ClientData(
            clientId,
            userData.getString("username"),
            userData.getString("firstname"),
            userData.getString("lastname"),
            userData.getInt("rating")
        );
        clientDataMap.put(clientId, client);

        return clientId;
    }

    public GameState startGame(long clientId) {
        return null;
    }

    public GameState guessLetter(long clientId) {
        return null;
    }

    public String getCorrectWord(long clientId) {
        return null;
    }

    public int getRatingChange(long clientId) {
        return 0;
    }

    Collection<ClientData> getClients(){
        return clientDataMap.values();
    }
}
