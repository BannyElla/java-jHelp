/*
 * Server.java
 *
 */
package jhelp;

import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.System.err;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class sets a network connection between end client's objects of
 * {@link jhelp.Client} type and single {@link jhelp.ServerDb} object.
 *
 * @author <strong >Y.D.Zakovryashin, 2009</strong>
 * @version 1.0
 * @see jhelp.Client
 * @see jhelp.ClientThread
 * @see jhelp.ServerDb
 */
public class Server extends Thread implements JHelp {

    private ServerSocket serverSocket;    
    private Socket clientSocket;
    private int port;
    private int dbPort;
    private Properties commonConfig;
    private ServerDb db;
    boolean clientSession = true;


    /**
     * Creates a new instance of Server
     */
    public Server() {
        this(JHelp.DEFAULT_SERVER_PORT, JHelp.DEFAULT_DATABASE_PORT);
    }

    /**
     *
     * @param port
     * @param dbPort
     */
    public Server(int port, int dbPort) {
        this.port = port;
        this.dbPort = dbPort;
        db = new ServerDb(dbPort);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("SERVER: main");
        Server server = new Server();
        if (server.connect() == JHelp.OK) {
            server.start();
        }
    }

    /**
     * The method opens the client socket, creates a new thread
     * {@link jhelp.ClientThread} to which the client socket transfers
     */
    @Override
    public void run() {
        System.out.println("SERVER run");
        try (ServerSocket server = new ServerSocket(port)) {
            while (clientSession) {
                try {
                    Socket socket = server.accept();
                    clientSocket = socket;
                    serverSocket = server;
                    ClientThread cThread = new ClientThread(this, socket);
                    if(cThread.connect() == JHelp.OK) {
                        Thread tr = new Thread(cThread);
                        tr.start();
                    } else {
                        throw new IOException("Error of opening stream");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(err);
                    showErrorMessage(ex.getMessage());
                    disconnect();
                }
            }
        } catch (IOException ex) {
            showErrorMessage("Open server socket error: " + ex.getMessage());
            disconnect();
        }

    }

    /**
     * The method sets connection to database ({@link jhelp.ServerDb} object)
     * and create {@link java.net.ServerSocket} object for waiting of client's
     * connection requests. This method uses parameters from a configuration file
     * for connection.
     *
     * @return error code. The method returns {@link JHelp#OK} if streams are
     * successfully opened, otherwise the method returns {@link JHelp#ERROR}.
     */
    @Override
    public int connect() {
        System.out.println("SERVER: connect");
        commonConfig = initConfig(CONFIG_PATH + "server.cfg");
        port = Integer.valueOf(commonConfig.getProperty("port"));
        dbPort = Integer.valueOf(commonConfig.getProperty("dbPort"));
        return OK;
    }

    /**
     * The method sets connection to database ({@link jhelp.ServerDb} object)
     * and create {@link java.net.ServerSocket} object for waiting of client's
     * connection requests.
     *
     * @param args specifies properties of connection.
     * @return error code. The method returns {@link JHelp#OK} if connection are
     * openeds uccessfully, otherwise the method returns {@link JHelp#ERROR}.
     */
    @Override
    public int connect(String[] args) {
        System.out.println("SERVER: connect");
        port = Integer.valueOf(args[0]);
        dbPort = Integer.valueOf(args[1]);
        return OK;
    }

    /**
     * Transports initial {@link Data} object from {@link ClientThread} object
     * to {@link ServerDb} object and returns modified {@link Data} object to
     * {@link ClientThread} object.
     *
     * @param data Initial {@link Data} object which was obtained from client
     * application.
     * @return modified {@link Data} object
     */
    @Override
    synchronized public Data getData(Data data) {
        System.out.println("SERVER:getData");
        return db.getData(data);
    }

    /**
     * The method closes connection with database.
     *
     * @return error code. The method returns {@link JHelp#OK} if a connection
     * with database ({@link ServerDb} object) closed successfully, otherwise
     * the method returns {@link JHelp#ERROR} or any error code.
     */
    @Override
    public int disconnect() {
        System.out.println("SERVER: disconnect");
        clientSession = false;
        try {            
            if (serverSocket != null && serverSocket.isClosed() == false) {
                serverSocket.close();
            }
            if (clientSocket != null && clientSocket.isClosed() == false) {
                clientSocket.close();
            }
            if (db != null) {
                db.disconnect();
            }
        } catch (IOException ex) {
            ex.printStackTrace(err);
            showErrorMessage("Server disconnect error: " + ex.getMessage());
            
        }
        return JHelp.DISCONNECT;
    }
    
    /**
     * The method creates a Properties object {@link jhelp.Configuration}
     * for loading configuration parameters from a configuration file.
     * 
     * @param fileName - name of a configuration file
     * @return Properties object
     */
    private Properties initConfig(String fileName) {
        Properties commonConfig = null;
        try {
            commonConfig = new Configuration(fileName);
        } catch (FileNotFoundException fe) {
            showErrorMessage("Configuration file " + fileName +
                             " doesn't exist\n" + fe.getMessage());
            disconnect();
        } catch (IOException ie) {
            showErrorMessage(ie.getMessage());
            disconnect();
        }
        return commonConfig;
    }

    private void showErrorMessage(String message) {
        JFrame errorWindow = new JFrame();
        errorWindow.setAlwaysOnTop(true);
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        JOptionPane.showMessageDialog(errorWindow,
                "Server disconnect error: " + message  + 
                        "\nClass: " + ste.getClassName()+ 
                        "\nMethod " + ste.getMethodName() +
                             "(): " + ste.getLineNumber(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
