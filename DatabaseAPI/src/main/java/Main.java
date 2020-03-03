import java.util.Scanner;

public class Main {


    private static final int PORT = 45786;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("\nSTARTING HANGMAN DATABASE API");

        APIServer server = new APIServer(PORT);
        server.start();
        System.out.println("APIServer started!\n");

        System.out.println(
                "Console options:\n"+
                "\trestart\t\tRestart the server\n"+
                "\tstop\t\tStops the server process\n"+
                "\tdebug\t\tPrint debug messages until enter is pressed."
                );

        String input = null;
        while (true) {
            System.out.print("\n>");
            input = scanner.nextLine();

            if( input.equals("restart") ){
                System.out.println("Restarting server");
                server.restart();
                System.out.println("APIServer restarted");
            }

            if( input.equals("debug") ){
                System.out.println("Printing debug message until enter is pressed");
                DebugPrinter.toggle(true);
                scanner.nextLine();
                DebugPrinter.toggle(false);
                System.out.println("Stopped debug");
            }

            if( input.equals("stop"))
                break;
        }


        System.out.println("\nStopping server\n");
        server.stop();
        DatabaseConnector.close();
    }

}
