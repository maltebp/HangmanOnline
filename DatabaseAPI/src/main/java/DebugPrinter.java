public class DebugPrinter {

    private static boolean enabled = false;

    public static void toggle(boolean toggle){
        enabled = toggle;
    }

    public static void print(String msg){
        if( enabled ){
            System.out.println("DEBUG: " + msg);
        }
    }
}
