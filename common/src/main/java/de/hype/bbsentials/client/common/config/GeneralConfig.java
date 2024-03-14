package de.hype.bbsentials.client.common.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static de.hype.bbsentials.client.common.client.BBsentials.generalConfig;


public class GeneralConfig extends BBsentialsConfig {

    public transient int apiVersion = 1;
    public String[] bbsentialsRoles = {""};
    public boolean useNumCodes = true;
    public boolean doGuildChatCustomMenu = true;
    public boolean doAllChatCustomMenu = true;
    public boolean doPartyChatCustomMenu = true;
    public boolean doDesktopNotifications = false;
    public String nickname = "";
    public String notifForMessagesType = "NONE";
    public JsonObject recentBingoData = null;
    public Set<String> profileIds = new HashSet<>();

    public GeneralConfig() {
        super(1);
        doInit();
    }


    public static boolean isBingoTime() {
        Instant start = Instant.ofEpochSecond(generalConfig.recentBingoData.get("start").getAsLong());
        Instant end = Instant.ofEpochSecond(generalConfig.recentBingoData.get("end").getAsLong());
        Instant now = Instant.now();
        return start.minus(12, ChronoUnit.HOURS).isBefore(now) && end.plus(2, ChronoUnit.HOURS).isAfter(now);
    }

    public boolean hasBBRoles(String roleName) {
        if (roleName == null) return true;
        if (roleName.isEmpty()) return true;
        for (String role : bbsentialsRoles) {
            if (role.equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }

    public String getMCUUID() {
        return EnvironmentCore.utils.getMCUUID().replace("-", "");
    }

    public String getUsername() {
        return EnvironmentCore.utils.getUsername();
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public void setDefault() {

    }

    public void onInit() {
        try {
            checkForBingoData();
        } catch (IOException e) {
            Chat.sendPrivateMessageToSelfError("Error Trying to load Bingo Data.");
        }
    }

    public JsonObject checkForBingoData() throws IOException {
        ZonedDateTime hypixelDate = Instant.now().atZone(ZoneId.of("America/Chicago")).plusDays(3);
        if (recentBingoData != null && recentBingoData.get("id").getAsInt() == hypixelDate.getMonthValue() * (hypixelDate.getYear() - 2022))
            return recentBingoData;
        String apiUrl = "https://api.hypixel.net/v2/resources/skyblock/bingo";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Optional: Setze Verbindungseigenschaften (z.B., Timeout, Methode)
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Lese die API-Antwort
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String apiResponse = scanner.hasNext() ? scanner.next() : "";

            // Konvertiere die API-Antwort in ein JsonObject
            recentBingoData = JsonParser.parseString(apiResponse).getAsJsonObject();
            if (recentBingoData.get("success").getAsBoolean() && !recentBingoData.get("goals").isJsonNull() && !recentBingoData.getAsJsonArray("goals").isEmpty()) {
                return recentBingoData;
            }
            throw new IOException("Invalid Bingo Data");
        }
        else {
            throw new IOException("Error Requesting from API. HTTP-Statuscode: " + responseCode);
        }
    }
}
