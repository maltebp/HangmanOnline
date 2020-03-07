package logic;

import javax.xml.ws.Endpoint;
import java.util.Scanner;

public class LogicServer {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Starting logic server");

        HangmanLogicImpl logic = new HangmanLogicImpl();

        Endpoint.publish("http://localhost:9901/hangmanlogic", logic);

        System.out.println("Started server!");

        while(true) {
            System.out.print("\n>");
            String input = scanner.nextLine();

            if( input.equals("clients") ){
                for( ClientData client : logic.getClients() ){
                    System.out.println(client.getUsername());
                }
            }
        }



    }

}
