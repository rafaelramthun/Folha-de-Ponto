package com.rafael.folhadeponto;

public class Registro {
    private String tipo;
    private String dataHora;

    // Construtor vazio exigido pelo Firebase
    public Registro() {}

    public Registro(String tipo, String dataHora) {
        this.tipo = tipo;
        this.dataHora = dataHora;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDataHora() {
        return dataHora;
    }

    public String getData() {
        return dataHora.split(" ")[0]; // Extrai apenas a data
    }

    public String getHorario() {
        return dataHora.split(" ")[1]; // Extrai apenas o horário
    }
}
