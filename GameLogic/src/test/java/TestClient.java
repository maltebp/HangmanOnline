import model.User;
import model.GameState;
import logic.HangmanLogic;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.Scanner;

public class TestClient {

    public static void main(String[] args) throws Exception {

        System.out.println("Starting client");

        User user = null;
        Scanner scan = new Scanner(System.in);

        URL url = new URL("http://localhost:9901/hangmanlogic?wsdl");
        QName qname = new QName("http://logic/", "HangmanLogicServerService");
        Service service = Service.create(url, qname);

        HangmanLogic logic = service.getPort(HangmanLogic.class);

        System.out.println("Connected!");

        while(true){
            String input = scan.nextLine();

            if( input.equals("login") ) {

                System.out.println("\nInput username and password: ");
                String name = scan.nextLine();
                String pass = scan.nextLine();

                System.out.print("\nAuthenticating... ");
                user = logic.login(name, pass);

                if (user == null) {
                    System.out.println(" Failed!");
                } else {
                    System.out.println(" Success!");
                    System.out.println(user);
                }
            }

            if( input.equals("play") ){
                GameState state = logic.startGame(user.username);
                System.out.printf("Starting a new game: %s\n", state);

                do{
                    System.out.println("Current word: " + state.currentWord);
                    System.out.println("Remaining Attempts: " + state.remainingAttempts);

                    System.out.print("Correct letters: ");
                    for( Character c : state.correctLetters )
                        System.out.print(c + " ");
                    System.out.println();

                    System.out.print("Wrong letters: ");
                    for( Character c : state.wrongLetters )
                        System.out.print(c + " ");
                    System.out.println();
                    System.out.println("\nMake a guess!");
                    input = scan.nextLine();

                    if( !input.equals("") ){
                        char guess = input.charAt(0);
                        GameState newState = logic.guessLetter(user.username, guess);
                        if( newState != null ){
                            if( newState.remainingAttempts == state.remainingAttempts ){
                                System.out.println("Correct!");
                            }else{
                                System.out.println("Wrong!");
                            }
                            state = newState;
                        }else{
                            System.out.println("Error!");
                        }
                    }

                }while( !state.gameFinished );

                if( state.gameWon ){
                    System.out.println("You guessed correctly!");
                }else{
                    System.out.println("Game lost!");
                    System.out.printf("Correct word was: %s\n", state.word);
                }

            }

        }
    }

}
