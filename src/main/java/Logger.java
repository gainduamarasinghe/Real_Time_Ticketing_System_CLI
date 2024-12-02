import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "ticketing_system.txt";
    private static BufferedWriter writer;

    static {
        try {
            writer = new BufferedWriter(new FileWriter(LOG_FILE, true)); // Append mode
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    // Synchronized method to log messages
    public static synchronized void log(String message) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logMessage = timestamp + " - " + message;
            if (writer != null) {
                writer.write(logMessage);
                writer.newLine();
                writer.flush(); // Ensure immediate write to the file
            }
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

    // Method to close the logger (e.g., when the program exits)
    public static synchronized void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to close logger: " + e.getMessage());
        }
    }
}

