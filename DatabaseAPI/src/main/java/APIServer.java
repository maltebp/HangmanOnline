
import io.javalin.Javalin;
import org.json.JSONException;
import org.json.JSONObject;
import sun.security.ssl.Debug;


import java.util.List;

public class APIServer {

    // The key to be used when a rating should be updated
    private static final String UPDATE_KEY = "hangman1234";

    private int port;
    private Javalin javalinServer;

    public APIServer(int port){
        this.port = port;
    }

    public void start(){
        if( javalinServer != null )
            stop();

        javalinServer = Javalin.create().start(port);

        javalinServer.before(context -> {
            DebugPrinter.printf(
                    "\nNew Request: \n"+
                    "   Source: %s\n" +
                    "   Method: %s\n" +
                    "   Path: %s\n" +
                    "   Body: %s\n",
                    context.ip(),
                    context.method(),
                    context.path(),
                    context.body());
        });

        // Get All Users
        javalinServer.get("/user/all", context -> {
            List<User> users = DatabaseConnector.getInstance().getAllUsers();

            // Probably should find a better solution for this json conversion
            String userListJSON = "";
            for( User user : users ){
                if( !userListJSON.equals("") ){
                    userListJSON += ",";
                }
                userListJSON += user.toJSON();
            }

            context.result(String.format("{ \"users\" : [%s] }", userListJSON));
        });


        // Get user
        javalinServer.get("/user/:username", context -> {
            String username = context.pathParam("username");
            System.out.println("User requested: " + username);

            User user = DatabaseConnector.getInstance().getUser(username);
            if( user == null ){
                context.status(404);
                context.result(String.format("404: Couldn't find user with username '%s'", username));
            }else{
                System.out.println("200: Found user " + user);
                context.status(200);
                context.result(user.toJSON());
            }
        });


        // Authenticate user
        // Takes password in body
        javalinServer.post("/user/:username/authenticate", context -> {
            String username = context.pathParam("username");
            String password = context.body();
            DebugPrinter.print("User authentication requested: " + username + "   " + password);

            boolean success = DatabaseConnector.getInstance().authenticateUser(username, password);
            if( !success ){
                DebugPrinter.print("Authentication: failed");
                context.status(403);
                context.result("403: Cannot authenticate username and password combination");
            }else{
                DebugPrinter.print("Authentication: success");
                context.status(200);
                context.result("200: User successfully authenticated");
            }
        });


        /* Update rating
         *  Body takes JSON data: { "rating" : number, "updateKey" : string }*/
        javalinServer.put("user/:username/rating", context -> {
            String username = context.pathParam("username");

            try{
                // Extract JSON body
                JSONObject bodyJSON = new JSONObject(context.body());
                int rating = bodyJSON.getInt("rating");
                String updateKey = bodyJSON.getString("updateKey");

                // Check update key
                if( !updateKey.equals(UPDATE_KEY) ){
                    context.status(403);
                    context.result("403: Wrong update key");
                }else{
                    // Update rating
                    DatabaseConnector.getInstance().updateUserRating(username, rating);
                    context.status(200);
                    context.result("200: User rating update");
                    DebugPrinter.printf("Updated user rating (user: %s, rating: %s)", username, rating);
                }

            }catch(JSONException e){
                context.status(400);
                context.result("400: Invalid body format");
            }catch(IllegalArgumentException e){
                context.status(404);
                context.result(String.format("404: Couldn't find user with username '%s'", username));
            }
        });
    }

    public void stop(){
        if( javalinServer != null ) {
            javalinServer.stop();
            javalinServer = null;
        }
    }

    public void restart(){
        stop();
        start();
    }
}
