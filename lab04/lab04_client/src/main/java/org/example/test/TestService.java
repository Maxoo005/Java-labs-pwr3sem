package org.example.test;

import org.example.model.PassengerWorkData;
import org.example.service.PassengerWorkService;

import java.io.IOException;
import java.util.List;

public class TestService {

    public static void main(String[] args) {
        PassengerWorkService service = new PassengerWorkService();

        // 1. Test z pliku sample
        System.out.println("=== Dane z pliku sample ===");
        List<PassengerWorkData> fromFile = service.loadFromSampleFile();
        fromFile.stream().limit(5).forEach(System.out::println);

        // 2. Test z API
        try {
            System.out.println("\n=== Dane z API TranStat ===");
            List<PassengerWorkData> fromApi = service.loadFromApi();
            fromApi.stream().limit(5).forEach(System.out::println);
        } catch (IOException | InterruptedException e) {
            System.out.println("Nie udało się pobrać danych z API: " + e.getMessage());
        }
    }
}
