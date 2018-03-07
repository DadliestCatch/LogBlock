package LogBlock;

import java.io.*;
import java.util.Properties;

public class Config {

    static Properties prop = new Properties();
    static OutputStream output = null;
    static File config = new File("mods/LogBlock/config.properties");

    public static boolean readConfig() {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            try {
                input = new FileInputStream(config);
            } catch (FileNotFoundException e) {
                createConfig();
                return false;
            }

            prop.load(input);

            Main.host = prop.getProperty("host");
            Main.database = prop.getProperty("database");
            Main.dbuser = prop.getProperty("dbuser");
            Main.dbpass = prop.getProperty("dbpass");
            Main.dbtable = prop.getProperty("dbtable", "blockbreaks");
            Main.queueThreads = Integer.parseInt(prop.getProperty("queueThreads", "2"));
            Main.queueShutdownTimeout = Long.parseLong(prop.getProperty("queueShutdownTimeout", "60"));
        } catch (IOException ex) {
            System.out.println("[LogBlock/WARN]: Disabled! Configuration error. " + ex.getMessage());
        }
        try {
            input.close();
        } catch (IOException e) {
            System.out.println("[LogBlock/WARN]: Disabled! Configuration error. " + e.getMessage());
            return false;
        }
        System.out.println("[LogBlock/INFO]: Config: OK");
        return true;
    }

    public static void createConfig() {
        try {
            config.getParentFile().mkdirs();

            output = new FileOutputStream(config);

            prop.setProperty("host", "localhost");
            prop.setProperty("database", "logblock");
            prop.setProperty("dbuser", "username");
            prop.setProperty("dbpass", "password");
            prop.setProperty("dbtable", "blocks");
            prop.setProperty("queueThreads", "2");
            prop.setProperty("queueShutdownTimeout", "60");

            prop.store(output, "LogBlock Configuration");

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                    System.out.println("[LogBlock/WARN]: Configuration file created. Please edit and restart server");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}

