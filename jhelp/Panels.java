package jhelp;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Ella
 */
public abstract class Panels extends JPanel {

    protected ActionListener listener;
    protected Properties theme;
    protected Properties lang;
    protected Font font;
    protected Color colorButton;
    protected Color colorPane;

    public Panels(Properties langConfig, Properties themeConfig, ActionListener listener) {
        this.theme = themeConfig;
        this.lang = langConfig;
        this.listener = listener;
        initThemeApplication();
        initPane();
    }
    
 /**
  *  Initialization of the panel
  */
    abstract protected void initPane();

    protected JButton initButton(String text, int name) {
        JButton button = new JButton(text);
        button.setName(String.valueOf(name));
        button.setFont(font);
        button.setBackground(colorButton);
        button.addActionListener(listener);
        return button;
    }

    private void initThemeApplication() {
        font = createFont("font", "fontStyle", "fontSize");
        colorButton = createColor("buttonColorR", "buttonColorG", "buttonColorB");
        colorPane = createColor("bgrR", "bgrG", "bgrB");
    }

    private Font createFont(String font, String fontStyle, String fontSize) {
        String fnt = theme.getProperty(font);
        String stl = theme.getProperty(fontStyle);
        String sz = theme.getProperty(fontSize);
        if (fnt == null || stl == null || sz == null
                || fnt.length() == 0 || stl.length() == 0 || sz.length() == 0) {
            return null;
        }
        int style = Integer.valueOf(stl);
        int size = Integer.valueOf(sz);
        return new Font(fnt, style, size);
    }

    private Color createColor(String r, String g, String b) {
        String sr = theme.getProperty(r);
        String sg = theme.getProperty(g);
        String sb = theme.getProperty(b);
        if (sr == null || sg == null || sb == null
                || sr.length() == 0 || sg.length() == 0 || sb.length() == 0) {
            return null;
        }

        int colorR = Integer.valueOf(sr);
        int colorG = Integer.valueOf(sg);
        int colorB = Integer.valueOf(sb);
        return new Color(colorR, colorG, colorB);
    }
}
