package logic;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import model.User;
import utility.DebugPrinter;

/** Handles the connections to the game Database API */
public class DatabaseConnector {

    private static final String DATABASE_API_URL = "http://localhost:45786";
    private static final String DATABASE_API_UPDATE_KEY = "hangman1234";


    /** Authenticates the given credentials with the Database API */
    static boolean authenticateUser(String username, String password){
        int status;

        try{
            // Authenticate user
            status = Unirest.post(DATABASE_API_URL+"/user/{username}/authenticate")
                    .routeParam("username", username)
                    .body(password)
                    .asString().getStatus();
        }catch(Exception e){
            DebugPrinter.print("Database API server exception occured: " + e);
            return false;
        }

        if( status == 403 ){
            // Not authenticated
            return false;
        }

        if( status != 200 ){
            DebugPrinter.print("Database API server unknown error (status not 200)");
            return false;
        }

        return true;
    }


    /** Get User inforamtion through from the Database API */
    public static User getUser(String username){

        HttpResponse<String> response;
        try{
            response = Unirest.get(DATABASE_API_URL+"/user/{username}")
                    .routeParam("username", username)
                    .asString();
        }catch(Exception e){
            DebugPrinter.print("Database API server exception occured: " + e);
            return null;
        }

        // Check response
        if( response.getStatus() != 200 ){
            DebugPrinter.print(String.format("Lookup for client data failed (status not 200) (username: %s)",username));
            return null;
        }

        // Unpack response body JSON
        JSONObject userData = new JSONObject(response.getBody());

        // Create client
        User user = new User();
        user.username = userData.getString("username");
        user.firstname = userData.getString("firstname");
        user.lastname = userData.getString("lastname");
        user.rating = userData.getInt("rating");

        return user;
    }


    public static boolean setUserRating(String username, int rating){

        // Update Rating in database
        JSONObject body = new JSONObject()
                .put("rating", rating)
                .put("updateKey", DATABASE_API_UPDATE_KEY);

        // Update on server
        HttpResponse<String> response;
        try{
            response = Unirest.put(DATABASE_API_URL+"/user/{username}/rating")
                    .routeParam("username", username)
                    .body(body)
                    .asString();
        }catch(Exception e){
            DebugPrinter.print("Database API server exception occured: " + e);
            return false;
        }

        // Check result
        if( response.getStatus() != 200 ) {
            DebugPrinter.print("Update rating error (status not 200)");
            return false;
        }
        return true;
    }


}
