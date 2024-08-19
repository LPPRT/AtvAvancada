package com.example.regiao;

import java.util.Random;

public class Region {
    private String name;
    private String latitude;
    private String longitude;
    private int user;
    private long timestamp;
    private double latitudeAsDouble;
    private double longitudeAsDouble;

    public Region(double longitude, double latitude, String name) {
        this.longitude = String.valueOf(longitude);
        this.latitude = String.valueOf(latitude);
        this.latitudeAsDouble = latitude;
        this.longitudeAsDouble = longitude;
        this.name = name; // Adiciona o nome da região
        this.user = generateRandomUserId();
        this.timestamp = System.nanoTime();
    }

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getUser() {
        return user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getLatitudeAsDouble() {
        return latitudeAsDouble;
    }

    public double getLongitudeAsDouble() {
        return longitudeAsDouble;
    }

    public double calcularDistancia(double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em km
        double latDistance = Math.toRadians(lat2 - this.latitudeAsDouble);
        double lonDistance = Math.toRadians(lon2 - this.longitudeAsDouble);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitudeAsDouble)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distancia = R * c;
        return distancia * 1000; // Retorna a distância em metros
    }

    private int generateRandomUserId() {
        Random random = new Random();
        return random.nextInt(1000);
    }
}


