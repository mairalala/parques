package juego_parques;

import java.awt.Color;
import java.awt.Point;

public class Casilla {

    private Point posicion;
    private String tipo;
    private String color;
    private boolean preguntaRespondida = false;   // Ya existía
    private String categoria;                    // Categoría de pregunta
    private Ficha fichaOcupante = null;          // Ficha en la casilla (si hay)

    public Casilla(Point pos, String tipo, String color) {
        this.posicion = pos;
        this.tipo = tipo;
        this.color = color;
    }

    // ------------------- GETTERS BÁSICOS -------------------
    public Point getPosicion() {
        return posicion;
    }

    public String getTipo() {
        return tipo;
    }

    public String getColor() {
        return color;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setColor(String color) {
        this.color = color;
    }

    // ------------------- SEGURO -------------------
    public boolean isSeguro() {
        return "seguro".equals(tipo);
    }

    // ------------------- COLOR PARA DIBUJAR -------------------
    public Color getDrawColor() {
        if (tipo == null) return Color.WHITE;

        switch (tipo) {
            case "salida":
            case "pasillo":
                return getColorJugador();

            case "seguro":
                return new Color(0, 200, 200);

            case "meeta":
                return new Color(120, 230, 230);

            case "pregunta":
                return Color.DARK_GRAY;

            default:
                return Color.WHITE;
        }
    }

    private Color getColorJugador() {
        if (color == null) return Color.LIGHT_GRAY;

        switch (color) {
            case "Rojo":
                return new Color(255, 80, 80);
            case "Azul":
                return new Color(80, 80, 255);
            case "Verde":
                return new Color(80, 200, 80);
            case "Amarillo":
                return new Color(255, 220, 80);
            default:
                return Color.LIGHT_GRAY;
        }
    }

    // ------------------- PREGUNTA -------------------
    public boolean isPreguntaRespondida() {
        return preguntaRespondida;
    }

    public void setPreguntaRespondida(boolean respondida) {
        this.preguntaRespondida = respondida;
    }

    // ------------------- CATEGORÍA -------------------
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // ------------------- FICHA EN CASILLA -------------------
    public Ficha getFicha() {
        return fichaOcupante;
    }

    public void setFicha(Ficha f) {
        this.fichaOcupante = f;
    }

    public boolean tieneFicha() {
        return fichaOcupante != null;
    }
}
