package logic;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface HangmanLogic {


    @WebMethod
    long authenticate(String username, String password);

    @WebMethod
    GameState startGame(long clientId);

    @WebMethod
    GameState guessLetter(long clientId);

    @WebMethod
    String getCorrectWord(long clientId);

    @WebMethod
    int getRatingChange(long clientId);

}