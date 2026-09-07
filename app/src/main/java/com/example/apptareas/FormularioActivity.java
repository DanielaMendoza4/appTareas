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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FormularioActivity extends AppCompatActivity {

    public static final String EXTRA_TAREA = "extra_tarea";

    private EditText edtTitulo, edtDescripcion, edtUsuario;
    private Button btnPendiente, btnProgreso, btnCompletada, btnSeleccionarFecha;
    private TextView txtFechaCreacion;

    private String estadoSeleccionado = Tarea.ESTADO_PENDIENTE;
    private String fechaVencimiento = "";

    private final SimpleDateFormat formatoCreacion = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat formatoVencimiento = new SimpleDateFormat("dd MMM", new Locale("es", "ES"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_tarea);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtUsuario = findViewById(R.id.edtUsuario);
        txtFechaCreacion = findViewById(R.id.txtFechaCreacion);
        btnPendiente = findViewById(R.id.btnPendiente);
        btnProgreso = findViewById(R.id.btnProgreso);
        btnCompletada = findViewById(R.id.btnCompletada);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);

        txtFechaCreacion.setText(formatoCreacion.format(Calendar.getInstance().getTime()));
        marcarEstadoSeleccionado(Tarea.ESTADO_PENDIENTE);

        btnPendiente.setOnClickListener(v -> marcarEstadoSeleccionado(Tarea.ESTADO_PENDIENTE));
        btnProgreso.setOnClickListener(v -> marcarEstadoSeleccionado(Tarea.ESTADO_EN_PROGRESO));
        btnCompletada.setOnClickListener(v -> marcarEstadoSeleccionado(Tarea.ESTADO_COMPLETADA));

        btnSeleccionarFecha.setOnClickListener(v -> mostrarSelectorDeFecha());
        findViewById(R.id.btnCalendario).setOnClickListener(v -> mostrarSelectorDeFecha());
        findViewById(R.id.btnRegresar).setOnClickListener(v -> finish());
        findViewById(R.id.btnGuardarTarea).setOnClickListener(v -> guardarTarea());
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
            Calendar seleccionada = Calendar.getInstance();
            seleccionada.set(anio, mes, dia);
            fechaVencimiento = capitalizar(formatoVencimiento.format(seleccionada.getTime()));
            btnSeleccionarFecha.setText(fechaVencimiento);
        }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String capitalizar(String texto) {
        if (texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private void guardarTarea() {
        String titulo = edtTitulo.getText().toString().trim();
        String descripcion = edtDescripcion.getText().toString().trim();
        String usuario = edtUsuario.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "Ingresa un título para la tarea", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fechaVencimiento.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha de vencimiento", Toast.LENGTH_SHORT).show();
            return;
        }

        Tarea tarea = new Tarea(System.currentTimeMillis(), titulo, descripcion,
                estadoSeleccionado, txtFechaCreacion.getText().toString(), fechaVencimiento, usuario);

        Intent resultado = new Intent();
        resultado.putExtra(EXTRA_TAREA, tarea);
        setResult(RESULT_OK, resultado);
        finish();
    }
}