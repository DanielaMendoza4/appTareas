package com.example.apptareas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    public interface OnTareaClickListener {
        void onTareaClick(Tarea tarea, int posicion);
    }

    private final List<Tarea> tareas;
    private final OnTareaClickListener listener;

    public TareaAdapter(List<Tarea> tareas, OnTareaClickListener listener) {
        this.tareas = tareas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = tareas.get(position);

        holder.tvTitulo.setText(tarea.getTitulo());
        holder.tvDescripcion.setText(tarea.getDescripcion());
        holder.tvEstado.setText(tarea.getEstado());
        holder.tvFechaVencimiento.setText("Vence: " + tarea.getFechaVencimiento());

        switch (tarea.getEstado()) {
            case Tarea.ESTADO_PENDIENTE:
                holder.vIndicadorEstado.setBackgroundColor(Color.parseColor("#E74C3C"));
                break;
            case Tarea.ESTADO_EN_PROGRESO:
                holder.vIndicadorEstado.setBackgroundColor(Color.parseColor("#F5A623"));
                break;
            case Tarea.ESTADO_COMPLETADA:
                holder.vIndicadorEstado.setBackgroundColor(Color.parseColor("#2ECC71"));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTareaClick(tarea, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvEstado, tvFechaVencimiento;
        View vIndicadorEstado;

        TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloTarea);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionTarea);
            tvEstado = itemView.findViewById(R.id.tvEstadoTarea);
            tvFechaVencimiento = itemView.findViewById(R.id.tvFechaVencimiento);
            vIndicadorEstado = itemView.findViewById(R.id.vIndicadorEstado);
        }
    }
}