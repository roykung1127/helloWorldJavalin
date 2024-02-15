package app.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FlatFile {

    static String configPath = "conf";
    static String configFile = "config.dat";
    static String absolutePath = configPath + File.separator + configFile;

    public static void setData(String data) {
        init();
        try(FileWriter fileWriter = new FileWriter(absolutePath)) {
            String fileContent = data;
            fileWriter.write(fileContent);
            fileWriter.close();
        } catch (IOException e) {
            // Cxception handling
        }
    }

    public static String getData() {
        String s = "0";
        try {
            s = Files.readString(Paths.get(absolutePath), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            // Exception handling
        } catch (IOException e) {
            // Exception handling
        }
        return s;
    }

    private static void init() {
        try {
            File dir = new File(configPath);
            if (dir.mkdirs()) {
                Log.info("Folder created");
            } else {
                Log.info("Folder existed");
            }
            File myObj = new File(dir, configFile);
            if (myObj.createNewFile()) {
                Log.info("File created: " + myObj.getName());
            } else {
                Log.info("File already exists.");
            }
        } catch (IOException e) {
            Log.info("An error occurred.");
            e.printStackTrace();
        }
    }
}