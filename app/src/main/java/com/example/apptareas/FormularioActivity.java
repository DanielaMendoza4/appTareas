package com.example.apptareas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class FormularioActivity extends AppCompatActivity {

    public static final String EXTRA_TAREA = "extra_tarea";
    public static final String EXTRA_TAREA_ID = "extra_tarea_id";

    private EditText edtTitulo, edtDescripcion, edtUsuario;
    private Button btnPendiente, btnProgreso, btnCompletada, btnSeleccionarFecha, btnGuardarTarea;
    private TextView txtFechaCreacion;

    private String estadoSeleccionado = Tarea.ESTADO_PENDIENTE;
    private String fechaCreacionISO = "";
    private String fechaVencimientoISO = "";

    private TareasDbHelper dbHelper;
    private long tareaIdEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_tarea);

        dbHelper = new TareasDbHelper(this);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtUsuario = findViewById(R.id.edtUsuario);
        txtFechaCreacion = findViewById(R.id.txtFechaCreacion);
        btnPendiente = findViewById(R.id.btnPendiente);
        btnProgreso = findViewById(R.id.btnProgreso);
        btnCompletada = findViewById(R.id.btnCompletada);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea);

        tareaIdEditar = getIntent().getLongExtra(EXTRA_TAREA_ID, -1);

        if (tareaIdEditar != -1) {
            Tarea existente = dbHelper.obtenerPorId(tareaIdEditar);
            if (existente == null) {
                Toast.makeText(this, "Tarea no encontrada", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            edtTitulo.setText(existente.getTitulo());
            edtDescripcion.setText(existente.getDescripcion());
            edtUsuario.setText(existente.getUsuarioAsignado());
            estadoSeleccionado = existente.getEstado();
            fechaCreacionISO = existente.getFechaCreacion();
            fechaVencimientoISO = existente.getFechaVencimiento();
            txtFechaCreacion.setText(FechaUtils.paraUICreacion(fechaCreacionISO));
            btnSeleccionarFecha.setText(FechaUtils.paraUIVencimiento(fechaVencimientoISO));
            btnGuardarTarea.setText("Actualizar Tarea");
            marcarEstadoSeleccionado(estadoSeleccionado);
        } else {
            fechaCreacionISO = FechaUtils.hoyISO();
            txtFechaCreacion.setText(FechaUtils.paraUICreacion(fechaCreacionISO));
            marcarEstadoSeleccionado(Tarea.ESTADO_PENDIENTE);
        }

        btnPendiente.setOnClickListener(v -> marcarEstadoSeleccionado(Tarea.ESTADO_PENDIENTE));
        btnProgreso.setOnClickListener(v -> marcarEstadoSeleccionado(Tarea.ESTADO_EN_PROGRESO));
        btnCompletada.setOnClickListener(v -> marcarEstadoSeleccionado(Tarea.ESTADO_COMPLETADA));

        btnSeleccionarFecha.setOnClickListener(v -> mostrarSelectorDeFecha());
        findViewById(R.id.btnCalendario).setOnClickListener(v -> mostrarSelectorDeFecha());
        findViewById(R.id.btnRegresar).setOnClickListener(v -> finish());
        btnGuardarTarea.setOnClickListener(v -> guardarTarea());
    }

    private void marcarEstadoSeleccionado(String estado) {
        estadoSeleccionado = estado;
        resetearBoton(btnPendiente);
        resetearBoton(btnProgreso);
        resetearBoton(btnCompletada);

        Button botonActivo = estado.equals(Tarea.ESTADO_PENDIENTE) ? btnPendiente
                : estado.equals(Tarea.ESTADO_EN_PROGRESO) ? btnProgreso : btnCompletada;

        botonActivo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#16A9C0")));
        botonActivo.setTextColor(Color.WHITE);
    }

    private void resetearBoton(Button boton) {
        boton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EEEEEE")));
        boton.setTextColor(Color.parseColor("#333333"));
    }

    private void mostrarSelectorDeFecha() {
        Calendar hoy = Calendar.getInstance();
        new DatePickerDialog(this, (view, anio, mes, dia) -> {
            fechaVencimientoISO = FechaUtils.isoDe(anio, mes, dia);
            btnSeleccionarFecha.setText(FechaUtils.paraUIVencimiento(fechaVencimientoISO));
        }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void guardarTarea() {
        String titulo = edtTitulo.getText().toString().trim();
        String descripcion = edtDescripcion.getText().toString().trim();
        String usuario = edtUsuario.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "Ingresa un título para la tarea", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fechaVencimientoISO.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha de vencimiento", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tareaIdEditar != -1) {
            Tarea actualizada = new Tarea(tareaIdEditar, titulo, descripcion,
                    estadoSeleccionado, fechaCreacionISO, fechaVencimientoISO, usuario);
            dbHelper.actualizar(actualizada);
        } else {
            Tarea nueva = new Tarea(0, titulo, descripcion,
                    estadoSeleccionado, fechaCreacionISO, fechaVencimientoISO, usuario);
            dbHelper.insertar(nueva);
        }

        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) dbHelper.close();
        super.onDestroy();
    }
}
