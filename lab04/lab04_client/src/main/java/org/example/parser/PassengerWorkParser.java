package org.example.parser;

import org.example.model.PassengerWorkData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PassengerWorkParser {

    public static List<PassengerWorkData> parse(String json) {
        JSONArray array = new JSONArray(json);
        List<PassengerWorkData> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.getJSONObject(i);

            int id = o.getInt("id");
            String port = o.getString("mapParam");
            int year = Integer.parseInt(o.getString("year"));
            String month = o.getString("dataXPl");
            double importValue = o.getDouble("import");
            double exportValue = o.getDouble("export");

            list.add(new PassengerWorkData(
                    id,
                    port,
                    year,
                    month,
                    importValue,
                    exportValue
            ));
        }

        return list;
    }
}