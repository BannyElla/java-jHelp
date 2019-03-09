package jhelp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Ella
 */
public class Configuration extends Properties {

    private String fileSeparator = File.separator;

    public Configuration(String fileName) throws FileNotFoundException, IOException {
        String filePath = System.getProperty("user.dir") + fileSeparator + fileName;
        try (FileReader reader = new FileReader(new File(filePath))) {
            load(reader);
            
        }
    }
}
