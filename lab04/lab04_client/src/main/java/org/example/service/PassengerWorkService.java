package org.example.service;

import org.example.model.PassengerWorkData;
import org.example.parser.PassengerWorkParser;
import org.example.util.Config;
import org.example.util.JsonLoader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PassengerWorkService {

    private final HttpClient client;
    private final String baseUrl;
    private final String endpointPath;
    private final String sampleFileName;

    public PassengerWorkService() {
        this.client = HttpClient.newHttpClient();
        this.baseUrl = Config.get("api.baseUrl");
        this.endpointPath = Config.get("api.endpoint.passengerWork");
        this.sampleFileName = Config.get("data.sampleFile");
    }

    //Wczytanie danych z pliku JSON
    public List<PassengerWorkData> loadFromSampleFile() {
        String json = JsonLoader.loadFromResources(sampleFileName);
        return PassengerWorkParser.parse(json);
    }

    //Wczytanie danych z API TranStat
    public List<PassengerWorkData> loadFromApi() throws IOException, InterruptedException {
        String url = baseUrl + endpointPath;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Błąd API TranStat: HTTP " + response.statusCode());
        }

        return PassengerWorkParser.parse(response.body());
    }
}