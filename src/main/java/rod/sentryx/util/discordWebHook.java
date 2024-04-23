package rod.sentryx.util;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class discordWebHook {
    // add webhook url here between the ""
    public static String webhookUrl = "";

    public static void sendWebhook(String message) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Set request method
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            // Enable output and set content length
            con.setDoOutput(true);
            byte[] postData = message.getBytes(StandardCharsets.UTF_8);
            con.setRequestProperty("Content-Length", String.valueOf(postData.length));

            // Write message to output stream
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
            }

            // Check response code
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Webhook message sent successfully.");
            } else {
                System.err.println("Failed to send webhook message. Response code: " + responseCode);
            }

            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

