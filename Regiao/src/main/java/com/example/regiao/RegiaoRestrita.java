package com.example.regiao;

public class RegiaoRestrita extends Region {
    private Region regiaoPrincipal;
    private boolean restrita;

    public RegiaoRestrita(double longitude, double latitude, String name, Region regiaoPrincipal, boolean restrita) {
        super(longitude, latitude, name);
        if (regiaoPrincipal != null) {
            this.regiaoPrincipal = regiaoPrincipal;
            this.restrita = restrita;
        } else {
            throw new IllegalArgumentException("A região principal não pode ser nula.");
        }
    }

    public Region getRegiaoPrincipal() {
        return regiaoPrincipal;
    }

    public void setRegiaoPrincipal(Region regiaoPrincipal) {
        this.regiaoPrincipal = regiaoPrincipal;
    }

    public boolean isRestrita() {
        return restrita;
    }

    public void setRestrita(boolean restrita) {
        this.restrita = restrita;
    }

    @Override
    public double calcularDistancia(double lat2, double lon2) {
        // Pode-se adicionar uma lógica específica para RegiaoRestrita se necessário
        return super.calcularDistancia(lat2, lon2);
    }
}

