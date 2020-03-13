package logic;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import model.User;
import model.GameState;
import utility.DebugPrinter;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


@WebService(endpointInterface = "logic.HangmanLogic")
public class HangmanLogicServer implements HangmanLogic, Runnable {

    private static final long TIMEOUT = 120;
    private static final long TIMEOUT_CHECK_FREQ = 10; // Seconds

    private Endpoint endpoint;

    // Currently logged in users
    private static final LinkedList<String> loggedInUsers = new LinkedList<String>();

    // User's last active time (updated at every server request)
    private static final HashMap<String, Long> timeoutMap = new HashMap<String, Long>();

    // User's games
    private static final HashMap<String, GameState> gameMap = new HashMap<String, GameState>();

    private static boolean checkTimeout = true;
    private Thread timeoutThread;



    public HangmanLogicServer(){
        // Start the timeout thread
        timeoutThread = new Thread(this);

    }

    void start(int port){
        HangmanLogicServer logic = new HangmanLogicServer();
        endpoint = Endpoint.publish(String.format("http://[::]:%d/hangmanlogic", port), logic);
        timeoutThread.start();
    }

    void stop(){
        endpoint.stop();
        checkTimeout = false;
        timeoutThread.interrupt();
    }


    /**
     * Login to the logic server with the given DTU credentials.
     *
     * The user will automatically be logged out if no requests are made to
     * the server with the username after 120 seconds.
     *
     * @param username The DTU Username (i.e. s185139)
     * @param password The 62597 course password
     *
     * @return Information about the user (including name and rating), or null if error occured (i.e. already logged in)
     */
    public User login(String username, String password) {
        if (loggedInUsers.contains(username)){
            DebugPrinter.printf("Login failed (user '%s' is already logged in)\n", username);
            return null;
        }

        if( DatabaseConnector.authenticateUser(username, password) ){
            DebugPrinter.printf("Login successful (u=%s, p=%s)\n", username, password);
            loggedInUsers.add(username);
            timeoutMap.put(username, System.currentTimeMillis());
            return DatabaseConnector.getUser(username);
        }else{
            DebugPrinter.printf("Login failed");
            return null;
        }
    }


    /**
     * Log out the user identified by the given credentials.
     *
     * Logging out will finish active game, and update the user
     * rating accordingly.
     *
     * @param username The DTU Username (i.e. s185139)
     * @param password The 62597 course password
     */
    public void logout(String username, String password){
        if( !DatabaseConnector.authenticateUser(username, password)){
            DebugPrinter.printf("Logout Failed: couldn't authenticate user (%s, %s)", username, password);
            return;
        }

        removeClient(username);
        DebugPrinter.print(String.format("Logout (%s, %s): success", username, password));
    }


    /**
     * Start a game for a user with given username, which has already
     * logged in.
     *
     * @return Information about the game (i.e. word to guess, guessed letters, remaining attempts etc.),
     *          or null if an error has occured.
     */
    public GameState startGame(String username) {

        // Check if client is authenticated
        if( !loggedInUsers.contains(username) ){
            DebugPrinter.printf("Start game failed (username %s not authenticated)", username);
            return null;
        }

        updateTimeout(username);

        // Check and clear existing game
        GameState existingGame = gameMap.get(username);
        if( existingGame != null && !existingGame.gameFinished ){
            clearGame(username);
        }

        // Setup new game
        GameState newGame = GameController.setupGame();
        gameMap.put(username, newGame);

        DebugPrinter.print( String.format("Starting new game for client (%s): %s", username, newGame));
        return newGame;
    }



    /**
     * Guess a letter for the currently active game for the given user.
     *
     * User must be logged in, and a game must be started.
     *
     * @return The GameState after the letter has been guessed, or null if an error
     *          occured (client id doesn't exist, game hasn't started or game has finished)
     */
    public GameState guessLetter(String username, char guessedLetter) {

        // Check if client exists
        if (!loggedInUsers.contains(username)) {
            DebugPrinter.printf("Guess letter failed (user '%s' not authenticated)", username);
            return null;
        }

        updateTimeout(username);

        // Check if game is running
        GameState state = gameMap.get(username);
        if( state == null || state.gameFinished )
            return null;

        GameController.guessLetter(state, guessedLetter);
        if( state.gameFinished )
            state = clearGame(username);

        return state;
    }


    // SERVER SIDE  UTILITY FUNCTIONS --------------------------------------------------------------

    List<String> getLoggedInUsers(){
        return loggedInUsers;
    }

    HashMap<String, GameState> getGames(){
        return gameMap;
    }


    /** Removes a client as "logged" in, cleaning up games and timeout data */
    private void removeClient(String username){
        if( gameMap.containsKey(username) )
            clearGame(username);
        loggedInUsers.remove(username);
        timeoutMap.remove(username);
    }

    /** Clears the game for a client, calculating/updating rating etc. */
    private GameState clearGame(String username){
        User user = DatabaseConnector.getUser(username);
        GameState gameState = gameMap.get(username);
        int ratingChange  = GameController.calculateRatingChange(gameState);

        // Prevent rating from going below 0
        if(user.rating + ratingChange < 0) ratingChange = -user.rating;
        gameState.ratingChange = 0;

        // Update Rating in database
        if( ratingChange != 0 ){
            DatabaseConnector.setUserRating(user.username, user.rating + ratingChange);
        }
        gameMap.remove(username);
        DebugPrinter.printf("Cleared game for %s (rating change: %d)\n", username, ratingChange);
        return gameState;
    }


    /** Reset the timeout timer for a user */
    private void updateTimeout(String username){
        synchronized (timeoutMap) {
            if( timeoutMap.containsKey(username) ){
                timeoutMap.remove(username);
                timeoutMap.put(username, System.currentTimeMillis());
            }
        }
    }

    /** Check if any users have timed out, and in that case remove them */
    private void checkTimeouts(){
        DebugPrinter.printf("Checking timeouts\n");
        LinkedList<String> clientsToRemove = new LinkedList<String>();

        // Find clients to remove
        synchronized (timeoutMap){
            for( String username : timeoutMap.keySet() ){
                if( System.currentTimeMillis() - timeoutMap.get(username) > TIMEOUT*1000 )
                    clientsToRemove.add(username);
            }
        }

        // Remove clients
        for( String client : clientsToRemove ){
            removeClient(client);
            DebugPrinter.printf("User '%s' timed out\n", client);
        }
    }



    /** Periodically run timeout checks
     * (Runnable run method, for thread starting point.) */
    public void run() {
        while(checkTimeout){
            try{
                checkTimeouts();
                Thread.sleep(TIMEOUT_CHECK_FREQ*1000);
            }catch(InterruptedException e){
                DebugPrinter.printf("Timeout thread was interrupted\n");
            }
        }
    }
}
