package chatfx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author frair
 */
public class ChatProperties {

    synchronized public static String readProperties(String propertyName) {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("resources/pap.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            return prop.getProperty(propertyName);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return null;
    }
}
