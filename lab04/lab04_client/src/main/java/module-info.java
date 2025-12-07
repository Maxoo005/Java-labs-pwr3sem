module lab04.client {
    requires java.base;
    requires java.net.http;
    requires org.json;


    exports org.example;
    exports org.example.util;
    exports org.example.service;
    exports org.example.model;
    exports org.example.test;

}