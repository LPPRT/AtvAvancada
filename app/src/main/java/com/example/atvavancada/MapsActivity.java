package com.example.atvavancada;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeoApiContext geoApiContext;

    private LatLng[] rota1 = {
            new LatLng(-21.2399, -44.9984),
            new LatLng(-21.237794, -44.997201),
            new LatLng(-21.235641, -44.997237),
            new LatLng(-21.233224, -44.997392),
            new LatLng(-21.232351, -44.995683),
            new LatLng(-21.2340, -44.9946)
    };

    private LatLng[] rota2 = {
            new LatLng(-21.2399, -44.9984),
            new LatLng(-21.239425, -44.996515),
            new LatLng(-21.238828, -44.995182),
            new LatLng(-21.237465, -44.994641),
            new LatLng(-21.235725, -44.994727),
            new LatLng(-21.2340, -44.9946)
    };

    private LatLng[] rota3 = {
            new LatLng(-21.2399, -44.9984),
            new LatLng(-21.238283, -44.993868),
            new LatLng(-21.237377, -44.990534),
            new LatLng(-21.236874, -44.990615),
            new LatLng(-21.234854, -44.991272),
            new LatLng(-21.2340, -44.9946)
    };

    private List<Polyline> polylines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyBljF-txtcXnmNgMigJ5PYBfKBEQp6SZLM")
                .build();

        Button buttonRota1 = findViewById(R.id.button_rota1);
        Button buttonRota2 = findViewById(R.id.button_rota2);
        Button buttonRota3 = findViewById(R.id.button_rota3);

        buttonRota1.setOnClickListener(v -> desenharRota(rota1, "Rota 1"));
        buttonRota2.setOnClickListener(v -> desenharRota(rota2, "Rota 2"));
        buttonRota3.setOnClickListener(v -> desenharRota(rota3, "Rota 3"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rota1[0], 12)); // Inicialmente focando no início da rota 1
    }

    private void desenharRota(LatLng[] medidores, String nomeRota) {
        if (medidores == null || medidores.length == 0) {
            Log.e("MapsActivity", "Rota vazia ou nula. Não é possível desenhar a rota.");
            return;
        }

        // Limpar polylines anteriores
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();

        new Thread(() -> {
            try {
                double totalDistancia = 0.0;

                for (int i = 0; i < medidores.length - 1; i++) {
                    LatLng origem = medidores[i];
                    LatLng destino = medidores[i + 1];

                    // Chamar a API Directions para obter a rota
                    DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                            .origin(new com.google.maps.model.LatLng(origem.latitude, origem.longitude))
                            .destination(new com.google.maps.model.LatLng(destino.latitude, destino.longitude))
                            .await();

                    if (result.routes != null && result.routes.length > 0) {
                        DirectionsRoute route = result.routes[0];
                        double distancia = route.legs[0].distance.inMeters / 1000.0; // Distância em quilômetros
                        totalDistancia += distancia;

                        runOnUiThread(() -> {
                            PolylineOptions polylineOptions = new PolylineOptions();
                            for (com.google.maps.model.LatLng point : route.overviewPolyline.decodePath()) {
                                polylineOptions.add(new LatLng(point.lat, point.lng));
                            }
                            Polyline polyline = mMap.addPolyline(polylineOptions);
                            polylines.add(polyline); // Armazenar a polyline adicionada

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origem, 12));
                        });
                    }
                }

                // Calcular e exibir tempos para diferentes velocidades
                StringBuilder resultadoFinal = new StringBuilder("Dados da " + nomeRota + ":\n");
                for (int velocidade = 5; velocidade <= 50; velocidade += 5) {
                    double tempoDeslocamento = (totalDistancia / velocidade) * 60.0; // Tempo em minutos
                    String mensagem = String.format(
                            "Velocidade: %d km/h, Tempo de deslocamento: %.2f minutos",
                            velocidade, tempoDeslocamento
                    );
                    resultadoFinal.append(mensagem).append("\n");
                }

                // Mostrar o resultado final uma única vez
                System.out.println(resultadoFinal.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    
}
