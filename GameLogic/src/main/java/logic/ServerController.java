package logic;

import model.User;
import model.GameState;
import utility.DebugPrinter;

import javax.xml.ws.Endpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ServerController {

    private static final int PORT = 9901;


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);


        System.out.println("\nSTARTING HANGMAN GAME LOGIC SERVER");
        HangmanLogicServer server = new HangmanLogicServer();
        server.start(PORT);
        System.out.println("Server started!\n");

        System.out.println(
                "Console options:\n"+
                        "\tusers\t\tLists all the users who are currently logged in\n"+
                        "\tgames\t\tLists all active games\n"+
                        "\tdebug\t\tPrint debug messages until enter is pressed.\n" +
                        "\tstop\t\tStops the server"
        );

        String input = null;
        while (true) {
            System.out.print("\n>");
            input = scanner.nextLine();

            if( input.equals("users") ){
                List<String> users = server.getLoggedInUsers();
                HashMap<String, GameState> games = server.getGames();

                if( users.size() == 0 )
                    System.out.println("No users connected");
                else{
                    System.out.println("Current users: " + users.size());
                    for( String username : server.getLoggedInUsers() ){
                        User user = DatabaseConnector.getUser(username);
                        boolean inGame = games.containsKey(username);
                        System.out.printf("\t%s (%s %s, %d)%s\n",
                                username,
                                user.firstname,
                                user.lastname,
                                user.rating,
                                inGame ? " - (in game)" : "");
                    }
                }

            }

            if( input.equals("games") ){
                HashMap<String, GameState> games = server.getGames();

                if( games.size() == 0 )
                    System.out.println("No games running");
                else{
                    System.out.println("Current Games: " + games.size());
                    for( String username : games.keySet() ){
                        System.out.printf("\t%s: %s\n", username, games.get(username) );
                    }
                }
            }

            if( input.equals("debug") ){
                System.out.println("Printing debug message until enter is pressed");
                DebugPrinter.toggle(true);
                scanner.nextLine();
                DebugPrinter.toggle(false);
                System.out.println("Stopped debug");
            }

            if( input.equals("stop")) {
                System.out.println("Stopping server");
                break;
            }
        }

        server.stop();
        System.out.println("Server stopped");
    }

}
