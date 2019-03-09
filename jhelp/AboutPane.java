package jhelp;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Ella
 */
public class AboutPane extends Panels {

    private String file;
    private String text;

    public AboutPane(Properties langConfig, Properties themeConfig, ActionListener listener) {
        super(langConfig, themeConfig, listener);

    }

    @Override
    protected void initPane() {
        file = "about_ru.cfg";
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(lang.getProperty("appDescription"));
        label.setFont(font);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextArea textArea = new JTextArea();
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        getText(file);

        textArea.setText(text);
        JScrollPane scroll = new JScrollPane(textArea);

        panel.add(label, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
    }

    public void getText(String fileName) {
        String path = System.getProperty("user.dir") + System.getProperty("file.separator") + JHelp.CONFIG_PATH + fileName;
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "Cp1251")) {
            BufferedReader br = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            sb.deleteCharAt(sb.length() - 1);

            br.close();
            reader.close();
            text = sb.toString();
        } catch (FileNotFoundException fe) {
            text = "Content is not found";
        } catch (IOException ie) {
            text = "Content is not found";
        }
    }
}
