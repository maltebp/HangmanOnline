package logic;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class TestClient {

    public static void main(String[] args) throws MalformedURLException {

        Scanner scan = new Scanner(System.in);

        URL url = new URL("http://localhost:9901/hangmanlogic?wsdl");
        QName qname = new QName("http://logic/", "HangmanLogicImplService");
        Service service = Service.create(url, qname);

        HangmanLogic logic = service.getPort(HangmanLogic.class);

        while(true){
            System.out.println("\nInput username and password: ");
            String name = scan.nextLine();
            String pass = scan.nextLine();

            System.out.print("\nAuthenticating... ");
            long clientId = logic.authenticate(name, pass);

            if( clientId == 0){
                System.out.println(" Failed!");
            }else{
                System.out.printf(" Success! (ID: %d)\n", clientId);
            }

        }
    }

}
