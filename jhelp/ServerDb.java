/*
 * ServerDb.java
 *
 */
package jhelp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class presents server directly working with database. The complete
 * connection string should take the form of:<br>
 * <code><pre>
 *     jdbc:subprotocol://servername:port/datasource:user=username:password=password
 * </pre></code> Sample for using MS Access data source:<br>
 * <code><pre>
 *  private static final String accessDBURLPrefix
 *      = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
 *  private static final String accessDBURLSuffix
 *      = ";DriverID=22;READONLY=false}";
 *  // Initialize the JdbcOdbc Bridge Driver
 *  try {
 *         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
 *      } catch(ClassNotFoundException e) {
 *         System.err.println("JdbcOdbc Bridge Driver not found!");
 *      }
 *
 *  // Example: method for connection to a Access Database
 *  public Connection getAccessDBConnection(String filename)
 *                           throws SQLException {
 *       String databaseURL = accessDBURLPrefix + filename + accessDBURLSuffix;
 *       return DriverManager.getConnection(databaseURL, "", "");
 *   }
 * </pre></code>
 *
 * @author <strong >Y.D.Zakovryashin, 2009</strong>
 */
public class ServerDb implements JHelp {

    private Connection connection;
    private int port;
    private String dbName;
    private String user;
    private String password;
    private Properties commonConfig;
    private String selectDefinitionScript;
    private String insertTerminScript;
    private String selectTerminScript;
    private String deleteTerminScript;
    private String insertDefinitionScript;
    private String updateDefinitionScript;
    private String deleteDefinitionScript;
    private String countDefinitionScript;
    private String connectString;
    private String driver;

    {
        commonConfig = initConfig(CONFIG_PATH + "serverDb.cfg");
        selectDefinitionScript = initConfig(SCRIPT_PATH + "selectDefinition.sql").getProperty("script");
        insertTerminScript = initConfig(SCRIPT_PATH + "insertTermin.sql").getProperty("script");
        selectTerminScript = initConfig(SCRIPT_PATH + "selectTermin.sql").getProperty("script");
        deleteTerminScript = initConfig(SCRIPT_PATH + "deleteTermin.sql").getProperty("script");
        insertDefinitionScript = initConfig(SCRIPT_PATH + "insertDefinition.sql").getProperty("script");
        updateDefinitionScript = initConfig(SCRIPT_PATH + "updateDefinition.sql").getProperty("script");
        deleteDefinitionScript = initConfig(SCRIPT_PATH + "deleteDefinition.sql").getProperty("script");
        countDefinitionScript = initConfig(SCRIPT_PATH + "countDefinition.sql").getProperty("script");
        user = commonConfig.getProperty("user");
        password = commonConfig.getProperty("password");
        dbName = commonConfig.getProperty("dbName");
        connectString = commonConfig.getProperty("url") + ":" + port + "/" + dbName;
        driver = commonConfig.getProperty("driver");
    }

    /**
     * Creates a new instance of <code>ServerDb</code> with default parameters.
     * Default parameters are:<br>
     * <ol>
     * <li><code>ServerDb</code> host is &laquo;localhost&raquo;;</li>
     * <li>{@link java.net.ServerSocket} is opened on
     * {@link jhelp.JHelp#DEFAULT_DATABASE_PORT};</li>
     * </ol>
     */
    public ServerDb() {
        this(DEFAULT_DATABASE_PORT);
        System.out.println("SERVERDb: default constructor");
    }

