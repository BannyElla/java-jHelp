package jhelp;

/**
 *
 * @author Ella
 */
public class Main {

    public static void main(String[] args) {
        Client client = new Client();
        if (client.connect() == JHelp.OK) {
            client.run();
        }
    }
}
