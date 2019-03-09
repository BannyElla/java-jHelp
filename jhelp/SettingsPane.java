package jhelp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Ella
 */
public class SettingsPane extends Panels {
    private JRadioButton engButton;
    private JRadioButton rusButton;
    private ButtonGroup group;
    private JComboBox comboBox;
    

    public SettingsPane(Properties langConfig, Properties themeConfig, ActionListener listener) {
        super(langConfig, themeConfig, listener);       
    }

    @Override
    protected void initPane() {   
        setLayout(new BorderLayout());

        JPanel langPanel = new JPanel();
        FlowLayout layoutPane = new FlowLayout(FlowLayout.LEFT);
        langPanel.setLayout(layoutPane);
        JLabel langLabel = initLabel(lang.getProperty("language")+":");

        group = new ButtonGroup();
        engButton = initRadioButton(lang.getProperty("eng"), true, JHelp.LANG);
        rusButton = initRadioButton(lang.getProperty("rus"), false, JHelp.LANG);
        rusButton.setEnabled(false);

        group.add(engButton);
        group.add(rusButton);
        langPanel.add(langLabel);
        langPanel.add(engButton);
        langPanel.add(rusButton);

        JPanel themePanel = new JPanel();
        themePanel.setLayout(layoutPane);
        JLabel themeLabel = initLabel(lang.getProperty("theme")+":");
        String[] dropDownList = {"Default", "Pink"};
        comboBox = new JComboBox(dropDownList);
        comboBox.setEditable(false);
        comboBox.setName(String.valueOf(JHelp.THEME));
        comboBox.addActionListener(listener);

        themePanel.add(themeLabel);
        themePanel.add(comboBox);
        
        JPanel applyPanel = new JPanel();
        applyPanel.setLayout(layoutPane);
        JButton applyButton = initButton(lang.getProperty("apply"), JHelp.APPLY);
        applyPanel.add(applyButton);
        applyButton.setActionCommand(getButtonAction());
        add(langPanel, BorderLayout.NORTH);
        add(themePanel, BorderLayout.CENTER);
        add(applyPanel, BorderLayout.SOUTH);
    }

    private JRadioButton initRadioButton(String text, boolean selected, int name) {
        JRadioButton button = new JRadioButton(text, selected);
        button.setFont(font);
        button.setName(String.valueOf(name));
        button.addActionListener(listener);
        button.setActionCommand(text.toLowerCase());
        return button;
    }

    private JLabel initLabel(String name) {
        JLabel label = new JLabel(name);
        label.setFont(font);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }
    
    private String getButtonAction() {
        return group.getSelection().getActionCommand()+".cfg";
    }
}
