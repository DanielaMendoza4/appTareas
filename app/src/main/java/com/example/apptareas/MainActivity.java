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
    private TareaAdapter adapter; // Tu adaptador del RecyclerView
    // private SQLiteHelper dbHelper; // Tu manejador de SQLite (pendiente)

    private final List<Tarea> listaCompleta = new ArrayList<>();
    private final List<Tarea> listaFiltrada = new ArrayList<>();
    private String filtroActual = "Todas";

    private ActivityResultLauncher<Intent> lanzadorFormulario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Enlazar componentes del XML con Java
        rvTareas = findViewById(R.id.rvTareas);
        fabAgregarTarea = findViewById(R.id.fabAgregarTarea);

        // 2. Configurar el formato de la lista (Vertical) y el adapter
        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TareaAdapter(listaFiltrada, (tarea, posicion) -> {
            // Aquí puedes abrir "Detalles de la Tarea" pasando la 'tarea'
        });
        rvTareas.setAdapter(adapter);

        // 3. Registrar el resultado del formulario (para recibir la tarea nueva)
        lanzadorFormulario = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                        Tarea nuevaTarea = (Tarea) resultado.getData()
                                .getSerializableExtra(FormularioActivity.EXTRA_TAREA);
                        if (nuevaTarea != null) {
                            listaCompleta.add(0, nuevaTarea);
                            aplicarFiltro(filtroActual);
                        }
                    }
                });

        // 4. Evento para abrir el formulario al presionar '+'
        fabAgregarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                lanzadorFormulario.launch(intent);
            }
        });

        // 5. Eventos de los filtros
        ((Button) findViewById(R.id.btnTodas)).setOnClickListener(v -> aplicarFiltro("Todas"));
        ((Button) findViewById(R.id.btnPendientes)).setOnClickListener(v -> aplicarFiltro(Tarea.ESTADO_PENDIENTE));
        ((Button) findViewById(R.id.btnEnProgreso)).setOnClickListener(v -> aplicarFiltro(Tarea.ESTADO_EN_PROGRESO));
        ((Button) findViewById(R.id.btnCompletadas)).setOnClickListener(v -> aplicarFiltro(Tarea.ESTADO_COMPLETADA));

        // 6. Cargar tareas
        cargarTareas();
    }

    private void cargarTareas() {
    /* Cuando conectes SQLite, reemplaza esto por:
       listaCompleta.clear();
       listaCompleta.addAll(dbHelper.obtenerTodasLasTareas());
    */
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
}