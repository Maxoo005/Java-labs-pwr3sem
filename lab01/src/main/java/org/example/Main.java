package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random r = new Random();

        List<Sound> toys = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int type = r.nextInt(3);
            switch (type) {
                case 0 -> toys.add(new Car());
                case 1 -> toys.add(new Doll());
                default -> toys.add(new Duck());
            }
        }

        for (Sound toy : toys) {
            System.out.println(toy.sound());
        }
    }
}
