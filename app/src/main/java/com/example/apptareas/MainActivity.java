package com.example.apptareas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTareas;
    private FloatingActionButton fabAgregarTarea;
    // Private TareaAdapter adapter; // Tu adaptador del RecyclerView
    // Private SQLiteHelper dbHelper; // Tu manejador de SQLite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Enlazar componentes del XML con Java
        rvTareas = findViewById(R.id.rvTareas);
        fabAgregarTarea = findViewById(R.id.fabAgregarTarea);

        // 2. Configurar el formato de la lista (Vertical)
        rvTareas.setLayoutManager(new LinearLayoutManager(this));

        // 3. Evento para abrir el formulario al presionar '+'
        fabAgregarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                startActivity(intent);
            }
        });

        // 4. Cargar tareas desde SQLite
        cargarTareas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar la lista cada vez que regresemos a esta pantalla
        cargarTareas();
    }

    private void cargarTareas() {
        /* Aquí realizarás la consulta SELECT a SQLite
           Ejemplo de estructura:
           List<Tarea> listaTareas = dbHelper.obtenerTodasLasTareas();
           adapter = new TareaAdapter(listaTareas, this);
           rvTareas.setAdapter(adapter);
        */
    }
}