    /**
     * Constructor creates new instance of <code>ServerDb</code>.
     *
     * @param port defines port for {@link java.net.ServerSocket} object.
     */
    public ServerDb(int port) {
        this.port = port;
        try {
            Class.forName(driver);
            System.out.println("SERVERDb: Server Constructed");
        } catch (ClassNotFoundException e) {
            showErrorMessage("Jdbc Client Driver not found! " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Constructor creates new instance of <code>ServerDb</code>.
     *
     * @param args array of {@link java.lang.String} type contains connection
     * parameters.
     */
    public ServerDb(String[] args) {
        this(Integer.valueOf(args[0]));
    }

    /**
     * Start method for <code>ServerDb</code> application.
     *
     * @param args array of {@link java.lang.String} type contains connection
     * parameters.
     */
    public static void main(String[] args) throws SQLException {
        System.out.println("SERVERDb: main");
        ServerDb server = new ServerDb();
        if (server.connect(args) == JHelp.READY) {
            server.run();
        }

    }

    /**
     * Method defines job cycle for client request processing.
     */
    private void run() {
        System.out.println("SERVERDb: run");
    }

    /**
     * Method returns result of client request to a database.
     *
     * @param data object of {@link jhelp.Data} type with request to database.
     * @return object of {@link jhelp.Data} type with results of request to a
     * database.
     * @see Data
     * @since 1.0
     */
    @Override
    public Data getData(Data data) {
        Data newData = data;
        int operation = data.getOperation();
        switch (operation) {
            case JHelp.SELECT:
                newData = selectData(data);
                break;
            case JHelp.INSERT:
                newData = insertData(data);
                break;
            case JHelp.UPDATE:
                newData = updateData(data);
                break;
            case JHelp.DELETE:
                newData = deleteData(data);
        }
        return newData;
    }

    private Data selectData(Data data) {
        try (Connection con = DriverManager.getConnection(connectString, user, password)) {
            connection = con;
            String term = data.getKey().getItem();
            PreparedStatement pstmt = con.prepareStatement(selectDefinitionScript);
            ResultSet result = queryData(pstmt, term);
            while (result.next()) {
                Item definition = new Item();
                definition.setId(result.getInt("id"));
                definition.setItem(result.getString("definition"));
                data.setValue(result.getRow() - 1, definition);
                data.getKey().setId(result.getInt("trem_id"));
            }
            if (data.getKey().getId() > 0) {
                data.setOperation(JHelp.ORIGIN);
            }
            if (!result.isClosed()) {
                result.close();
            }
            if (!pstmt.isClosed()) {
                pstmt.close();
            }
        } catch (SQLException ex) {
            showErrorMessage("SQL-select error: " + ex.getMessage());
        }
        return data;
    }

    private Data insertData(Data data) {
        try (Connection con = DriverManager.getConnection(connectString, user, password)) {
            connection = con;
            int defId = -1;
            int termId = -1;
            String term = data.getKey().getItem();
            PreparedStatement pstmt = con.prepareStatement(selectTerminScript);
            ResultSet result = queryData(pstmt, term);
            while (result.next()) {
                termId = result.getInt("id");
            }
            if (termId < 0) {
                termId = insertIntoTblTerms(term);
            }
            if (termId > 0) {
                for (Item item : data.getValues()) {
                    if (item.getState() != JHelp.INSERT) {
                        continue;
                    }
                    String definition = item.getItem();
                    defId = insertIntoTblDefinitions(definition, termId);
                    if (defId > 0) {
                        data.getKey().setId(termId);
                        item.setId(defId);
                        item.setState(JHelp.ORIGIN);
                        data.setOperation(JHelp.ORIGIN);
                    } else {
                        showErrorMessage("Definition isn't inserted into Db");
                    }
                }
            } else {
                showErrorMessage("Termin isn't inserted into Db");
            }
            if (!result.isClosed()) {
                result.close();
            }
            if (!pstmt.isClosed()) {
                pstmt.close();
            }
        } catch (SQLException ex) {
            showErrorMessage("SQL-insert error: " + ex.getMessage());
        }
        return data;
    }

    private Data updateData(Data data) {
        try (Connection con = DriverManager.getConnection(connectString, user, password)) {
            connection = con;
            PreparedStatement pstmt = con.prepareStatement(updateDefinitionScript);
            con.setAutoCommit(false);
            for (Item item : data.getValues()) {
                if (item.getState() != JHelp.UPDATE) {
                    continue;
                }
                int defId = item.getId();
                String newDef = item.getItem();
                pstmt.setString(1, newDef);
                pstmt.setInt(2, defId);
                pstmt.addBatch();
            }
            int[] countRows = pstmt.executeBatch();
            con.commit();
            if (countRows[0] > 0) {
                data.setOperation(JHelp.ORIGIN);
                for (Item item : data.getValues()) {
                    item.setState(JHelp.ORIGIN);
                }
            }
            if (!pstmt.isClosed()) {
                pstmt.close();
            }
        } catch (SQLException ex) {
            showErrorMessage("SQL-update error: " + ex.getMessage());
        }
        return data;
    }

    private Data deleteData(Data data) {
        try (Connection con = DriverManager.getConnection(connectString, user, password)) {
            connection = con;
            int deleteTerms = -1;
            int countDefinitions = -1;
            con.setAutoCommit(false);
            PreparedStatement pstmt = con.prepareStatement(deleteDefinitionScript);
            int termId = data.getKey().getId();
            for (Item item : data.getValues()) {
                if (item.getState() != JHelp.DELETE) {
                    continue;
                }
                int defId = item.getId();
                pstmt.setInt(1, defId);
                pstmt.addBatch();
            }
            int[] deleteDef = pstmt.executeBatch();
            con.commit();
            pstmt = con.prepareStatement(countDefinitionScript);
            pstmt.setInt(1, termId);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                countDefinitions = result.getInt(1);
            }
            if (countDefinitions == 0) {
                pstmt = con.prepareStatement(deleteTerminScript);
                pstmt.setInt(1, termId);
                deleteTerms = pstmt.executeUpdate();
            }
            con.commit();
            if (deleteDef.length > 0 && deleteTerms != 0) {
                for (Item item : data.getValues()) {
                    if (item.getState() == JHelp.DELETE) {
                        item.setState(JHelp.ORIGIN);
                    }
                }
                data.setOperation(JHelp.ORIGIN);
            }

            if (!pstmt.isClosed()) {
                pstmt.close();
            }
            if (!con.isClosed()) {
                con.close();
            }
        } catch (SQLException ex) {
            showErrorMessage("SQL-delete error: " + ex.getMessage());
        }
        return data;
    }

    /**
     * The method finds data by term
     * 
     * @param pstmt - PreparedStatement object
     * @param term - requested term
     * @return - ResultSet object
     * @throws SQLException 
     */
    private ResultSet queryData(PreparedStatement pstmt, String term) throws SQLException {
        pstmt.setString(1, "%" + term + "%");
        return pstmt.executeQuery();
    }

    private int insertIntoTblDefinitions(String definition, int id) throws SQLException {
        int defId = -1;
        PreparedStatement pstmt = connection.prepareStatement(insertDefinitionScript,
                Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, definition);
        pstmt.setInt(2, id);
        int result = pstmt.executeUpdate();
        if (result != 1) {
            throw new SQLException("Error insert data into table tblDefinitions. ");
        }
        ResultSet generatedKeys = pstmt.getGeneratedKeys();
        while (generatedKeys.next()) {
            defId = (int) generatedKeys.getLong(1);
        }
        pstmt.close();
        return defId;
    }

    private int insertIntoTblTerms(String term) throws SQLException {
        int termId = -1;
        PreparedStatement pstmt = connection.prepareStatement(insertTerminScript,
                Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, term);
        int result = pstmt.executeUpdate();
        if (result != 1) {
            throw new SQLException("Error insert data into table tblTerms. ");
        }
        ResultSet generatedKeys = pstmt.getGeneratedKeys();
        while (generatedKeys.next()) {
            termId = (int) generatedKeys.getLong(1);
        }
        pstmt.close();
        return termId;
    }

    /**
     *
     * @return error code. The method returns {@link JHelp#OK} if streams are
     * opened successfully, otherwise the method returns {@link JHelp#ERROR}.
     */
    @Override
    public int connect() {
        String[] args = {connectString, user, password};
        return connect(args);       
    }

    /**
     * Method sets connection to database and create
     * {@link java.net.ServerSocket} object for waiting of client's connection
     * requests.
     *
     * @return error code. Method returns {@link jhelp.JHelp#READY} in success
     * case. Otherwise method return {@link jhelp.JHelp#ERROR} or error code.
     */
    @Override
    public int connect(String[] args) {
        System.out.println("SERVERDb: connect");
        try (Connection con = DriverManager.getConnection(args[0], args[1], args[2])) {
            connection = con;
            System.out.println("SERVERDb: connect");

        } catch (SQLException ex) {
            showErrorMessage("Db connection error: " + ex.getMessage());
            disconnect();
            return JHelp.ERROR;
        }
        return JHelp.READY;
    }

    /**
     * Method disconnects <code>ServerDb</code> object from a database and
     * closes {@link java.net.ServerSocket} object.
     *
     * @return disconnect result. Method returns {@link #DISCONNECT} value, if
     * the process ends successfully. Othewise the method returns error code,
     * for example {@link #ERROR}.
     * @see jhelp.JHelp#DISCONNECT
     * @since 1.0
     */
    public int disconnect() {
        System.out.println("SERVERDb: disconnect");
        try {
            if (connection != null && connection.isClosed() == false) {
                System.out.println("close connection");
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("SERVERDB: fail close connection");
            showErrorMessage(ex.getMessage());
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
            showErrorMessage("Configuration file " + fileName + " doesn't exist\n" + fe.getMessage());
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
        JOptionPane.showMessageDialog(errorWindow,
                "Server disconnect error: " + message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
