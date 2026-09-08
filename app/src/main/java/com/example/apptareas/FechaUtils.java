package com.example.apptareas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class FechaUtils {

    public static final String FORMATO_ISO = "yyyy-MM-dd";
    private static final String FORMATO_CREACION_UI = "dd/MM/yyyy";
    private static final String FORMATO_VENCIMIENTO_UI = "dd MMM";

    private FechaUtils() { }

    public static String hoyISO() {
        return new SimpleDateFormat(FORMATO_ISO, Locale.US)
                .format(Calendar.getInstance().getTime());
    }

    public static String isoDe(int anio, int mes, int dia) {
        Calendar c = Calendar.getInstance();
        c.set(anio, mes, dia, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return new SimpleDateFormat(FORMATO_ISO, Locale.US).format(c.getTime());
    }

    public static String paraUICreacion(String iso) {
        return convertir(iso, FORMATO_CREACION_UI);
    }

    public static String paraUIVencimiento(String iso) {
        String base = convertir(iso, FORMATO_VENCIMIENTO_UI);
        if (base.isEmpty()) return base;
        return Character.toUpperCase(base.charAt(0)) + base.substring(1);
    }

    private static String convertir(String iso, String patronDestino) {
        if (iso == null || iso.isEmpty()) return "";
        try {
            Date fecha = new SimpleDateFormat(FORMATO_ISO, Locale.US).parse(iso);
            if (fecha == null) return iso;
            SimpleDateFormat destino = patronDestino.equals(FORMATO_VENCIMIENTO_UI)
                    ? new SimpleDateFormat(patronDestino, new Locale("es", "ES"))
                    : new SimpleDateFormat(patronDestino, Locale.getDefault());
            return destino.format(fecha);
        } catch (ParseException e) {
            return iso;
        }
    }
}
