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
                List<Double> distanciasEntreMedidores = new ArrayList<>();

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
                        distanciasEntreMedidores.add(distancia);

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

                // Simulação de 10 passagens e Reconciliação de Dados
                List<List<Double>> leiturasTempo = new ArrayList<>();
                double[] desvioPadrao = new double[10];
                double[][] matrizIncidencia = new double[10][medidores.length - 1];

                for (int i = 0; i < 10; i++) {
                    List<Double> leituras = new ArrayList<>();
                    for (double distancia : distanciasEntreMedidores) {
                        double tempoDeslocamento = (distancia / (5 + (i * 5))) * 60.0; // Tempo de deslocamento por trecho
                        leituras.add(tempoDeslocamento);
                    }
                    leiturasTempo.add(leituras);
                    desvioPadrao[i] = 0.1 * leituras.stream().mapToDouble(Double::doubleValue).sum(); // Assumindo um desvio padrão arbitrário
                    // Preencher a matriz de incidência com valores arbitrários para o exemplo
                    for (int j = 0; j < medidores.length - 1; j++) {
                        matrizIncidencia[i][j] = (i % 2 == 0) ? 1 : -1;
                    }
                }

                Reconciliation reconciliation = new Reconciliation(
                        leiturasTempo.stream().flatMap(List::stream).mapToDouble(Double::doubleValue).toArray(),
                        desvioPadrao,
                        matrizIncidencia
                );
                double[] valoresReconciliados = reconciliation.getReconciledFlow();

                // Exibir os tempos de deslocamento e leituras de tempo convertidas, incluindo a velocidade
                StringBuilder resultadoFinal = new StringBuilder("Dados da " + nomeRota + ":\n");
                for (int i = 0; i < 10; i++) {
                    int velocidade = 5 + (i * 5); // Velocidade atual para o teste
                    resultadoFinal.append(String.format("Teste %d (Velocidade: %d km/h):\n", i + 1, velocidade));
                    for (int j = 0; j < distanciasEntreMedidores.size(); j++) {
                        double tempoDeslocamento = leiturasTempo.get(i).get(j);
                        resultadoFinal.append(String.format(
                                "Trecho %d: Tempo de deslocamento: %.2f \n",
                                j + 1, tempoDeslocamento
                        ));
                    }

                    // Exibir leituras de tempo no final do teste
                    resultadoFinal.append("Leituras de Tempo para este teste:\n");
                    for (double leituraTempo : leiturasTempo.get(i)) {
                        resultadoFinal.append(String.format("%.2f minutos\n", leituraTempo));
                    }
                    resultadoFinal.append("\n");
                }

                // Mostrar o resultado final uma única vez no log
                Log.i("MapsActivity", resultadoFinal.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
