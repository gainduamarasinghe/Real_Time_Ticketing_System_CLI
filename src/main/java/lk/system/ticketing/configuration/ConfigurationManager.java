package lk.system.ticketing.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationManager {

    private static final String CONFIG_JSON_FILE = "configuration.json";

    // Method to save the configuration using JSON format with BufferedWriter
    public static void saveConfigToJson(Configuration configuration) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_JSON_FILE))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(configuration, writer);
            System.out.println("Configuration saved as JSON successfully.");
        } catch (IOException e) {
            System.out.println("Error saving configuration to JSON: " + e.getMessage());
        }
    }
}
