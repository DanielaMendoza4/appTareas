package com.example.apptareas;

import java.io.Serializable;

public class Tarea implements Serializable {

    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_EN_PROGRESO = "En progreso";
    public static final String ESTADO_COMPLETADA = "Completada";

    private long id;
    private String titulo;
    private String descripcion;
    private String estado;
    private String fechaCreacion;
    private String fechaVencimiento;
    private String usuarioAsignado;

    public Tarea() { }

    public Tarea(long id, String titulo, String descripcion, String estado,
                 String fechaCreacion, String fechaVencimiento, String usuarioAsignado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaVencimiento = fechaVencimiento;
        this.usuarioAsignado = usuarioAsignado;
    }

    public long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public String getFechaCreacion() { return fechaCreacion; }
    public String getFechaVencimiento() { return fechaVencimiento; }
    public String getUsuarioAsignado() { return usuarioAsignado; }
}