package jhelp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Ella
 */
public class MainPane extends Panels {

    private JTextField termField;
    private JTextArea textArea;
    private JButton next;
    private JButton prev;
    private JButton add;
    private JButton delete;
    private JButton edit;

    public MainPane(Properties langConfig, Properties themeConfig, ActionListener listener) {
        super(langConfig, themeConfig, listener);
    }

    @Override
    protected void initPane() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 25));

        JPanel findPanel = initFindPanel();

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        JPanel dimentionsPanel = initDimentionsPanel();
        JPanel textAreaPanel = initTextAreaPanel();
        JPanel operationPanel = initOperationsPanel();
        JPanel editPanel = initEditPanel();
        JPanel exitPanel = initExitPanel();

        operationPanel.add(editPanel);
        operationPanel.add(exitPanel);

        centerPanel.add(dimentionsPanel, BorderLayout.NORTH);
        centerPanel.add(operationPanel, BorderLayout.EAST);
        centerPanel.add(textAreaPanel, BorderLayout.CENTER);

        add(findPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public String getTermin() {
        return termField.getText();
    }

    public String getDimention() {
        return textArea.getText();
    }

    public JButton getNextButton() {
        return next;
    }

    public JButton getPrevButton() {
        return prev;
    }

    public void showDefinition(String definition) {
        textArea.setText(definition);
    }

    public void showTermin(String trmin) {
        termField.setText(trmin);
    }

    public void cleanFields() {
        termField.setText(null);
        textArea.setText(null);
    }

    private JPanel initExitPanel() {
        JPanel exitPanel = new JPanel();
        exitPanel.setLayout(new BorderLayout());
        JButton exit = initButton(lang.getProperty("exit"), JHelp.DISCONNECT);
        exitPanel.add(exit, BorderLayout.SOUTH);
        return exitPanel;
    }

    private JPanel initEditPanel() {
        JPanel editPanel = new JPanel();
        GridLayout egl = new GridLayout(3, 1);
        editPanel.setLayout(egl);
        egl.setVgap(15);
        add = initButton(lang.getProperty("add"), JHelp.INSERT);
        edit = initButton(lang.getProperty("edit"), JHelp.UPDATE);
        delete = initButton(lang.getProperty("delete"), JHelp.DELETE);
        editPanel.add(add);
        editPanel.add(edit);
        editPanel.add(delete);
        return editPanel;
    }

    private JPanel initOperationsPanel() {
        JPanel operationPanel = new JPanel();
        operationPanel.setPreferredSize(new Dimension(110, 300));
        operationPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        GridLayout ogl = new GridLayout(3, 1);
        ogl.setVgap(15);
        operationPanel.setLayout(ogl);
        return operationPanel;
    }

    private JPanel initTextAreaPanel() {
        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textAreaPanel.add(textArea, BorderLayout.CENTER);
        return textAreaPanel;
    }

    private JPanel initFindPanel() {
        JPanel findPanel = new JPanel();
        JLabel term = new JLabel(lang.getProperty("termin") + ":");
        term.setFont(font);
        termField = new JTextField();
        JButton find = initButton(lang.getProperty("find"), JHelp.SELECT);
        find.setPreferredSize(new Dimension(90, 25));
        findPanel.setLayout(new BoxLayout(findPanel, BoxLayout.LINE_AXIS));
        findPanel.add(term);
        findPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        findPanel.add(termField);
        findPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        findPanel.add(find);
        return findPanel;
    }

    private JPanel initDimentionsPanel() {
        JPanel dimentionsPanel = new JPanel();
        dimentionsPanel.setLayout(new BorderLayout());
        dimentionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel dimentionsLabel = new JLabel(lang.getProperty("dimentions") + ":");
        dimentionsLabel.setFont(font);
        dimentionsPanel.add(dimentionsLabel, BorderLayout.NORTH);
        return dimentionsPanel;
    }
}
