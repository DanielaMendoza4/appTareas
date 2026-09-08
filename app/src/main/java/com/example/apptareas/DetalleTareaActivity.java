package com.example.apptareas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.apptareas.database.TareasDbHelper;

public class DetalleTareaActivity extends AppCompatActivity {

    public static final String EXTRA_TAREA_ID = "extra_tarea_id";

    private TareasDbHelper dbHelper;
    private long tareaId = -1;

    private TextView tvTitulo, tvDescripcion, tvEstado, tvFechaCreacion, tvFechaVencimiento, tvUsuario;
    private View vIndicador;
    private ActivityResultLauncher<Intent> lanzadorEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_tarea);

        dbHelper = new TareasDbHelper(this);
        tareaId = getIntent().getLongExtra(EXTRA_TAREA_ID, -1);
        if (tareaId == -1) {
            finish();
            return;
        }

        tvTitulo = findViewById(R.id.tvDetalleTitulo);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        tvEstado = findViewById(R.id.tvDetalleEstado);
        tvFechaCreacion = findViewById(R.id.tvDetalleFechaCreacion);
        tvFechaVencimiento = findViewById(R.id.tvDetalleFechaVencimiento);
        tvUsuario = findViewById(R.id.tvDetalleUsuario);
        vIndicador = findViewById(R.id.vIndicadorEstadoDetalle);

        lanzadorEditar = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK) {
                        cargarDetalle();
                    }
                });

        ImageButton btnRegresar = findViewById(R.id.btnRegresarDetalle);
        btnRegresar.setOnClickListener(v -> finish());

        Button btnEditar = findViewById(R.id.btnEditarDesdeDetalle);
        btnEditar.setOnClickListener(v -> {
            Intent i = new Intent(DetalleTareaActivity.this, FormularioActivity.class);
            i.putExtra(FormularioActivity.EXTRA_TAREA_ID, tareaId);
            lanzadorEditar.launch(i);
        });

        Button btnEliminar = findViewById(R.id.btnEliminarDesdeDetalle);
        btnEliminar.setOnClickListener(v -> confirmarEliminar());

        cargarDetalle();
    }

    private void cargarDetalle() {
        Tarea t = dbHelper.obtenerPorId(tareaId);
        if (t == null) {
            Toast.makeText(this, "Tarea no encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvTitulo.setText(t.getTitulo());
        tvDescripcion.setText(t.getDescripcion() == null || t.getDescripcion().isEmpty()
                ? "Sin descripción" : t.getDescripcion());
        tvEstado.setText(t.getEstado());
        tvFechaCreacion.setText(FechaUtils.paraUICreacion(t.getFechaCreacion()));
        tvFechaVencimiento.setText(FechaUtils.paraUIVencimiento(t.getFechaVencimiento()));
        String usuario = t.getUsuarioAsignado();
        tvUsuario.setText(usuario == null || usuario.isEmpty() ? "Sin asignar" : usuario);

        int color;
        switch (t.getEstado()) {
            case Tarea.ESTADO_PENDIENTE:
                color = Color.parseColor("#E74C3C");
                break;
            case Tarea.ESTADO_EN_PROGRESO:
                color = Color.parseColor("#F5A623");
                break;
            default:
                color = Color.parseColor("#2ECC71");
                break;
        }
        vIndicador.setBackgroundColor(color);
        setResult(RESULT_OK);
    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Seguro que quieres eliminar esta tarea?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    dbHelper.eliminar(tareaId);
                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) dbHelper.close();
        super.onDestroy();
    }
}
