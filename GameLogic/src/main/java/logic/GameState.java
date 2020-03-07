package logic;

import java.util.List;

public class GameState {

    boolean gameWon = false;
    boolean gameLost = false;
    String currentWord;
    List<String> correctLetters;
    List<String> wrongLetters;
    int remainingAttempts;

}
