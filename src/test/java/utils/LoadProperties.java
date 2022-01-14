package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoadProperties {
    LoadProperties loadProperties;
    static Properties properties = new Properties();

    private LoadProperties() {

    }

    public static void init() {
        try {
            // /Users/willierose/Desktop/ApiTesting/properties.properties
            properties.load(new FileInputStream(System.getProperty("user.dir") + "/properties.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getProperty(String key) {
        init();
        return properties.getProperty(key);
    }
}
