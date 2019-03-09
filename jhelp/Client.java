/*
 * Client.java
 *
 */
package jhelp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Client class provides users's interface of the application.
 *
 * @author <strong >Y.D.Zakovryashin, 2009</strong>
 * @version 1.0
 */
public class Client extends JFrame implements JHelp {

    /**
     * Static constant for serialization
     */
    public static final long serialVersionUID = 1234;
    /**
     * Client configuration file name
     */

    private static final int DEFAULT_HEIGHT = 25;

    private ClientListener listener;
    private InetAddress address;
    private int port = JHelp.DEFAULT_SERVER_PORT;

    private Socket socket = null;
    /**
     * Programm Configerties
     */
    private Properties commonConfig;
    private Properties themeConfig;
    private Properties langConfig;
    public Properties userInterface;
    private MainPane mainPane;

    //-----------
    private ObjectOutputStream output;
    private ObjectInputStream input;
    //-----------

    /**
     * Private Data object presents informational data.
     */
    private Data data;

    {
        commonConfig = initConfig(getFullPath("client.cfg"));
        userInterface = initConfig(getFullPath("user_interface.cfg"));
        String themeName = userInterface.getProperty("theme");
        String langName = userInterface.getProperty("lang");
        themeConfig = initConfig(getFullPath(themeName));
        langConfig = initConfig(getFullPath(langName));
        data = new Data();
    }

    public Client() {
        listener = new ClientListener(this);
    }

    private Properties initConfig(String fileName) {
        Properties commonConfig = null;
        try {
            commonConfig = new Configuration(fileName);
        } catch (FileNotFoundException fe) {
            showErrorMessage(this, "Configuration file " + fileName +
                                   " doesn't exist\n" + fe.getMessage());
            dispose();
        } catch (IOException ie) {
            showErrorMessage(this, ie.getMessage());
            dispose();
        }
        return commonConfig;
    }

    /**
     * Method for application start
     *
     * @param args agrgument of command string
     */
    static public void main(String[] args) {

        Client client = new Client();
        if (client.connect() == JHelp.OK) {
            client.run();
        }
    }

    /**
     * Method define main job cycle
     */
    public void run() {
        setTitle(langConfig.getProperty("jhelp"));
        setBounds(50, 50, 720, 480);
        setMinimumSize(new Dimension(400, 300));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(listener.closeAction());
        setLayout(new BorderLayout());
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        getContentPane().add(content);

        JTabbedPane tabbet = new JTabbedPane();

        JPanel aboutPane = new AboutPane(langConfig, themeConfig, listener);
        JPanel SettingsPane = new SettingsPane(langConfig, themeConfig, listener);
        mainPane = new MainPane(langConfig, themeConfig, listener);

        tabbet.addTab(langConfig.getProperty("main"), mainPane);
        tabbet.addTab(langConfig.getProperty("settings"), SettingsPane);
        tabbet.addTab(langConfig.getProperty("about"), aboutPane);
        content.add(tabbet);
        setVisible(true);
    }

    /**
     * Method set connection to default server with default parameters
     *
     * @return error code
     * @throws java.net.UnknownHostException
     */
    @Override
    public int connect() {
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            showErrorMessage(this, "Get Local Host is fail " + ex.getMessage());
            dispose();
            return JHelp.ERROR;
        }
        return JHelp.OK;
    }

    /**
     * Method set connection to server with parameters defines by argument
     * <code>args</code>
     *
     * @return error code
     */
    @Override
    public int connect(String[] args) {
        String addressName = args[0];
        port = Integer.valueOf(args[1]);
        try {
            address = InetAddress.getByName(addressName);
        } catch (UnknownHostException ex) {
            showErrorMessage(this, "Unknown Host " + ex.getMessage());
            dispose();
            return JHelp.ERROR;
        }
        System.out.println("Client: connect");
        return JHelp.OK;
    }

    /**
     * Method gets data from data source
     *
     * @param data initial object (template)
     * @return new object
     */
    @Override
    public Data getData(Data data) {
        int operation = data.getOperation();
        this.data.setOperation(operation);
        String termin = getTermin().trim();
        this.data.getKey().setItem(termin);

        if (operation == JHelp.SELECT) {
            this.data.getKey().setId(-1);
            return this.data;
        }
        String dimention = getDimention().trim();
        if (dimention != null) {
            this.data.getValue(0).setItem(dimention);
            this.data.getValue(0).setState(operation);
        }
        return this.data;
    }

    public void processQuery(Data data) {
        int operation = data.getOperation();
        if (isDisconnect(operation)) {
            disconnect();
            return;
        }
        try {
            socket = new Socket(address, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            output.writeObject(data);

            input = new ObjectInputStream(socket.getInputStream());
            Data response = (Data) input.readObject();
            this.data = response;
            int operationStatus = response.getOperation();
            if (isFailRequest(operationStatus)) {
                showErrorMessage(this, "Request faild");
                return;
            }
            processResponse(operation);
            mainPane.showDefinition(this.data.getValue(0).getItem());
        } catch (IOException | ClassNotFoundException e) {
            showErrorMessage(this, "Process query error: " + e.getMessage());
        }
    }

    private boolean isDisconnect(int operation) {
        return operation == JHelp.DISCONNECT;
    }

    private boolean isFailRequest(int operation) {
        return operation != JHelp.ORIGIN;
    }

    private void processResponse(int originalOperation) {
        switch (originalOperation) {
            case JHelp.UPDATE:
                showInfoMessage(this, "Record successfully updated");
                break;
            case JHelp.INSERT:
                showInfoMessage(this, "Record successfully inserted");
                break;
            case JHelp.DELETE:
                showInfoMessage(this, "Record successfully deleted");
                this.data.getKey().setItem(null);
                this.data.getKey().setId(-1);
                this.data.getValue(0).setItem(null);
                this.data.getValue(0).setId(-1);
                mainPane.cleanFields();
                break;
        }
    }

    /**
     * Method disconnects client and server
     *
     * @return error code
     */
    public int disconnect() {
        System.out.println("Client: disconnect");
        try {
            if (socket != null && socket.isClosed() == false) {
                socket.close();
                socket = null;
            }
            if (output != null) {
            output.close();
            output = null;
            }
        } catch (IOException ex) {
            showErrorMessage(this, "Disconnect error: " + ex.getMessage());
        }
        dispose();
        return JHelp.OK;
    }

    public String getTermin() {
        return mainPane.getTermin();
    }

    public String getDimention() {
        return mainPane.getDimention();
    }

    public void showInfoMessage(JFrame mainWindow, String message) {
        JOptionPane.showMessageDialog(mainWindow,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void showWarningMessage(JFrame mainWindow, String message) {
        JOptionPane.showMessageDialog(mainWindow,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    public void showErrorMessage(JFrame mainWindow, String message) {
        JOptionPane.showMessageDialog(mainWindow,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public String getFullPath(String fileName) {
        return CONFIG_PATH + fileName;
    }
}
