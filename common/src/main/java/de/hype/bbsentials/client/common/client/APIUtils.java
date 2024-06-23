package de.hype.bbsentials.client.common.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIUtils {
    public static String getUsernameFromUUID(String uuid) {
        try {
            // Constructing the URL with the provided UUID
            String apiUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
            URL url = new URL(apiUrl);

            // Opening a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Setting up the request method
            connection.setRequestMethod("GET");

            // Reading the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the JSON response to get the username
            String jsonResponse = response.toString();
            int startIndex = jsonResponse.indexOf("\"name\"") + 8;
            int endIndex = jsonResponse.indexOf("\"", startIndex + 1);

            return jsonResponse.substring(startIndex, endIndex);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed (e.g., logging or returning an error message)
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    public static String getMcUUIDbyUsername(String username) {
        try {
            String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
            URL mojangAPI = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) mojangAPI.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                String uuid = response.toString().split("\"")[3];
                return uuid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
