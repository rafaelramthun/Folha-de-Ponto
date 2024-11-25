package com.rafael.folhadeponto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    private final List<Registro> registros;

    public RegistroAdapter(List<Registro> registros) {
        this.registros = registros;
    }

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tabela, parent, false);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        Registro registroAtual = registros.get(position);

        holder.tvData.setText(registroAtual.getData());
        holder.tvTipo.setText(registroAtual.getTipo());
        holder.tvHorario.setText(registroAtual.getHorario());

        // Exibe a linha separadora no final do dia anterior
        if (position < registros.size() - 1) {
            Registro proximoRegistro = registros.get(position + 1);
            if (!registroAtual.getData().equals(proximoRegistro.getData())) {
                // Exibe a linha separadora se o próximo registro for de outro dia
                holder.linhaSeparadora.setVisibility(View.VISIBLE);
            } else {
                // Oculta a linha separadora se o próximo registro for do mesmo dia
                holder.linhaSeparadora.setVisibility(View.GONE);
            }
        } else {
            // Último registro da lista nunca tem linha separadora
            holder.linhaSeparadora.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    static class RegistroViewHolder extends RecyclerView.ViewHolder {
        TextView tvData, tvTipo, tvHorario;
        View linhaSeparadora;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.tvData);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvHorario = itemView.findViewById(R.id.tvHorario);
            linhaSeparadora = itemView.findViewById(R.id.linhaSeparadora);
        }
    }
}
