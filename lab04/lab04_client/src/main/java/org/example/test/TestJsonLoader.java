package org.example.test;

import org.example.util.Config;
import org.example.util.JsonLoader;

public class TestJsonLoader {

    public static void main(String[] args) {

        String fileName = Config.get("data.sampleFile");

        String json = JsonLoader.loadFromResources(fileName);

        System.out.println("Długość JSON-a: " + json.length());
        System.out.println("Początek JSON-a:");
        System.out.println(json.substring(0, Math.min(500, json.length())));
    }
}