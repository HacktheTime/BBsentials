package de.hype.bingonet.client.common.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class BingoCardManager {
    JsonObject recentBingoData;

    public BingoCardManager() throws IOException {
        checkForBingoData();
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

    public boolean isBingoTime() {
        Instant start = Instant.ofEpochSecond(recentBingoData.get("start").getAsLong());
        Instant end = Instant.ofEpochSecond(recentBingoData.get("end").getAsLong());
        Instant now = Instant.now();
        return start.minus(12, ChronoUnit.HOURS).isBefore(now) && end.plus(2, ChronoUnit.HOURS).isAfter(now);
    }

    public boolean hasSpiderRelicGoal() {
        return recentBingoData.getAsJsonArray("goals").asList().stream().anyMatch(o -> o.getAsJsonObject().get("id").getAsString().equals("spider_relic"));
    }
    public boolean hasFairySoulGoal() {
        return recentBingoData.getAsJsonArray("goals").asList().stream().anyMatch(o -> o.getAsJsonObject().get("id").getAsString().equals("fairy_souls"));
    }


}
