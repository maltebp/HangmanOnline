package logic;

import model.User;
import model.GameState;

import javax.jws.WebMethod;
import javax.jws.WebService;


/**
 * SOAP Endpoint providing the online Hangman game logic.
 */
@WebService
public interface HangmanLogic {


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
    @WebMethod
    User login(String username, String password);


    /**
     * Log out the user identified by the given credentials.
     *
     * Logging out will finish active game, and update the user
     * rating accordingly.
     *
     * @param username The DTU Username (i.e. s185139)
     * @param password The 62597 course password
     */
    @WebMethod
    boolean logout(String username, String password);


    /**
     * Start a game for a user with given username, which has already
     * logged in.
     *
     * @return Information about the game (i.e. word to guess, guessed letters, remaining attempts etc.),
     *          or null if an error has occured.
     */
    @WebMethod
    GameState startGame(String username);


    /**
     * Guess a letter for the currently active game for the given user.
     *
     * User must be logged in, and a game must be started.
     *
     * @return The GameState after the letter has been guessed, or null if an error
     *          occured (client id doesn't exist, game hasn't started or game has finished)
     */
    @WebMethod
    GameState guessLetter(String username, char c);

}