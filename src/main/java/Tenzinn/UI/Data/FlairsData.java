package Tenzinn.UI.Data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FlairsData {

    private static JsonObject flairData;

    private static ArrayList<String> keys = new ArrayList<>();
    private static ArrayList<String> values = new ArrayList<>();

    public static void loadContent() {
        try {
            InputStream inputStream = FlairsData.class.getResourceAsStream("/Common/UI/Data/stats.json");

            System.out.println("FLAIRSDATA.JAVA: Cargando data de stats.json");

            if (inputStream == null) return;

            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            flairData = gson.fromJson(reader, JsonObject.class);

            reader.close();

            keys.clear();
            values.clear();

            for (var entry : flairData.entrySet()) {
                keys.add(entry.getKey());
                values.add(entry.getValue().getAsString());
            }

            System.out.println("FLAIRSDATA.JAVA: Información cargada en stats.json — " + keys.size() + " entradas.");
        } catch (Exception e) {
            System.err.println("Error al cargar stats.json: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static ArrayList<String> getKeys() { return keys; }
    public static ArrayList<String> getValues() { return values; }
}