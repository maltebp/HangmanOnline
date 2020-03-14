package model;

import java.util.LinkedList;
import java.util.List;


/** Represents the state of a particular Game
 *
 *  Used as DTO for SOAP
 * */
public class GameState {

    // NOTE: Field have to be public in order to be accesssable via soap

    public String word = "";
    public boolean gameFinished = false;
    public boolean gameWon = false;
    public String currentWord = "";
    public List<Character> correctLetters = new LinkedList<Character>();
    public List<Character> wrongLetters = new LinkedList<Character>();
    public int remainingAttempts;
    public int ratingChange = 0;


    /*  Guess Results
            0: No guess made
            1: Correct
            2: Wrong
            3: Wrong input (no within dictionairy)
            4: Already guessed
     */
    public int guessResult = 0;

    @Override
    public String toString(){
        return String.format("GameState( word=%s, finished=%s, won=%s, attempts=%d)", word, gameFinished, gameWon, remainingAttempts );
    }
}
