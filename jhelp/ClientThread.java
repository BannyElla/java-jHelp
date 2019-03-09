/*
 * Class ClientThread.
 */
package jhelp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.err;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class provides a network connection between end client of
 * {@link jhelp.Client} type and {@link jhelp.Server} object. Every object of
 * this class may work in separate thread.
 *
 * @author <strong >Y.D.Zakovryashin, 2009</strong>
 * @version 1.0
 * @see jhelp.Client
 * @see jhelp.Server
 */
public class ClientThread implements JHelp, Runnable {

    private Server server;
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Data data;

    /**
     * Creates a new instance of Client
     *
     * @param server reference to {@link Server} object.
     * @param socket reference to {@link java.net.Socket} object for connection
     * with client application.
     */
    public ClientThread(Server server, Socket socket) {
        System.out.println("MClient: constructor");
        this.server = server;
        this.clientSocket = socket;
    }

    /**
     * The method defines main job cycle for the object.
     */
    @Override
    public void run() {
        try {
            data = (Data) input.readObject();
            output.writeObject(server.getData(data));
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace(err);
            showErrorMessage("Thread run: " + ex.getStackTrace()[0]);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Opens input and output streams for data interchanging with client
     * application.
     *
     * @return error code. The method returns {@link JHelp#OK} if streams are
     * successfully opened, otherwise the method returns {@link JHelp#ERROR}.
     */
    @Override
    public int connect() {
        System.out.println("MClient: connect");
        try {
            input = new ObjectInputStream(clientSocket.getInputStream());
            output = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            return JHelp.ERROR;
        }
        return JHelp.OK;
    }

    /**
     * Opens input and output streams for data interchanging with client
     * application. This method uses parameters specified by parameter
     * <code>args</code>.
     *
     * @param args defines properties for input and output streams.
     * @return error code. The method returns {@link JHelp#OK} if streams are
     * successfully opened, otherwise the method returns {@link JHelp#ERROR}.
     */
    @Override
    public int connect(String[] args) {
        System.out.println("MClient: connect");
        return JHelp.OK;
    }

    /**
     * Transports {@link Data} object from client application to {@link Server}
     * and returns modified {@link Data} object to client application.
     *
     * @param data {@link Data} object which was obtained from client
     * application.
     * @return modified {@link Data} object
     */
    @Override
    public Data getData(Data data) {
        System.out.println("MClient: getData");
        return null;
    }

    /**
     * The method closes connection with client application.
     *
     * @return error code. The method returns {@link JHelp#OK} if input/output
     * streams and connection with client application was closed successfully,
     * otherwise the method returns {@link JHelp#ERROR}.
     */
    @Override
    public int disconnect() {
        System.out.println("MClient: disconnect");
        return JHelp.DISCONNECT;
    }

    private void showErrorMessage(String message) {
        JFrame errorWindow = new JFrame();
        errorWindow.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(errorWindow,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
