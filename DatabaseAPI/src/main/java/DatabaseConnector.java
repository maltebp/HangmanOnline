import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import com.mongodb.*;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *  Singleton class representing a connection to the database (MongoDB in this case). */
public class DatabaseConnector {

    // Settings
    private static final String DTU_USER_SERVER = "rmi://javabog.dk/brugeradmin";

    private static final String DB_URL = "mongodb://localhost:27017";
    private static final String DB_NAME = "HangmanDatabase";
    private static final String COLLECTION_USERS = "users";

    private MongoClient mongoClient;
    private DB database;


    public User getUser(String username) {

        // Get DTU bruger
        Bruger dtuUser = getDTUUser(username);
        if( dtuUser == null )
            // Error occured or it couldnt find dtu bruger
            return null;

        // Get Mongodb user
        DBCollection users = database.getCollection(COLLECTION_USERS);
        DBObject mongoUser = new BasicDBObject("_id", username);
        DBCursor cursor = users.find(mongoUser);

        // Check if user exists, otherwise add
        if( !cursor.hasNext() ){
            ((BasicDBObject) mongoUser).append("rating", 1000);
            users.insert(mongoUser);
        }else{
            mongoUser = cursor.one();
        }

        return createUser(mongoUser, dtuUser);
    }


    /**
     * Get a list of all "active" users. Active users are users who has
     * logged on with their DTU credentials at least once.
     * The list is sorted from highest rating to lowest.
     */
    public List<User> getAllUsers(){
        LinkedList<User> userList = new LinkedList<>();

        // Get Mongodb user
        DBCollection users = database.getCollection(COLLECTION_USERS);
        users.find();

        // Get other information from DTU bruger
        for(DBObject object : users.find() ) {
            Bruger dtuUser = getDTUUser(object.get("_id").toString());
            userList.add(createUser(object, dtuUser));
        }

        // Sort according to rating
        Collections.sort(userList);

        return userList;
    }



    public boolean authenticateUser(String username, String password){
        try {
            Brugeradmin dtuServer = (Brugeradmin) Naming.lookup(DTU_USER_SERVER);
            dtuServer.hentBruger(username, password);
        }catch( IllegalArgumentException e){
            return false;
        } catch( Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * @throws IllegalArgumentException If given username doesn't exist
     */
    public void updateUserRating(String username, int rating) throws IllegalArgumentException{

        // Check if user exists
        Bruger dtuUser = getDTUUser(username);
        if( dtuUser == null )
            throw new IllegalArgumentException(String.format("User with username '%s' doesn't exist", username));

        // Get Mongodb user
        DBCollection users = database.getCollection(COLLECTION_USERS);
        BasicDBObject currentUser = new BasicDBObject("_id", username);
        BasicDBObject updatedUser = new BasicDBObject("_id", username).append("rating", rating);

        // Update user (upsert: insert if it doesn't exist)
        users.update(currentUser, updatedUser, true, false);
    }



    /**
     * Closes the currently open DatabaseConnector */
    public static void close() {
        if( instance != null ) {
            instance.mongoClient.close();
            instance = null;
        }
    }


    private Bruger getDTUUser(String username){
        try {
            Brugeradmin dtuServer = (Brugeradmin) Naming.lookup(DTU_USER_SERVER);
            return dtuServer.hentBrugerOffentligt(username);
        } catch( IllegalArgumentException e) {
            return null;
        } catch(Exception e){
            return null;
        }
    }

    /**
     * Create User object from user entry from dtu users and mongo database user. */
    private static User createUser(DBObject mongoUser, Bruger dtuUser){
        User user = new User();
        user.setUsername(dtuUser.brugernavn);
        user.setFirstname(dtuUser.fornavn);
        user.setLastname(dtuUser.efternavn);
        user.setRating((int) mongoUser.get("rating"));
        return user;
    }


    // -------------------------------------------
    // Singleton logic
    private static DatabaseConnector instance = null;

    private DatabaseConnector() throws UnknownHostException {
        // Connecting to mongodb server
        mongoClient = new MongoClient(new MongoClientURI(DB_URL));
        database = mongoClient.getDB(DB_NAME);
    }

    public static DatabaseConnector getInstance() throws UnknownHostException {
        if( instance == null )
            instance = new DatabaseConnector();
        return instance;
    }
    // -------------------------------------------
}
