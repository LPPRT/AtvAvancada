package com.example.regiao;

public class SubRegiao extends Region {
    private Region regiaoPrincipal;

    public SubRegiao(double longitude, double latitude, String name, Region regiaoPrincipal) {
        super(longitude, latitude, name);
        if (regiaoPrincipal != null) {
            this.regiaoPrincipal = regiaoPrincipal;
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

    @Override
    public double calcularDistancia(double lat2, double lon2) {
        // Pode-se adicionar uma lógica específica para SubRegiao se necessário
        return super.calcularDistancia(lat2, lon2);
    }
}
