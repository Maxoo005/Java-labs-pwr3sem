package org.example.test;

import org.example.model.PassengerWorkData;
import org.example.parser.PassengerWorkParser;
import org.example.util.Config;
import org.example.util.JsonLoader;

import java.util.List;

public class TestParser {

    public static void main(String[] args) {

        String fileName = Config.get("data.sampleFile");

        String json = JsonLoader.loadFromResources(fileName);

        List<PassengerWorkData> data = PassengerWorkParser.parse(json);

        for (int i = 0; i < Math.min(5, data.size()); i++) {
            System.out.println(data.get(i));
        }
    }
}