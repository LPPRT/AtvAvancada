package com.example.atvavancada;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.regiao.Region;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Firebase {
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private Context context;

    public Firebase(Context context) {
        this.context = context;
    }

    /**
     * Método para salvar regiões no banco de dados Firebase.
     * @param filaRegioes Fila de regiões a serem salvas.
     */
    public void salvarRegioesNoBancoDeDados(Queue<Region> filaRegioes) {
        for (Region regiao : filaRegioes) {
            try {
                // Salvamento da região no banco de dados
                salvarRegiao(regiao);
            } catch (Exception e) {
                e.printStackTrace();
                exibirToast("Erro ao processar região: " + e.getMessage());
            }
        }
    }

    /**
     * Salva uma região no banco de dados Firebase.
     * @param regiao Região a ser salva.
     */
    private void salvarRegiao(Region regiao) {
        DatabaseReference regioesRef = FirebaseDatabase.getInstance().getReference("Regiao");
        String chaveRegiao = regioesRef.push().getKey();

        // Criar um mapa para os dados a serem salvos, excluindo latitudeAsDouble e longitudeAsDouble
        Map<String, Object> regiaoData = new HashMap<>();
        regiaoData.put("name", regiao.getName());
        regiaoData.put("latitude", regiao.getLatitude());
        regiaoData.put("longitude", regiao.getLongitude());
        regiaoData.put("user", regiao.getUser());
        regiaoData.put("timestamp", regiao.getTimestamp());

        regioesRef.child(chaveRegiao).setValue(regiaoData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Log.i("FIREBASE", "Região adicionada com sucesso!");
                    exibirToast("Região adicionada com sucesso!");
                } else {
                    Log.e("FIREBASE", "Erro ao salvar região: " + error.getMessage());
                    exibirToast("Erro ao salvar região no banco de dados. Tente novamente.");
                }
            }
        });
    }

    private void exibirToast(final String mensagem) {
        if (context == null) {
            Log.e("Firebase", "Contexto é nulo. Não é possível exibir o Toast.");
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isNearbyRegion(Queue<Region> regionsQueue, Region newRegion, double thresholdDistance) {
        for (Region region : regionsQueue) {
            double distance = region.calcularDistancia(Double.parseDouble(newRegion.getLatitude()), Double.parseDouble(newRegion.getLongitude()));
            if (distance < thresholdDistance) {
                return true;
            }
        }
        return false;
    }
}


