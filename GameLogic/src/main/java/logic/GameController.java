package logic;

import kong.unirest.Unirest;
import model.GameState;
import org.json.JSONArray;
import utility.DebugPrinter;
import utility.WordList;


/**
 * Contains the actual logic of the Game. It is given a particular
 * GameState object which it operates its logic on.
 */
class GameController {

    // URL for an api, which provides a random english word
    private static final String WORD_API_URL = "https://random-word-api.herokuapp.com/word";

    // Letters which you may guess (i.e. no numbers)
    private static final String DICTIONAIRY = "ABCDEFGHIJKLMNOPQRSTUVWXabcdefhijklmnopqrstuvwxyz";

    // Number of attempts you get at guessing the words
    private static final int ATTEMPTS = 6;


    /**
     * Run the logic which "makes a guess" at a letter.
     * It checks against the word, and updates the guessed word and
     * attempts accordingly.
     */
    static void guessLetter(GameState gameState, char guessedLetter) {
        // Check if letter can be guessed
        if (DICTIONAIRY.indexOf(guessedLetter) == -1){
            // Letter not allowed
            gameState.guessResult = 3;
            return;
        }

        if( gameState.wrongLetters.contains(guessedLetter) || gameState.correctLetters.contains(guessedLetter) ){
            // Already guessed that letter
            gameState.guessResult = 4;
            return;
        }

        if( gameState.word.indexOf(guessedLetter) == -1 ){
            // Wrong letter
            gameState.wrongLetters.add(guessedLetter);
            gameState.remainingAttempts--;
            gameState.guessResult = 2;
        }else {
            // Correct letter
            gameState.correctLetters.add(guessedLetter);
            gameState.guessResult = 1;

            // Adjust guessed word
            String currentWord = "";
            for (char c : gameState.word.toCharArray()) {
                if (gameState.correctLetters.contains(c)) {
                    currentWord += c;
                } else {
                    currentWord += "*";
                }
            }
            gameState.currentWord = currentWord;
        }

        // Check if player has won
        if( !gameState.currentWord.contains("*") ){
            gameState.gameFinished = true;
            gameState.gameWon = true;
        }

        // Check if player has lost
        if( gameState.remainingAttempts == 0 ){
            gameState.gameFinished = true;
            gameState.gameWon = false;
        }
    }


    /**
     * Calculate how much the player's rating should change according to
     * the current game state (differentiate between gameWon=true/false)
     */
    static int calculateRatingChange(GameState gameState){
        int wordValue = gameState.word.length()*2;
        if( !gameState.gameWon ) wordValue *= -1;

        return wordValue;
    }


    /**
     * Setup a fresh GameState with a random word and a finite number of attempts.
     */
    static GameState setupGame(){

        // Start new game -------------------------------------------
        GameState newGame = new GameState();

        // Get word
        try{
            // Get word from api
            String responseBody = Unirest.get(WORD_API_URL)
                    .asString().getBody();
            newGame.word = new JSONArray(responseBody).getString(0);
        }catch(Exception e){
            DebugPrinter.printf("An exception occured when trying to get word: " + e + "\n");

            // Get offline backup word
            newGame.word = WordList.getRandomWord();
        }

        // Create "unguessed word"
        for(int i=0; i < newGame.word.length(); i++ ){
            newGame.currentWord += "*";
        }

        newGame.remainingAttempts = ATTEMPTS;
        return newGame;
    }

}
