package jhelp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

/**
 * Class defines a process for all events what happens in client form.
 *
 * @author <strong >Y.D.Zakovryashin</strong>, 2009
 * @version 1.0
 */
public class ClientListener extends WindowAdapter
        implements ActionListener, KeyListener, TextListener {

    private Client client;
    private Data data;
    private Map<String, String> themeFiles;
    private Map<String, String> langFiles;
    private String fileNameNewTheme;
    private String fileNameNewLang;
    private final String CFG = ".cfg";

    {
        themeFiles = new HashMap<>();
        themeFiles.put("default", "theme_default");
        themeFiles.put("по умолчанию", "theme_default");
        themeFiles.put("pink", "theme_pink");
        themeFiles.put("розовая", "theme_pink");
        langFiles = new HashMap<>();
        langFiles.put("eng", "lang_eng");
        langFiles.put("rus", "lang_rus");
        langFiles.put("английский", "lang_rus");
        langFiles.put("русский", "lang_eng");
        fileNameNewTheme = themeFiles.get("default") + CFG;
        fileNameNewLang = langFiles.get("eng") + CFG;
    }

    /**
     * Single constructor of the class.
     *
     * @param client references to client form
     */
    public ClientListener(Client client) {
        this.client = client;
        System.out.println(fileNameNewTheme);
        System.out.println(fileNameNewLang);
    }

    /**
     * Method for processing of {@link java.awt.event.ActionEvent} events.
     *
     * @param e reference to {@link java.awt.event.ActionEvent} event what
     * happens
     * @see java.awt.event.ActionEvent
     * @see java.awt.event.ActionListener
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent source = (JComponent) e.getSource();
        int operation = Integer.valueOf(source.getName());
        switch (operation) {
            case Client.NEXT:
                System.out.println("NEXT");
                break;
            case Client.PREVIOUS:
                System.out.println("PREV");
                break;
            case Client.DISCONNECT:
                disconnect();
                break;
            case Client.LANG:
                setNewLanguage(source);
                break;
            case Client.THEME:
                setNewTheme(source);
                break;
            case Client.APPLY:
                applyNewSettings();
                break;
            default:
                if (client.getTermin().length() < 3) {
                    break;
                } else {
                data = new Data();
                data.setOperation(operation);
                    data = client.getData(data);
                    data.setOperation(operation);
                    client.processQuery(data);
                    break;
                }
        }
    }

    public WindowAdapter closeAction() {
        WindowAdapter w = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }   
        };
        return w;
    }
    
    private void disconnect() {
                Data dicsonnectData = new Data();
                dicsonnectData.setOperation(JHelp.DISCONNECT);
                client.processQuery(dicsonnectData);
            }

    private void setNewLanguage(JComponent source) {
        JRadioButton radio = (JRadioButton) source;
        String file = radio.getText().toLowerCase();
        fileNameNewLang = langFiles.get(file) + CFG;
    }

    private void setNewTheme(JComponent source) {
        JComboBox box = (JComboBox) source;
        String theme = box.getSelectedItem().toString().toLowerCase();
        fileNameNewTheme = themeFiles.get(theme) + CFG;
    }

    private void applyNewSettings() {
        String fileName = client.getFullPath(client.userInterface.getProperty("file"));
        try (PrintStream writer = new PrintStream(fileName)) {
            client.userInterface.setProperty("lang", fileNameNewLang);
            client.userInterface.setProperty("theme", fileNameNewTheme);
            client.userInterface.list(writer);
            client.showWarningMessage(client, "Please, restart the application to apply new theme");

        } catch (IOException ex) {
            client.showErrorMessage(client, ex.getMessage());
        }
    }

    /**
     * Method for processing of {@link java.awt.event.KeyEvent} event. The
     * method invokes in case a user pushes any keyboard button with typed
     * symbol.
     *
     * @param e reference to {@link java.awt.event.KeyEvent} event what happens
     * @see java.awt.event.KeyEvent
     * @see java.awt.event.KeyListener
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Method for processing of {@link java.awt.event.KeyEvent} event. The
     * method invokes in case a user pushes but not releases any keyboard
     * button.
     *
     * @param e reference to {@link java.awt.event.KeyEvent} event what happens
     * @see java.awt.event.KeyEvent
     * @see java.awt.event.KeyListener
     */
    @Override
    public void keyPressed(KeyEvent e) {}

    /**
     * Method for processing of {@link java.awt.event.KeyEvent} event. The
     * method invokes in case a user releases any keyboard button.
     *
     * @param e reference to {@link java.awt.event.KeyEvent} event what happens
     * @see java.awt.event.KeyEvent
     * @see java.awt.event.KeyListener
     */
    @Override
    public void keyReleased(KeyEvent e) {}

    /**
     * This method are invoked when an object's text changed. This high-level
     * event is generated by an object (such as a TextComponent) when its text
     * changes.
     *
     * @param e reference to {@link java.awt.event.TextEvent} event what happens
     * @see java.awt.event.TextEvent
     * @see java.awt.event.TextListener
     */
    @Override
    public void textValueChanged(TextEvent e) {}
}
