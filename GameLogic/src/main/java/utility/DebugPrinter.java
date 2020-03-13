package utility;

/**
 * Static class to print debug messages which may be toggled on and of
 * using the toggle(..) method. */
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

    public static void printf(String format, Object ... args){
        if( enabled ){
            System.out.printf("DEBUG: "+format, args);
        }
    }
}
