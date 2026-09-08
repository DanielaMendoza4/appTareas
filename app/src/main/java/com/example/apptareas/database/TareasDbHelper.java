package com.example.apptareas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.apptareas.Tarea;

import java.util.ArrayList;
import java.util.List;

public class TareasDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "apptareas.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_TAREAS = "tareas";
    public static final String COL_ID = "id";
    public static final String COL_TITULO = "titulo";
    public static final String COL_DESCRIPCION = "descripcion";
    public static final String COL_ESTADO = "estado";
    public static final String COL_FECHA_CREACION = "fecha_creacion";
    public static final String COL_FECHA_VENCIMIENTO = "fecha_vencimiento";
    public static final String COL_USUARIO = "usuario_asignado";

    private static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_TAREAS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TITULO + " TEXT NOT NULL, " +
                    COL_DESCRIPCION + " TEXT, " +
                    COL_ESTADO + " TEXT NOT NULL CHECK(" + COL_ESTADO +
                    " IN ('Pendiente','En progreso','Completada')), " +
                    COL_FECHA_CREACION + " TEXT NOT NULL, " +
                    COL_FECHA_VENCIMIENTO + " TEXT NOT NULL, " +
                    COL_USUARIO + " TEXT)";

    public TareasDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
        db.execSQL("CREATE INDEX idx_tareas_estado ON " + TABLE_TAREAS + "(" + COL_ESTADO + ")");
        db.execSQL("CREATE INDEX idx_tareas_vencimiento ON " + TABLE_TAREAS + "(" + COL_FECHA_VENCIMIENTO + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        onCreate(db);
    }

    public long insertar(Tarea tarea) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = aContentValues(tarea);
        return db.insertOrThrow(TABLE_TAREAS, null, v);
    }

    public List<Tarea> obtenerTodas() {
        return consultar(null, null, COL_ID + " DESC");
    }

    public List<Tarea> obtenerPorEstado(String estado) {
        return consultar(COL_ESTADO + "=?", new String[]{estado}, COL_ID + " DESC");
    }

    public Tarea obtenerPorId(long id) {
        List<Tarea> r = consultar(COL_ID + "=?", new String[]{String.valueOf(id)}, null);
        return r.isEmpty() ? null : r.get(0);
    }

    public int actualizar(Tarea tarea) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_TAREAS, aContentValues(tarea),
                COL_ID + "=?", new String[]{String.valueOf(tarea.getId())});
    }

    public int eliminar(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_TAREAS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    private List<Tarea> consultar(String selection, String[] args, String orderBy) {
        List<Tarea> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_TAREAS, null, selection, args, null, null, orderBy);
            while (c.moveToNext()) {
                lista.add(new Tarea(
                        c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_TITULO)),
                        c.getString(c.getColumnIndexOrThrow(COL_DESCRIPCION)),
                        c.getString(c.getColumnIndexOrThrow(COL_ESTADO)),
                        c.getString(c.getColumnIndexOrThrow(COL_FECHA_CREACION)),
                        c.getString(c.getColumnIndexOrThrow(COL_FECHA_VENCIMIENTO)),
                        c.getString(c.getColumnIndexOrThrow(COL_USUARIO))));
            }
        } finally {
            if (c != null) c.close();
        }
        return lista;
    }

    private ContentValues aContentValues(Tarea t) {
        ContentValues v = new ContentValues();
        v.put(COL_TITULO, t.getTitulo());
        v.put(COL_DESCRIPCION, t.getDescripcion());
        v.put(COL_ESTADO, t.getEstado());
        v.put(COL_FECHA_CREACION, t.getFechaCreacion());
        v.put(COL_FECHA_VENCIMIENTO, t.getFechaVencimiento());
        v.put(COL_USUARIO, t.getUsuarioAsignado());
        return v;
    }
}
