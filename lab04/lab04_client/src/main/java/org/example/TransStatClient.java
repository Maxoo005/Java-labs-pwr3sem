package org.example;

import org.example.util.Config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;


public class TransStatClient {

    public static void main(String[] args) throws IOException, InterruptedException {

        // pobranie danych z config.properties
        String baseUrl = Config.get("api.baseUrl");
        String endpoint = Config.get("api.endpoint.passengerWork");

        String url = baseUrl + endpoint;

        // klient HTTP
        HttpClient client = HttpClient.newHttpClient();

        // request HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // wyniki
        System.out.println("URL: " + url);
        System.out.println("Status code = " + response.statusCode());
        System.out.println("Body length = " + response.body().length());
        System.out.println("Body (początek):");
        System.out.println(response.body().substring(0, Math.min(1000, response.body().length())));

        Path out = Path.of("src/main/resources/sample_passenger_work.json");
        Files.writeString(out, response.body(), StandardCharsets.UTF_8);

        System.out.println("\n JSON zapisany do:");
        System.out.println(out.toAbsolutePath());
    }
}
