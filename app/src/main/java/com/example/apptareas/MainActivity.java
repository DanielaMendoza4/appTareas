package com.example.apptareas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTareas;
    private FloatingActionButton fabAgregarTarea;
    private TareaAdapter adapter;
    private TareasDbHelper dbHelper;

    private final List<Tarea> listaCompleta = new ArrayList<>();
    private final List<Tarea> listaFiltrada = new ArrayList<>();
    private String filtroActual = "Todas";

    private ActivityResultLauncher<Intent> lanzadorFormulario;
    private ActivityResultLauncher<Intent> lanzadorDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TareasDbHelper(this);

        rvTareas = findViewById(R.id.rvTareas);
        fabAgregarTarea = findViewById(R.id.fabAgregarTarea);

        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TareaAdapter(listaFiltrada, (tarea, posicion) -> {
            Intent intent = new Intent(MainActivity.this, DetalleTareaActivity.class);
            intent.putExtra(DetalleTareaActivity.EXTRA_TAREA_ID, tarea.getId());
            lanzadorDetalle.launch(intent);
        });
        rvTareas.setAdapter(adapter);

        lanzadorFormulario = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK) {
                        cargarTareas();
                    }
                });

        lanzadorDetalle = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK) {
                        cargarTareas();
                    }
                });

        fabAgregarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                lanzadorFormulario.launch(intent);
            }
        });

        ((Button) findViewById(R.id.btnTodas)).setOnClickListener(v -> aplicarFiltro("Todas"));
        ((Button) findViewById(R.id.btnPendientes)).setOnClickListener(v -> aplicarFiltro(Tarea.ESTADO_PENDIENTE));
        ((Button) findViewById(R.id.btnEnProgreso)).setOnClickListener(v -> aplicarFiltro(Tarea.ESTADO_EN_PROGRESO));
        ((Button) findViewById(R.id.btnCompletadas)).setOnClickListener(v -> aplicarFiltro(Tarea.ESTADO_COMPLETADA));

        cargarTareas();
    }

    private void cargarTareas() {
        listaCompleta.clear();
        listaCompleta.addAll(dbHelper.obtenerTodas());
        aplicarFiltro(filtroActual);
    }

    private void aplicarFiltro(String filtro) {
        filtroActual = filtro;
        listaFiltrada.clear();

        if (filtro.equals("Todas")) {
            listaFiltrada.addAll(listaCompleta);
        } else {
            for (Tarea tarea : listaCompleta) {
                if (tarea.getEstado().equals(filtro)) listaFiltrada.add(tarea);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) dbHelper.close();
        super.onDestroy();
    }
}
