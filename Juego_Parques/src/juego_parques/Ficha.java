package juego_parques;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Ficha {

    private String colorStr;
    private Color color; // nuevo campo
    private int numero;
    private Point posicion;
    private int indiceCasilla = 0; // índice en ruta principal
    private int indiceCasillaPasillo = -1; // índice en pasillo (-1 si no está)
    private boolean enBase = true;
    private boolean enMeta = false;
    private boolean haDadoVuelta = false; // NUEVO: controla si completó la vuelta

    public Ficha(String colorStr, int numero) {
        this.colorStr = colorStr;
        this.numero = numero;

        switch (colorStr) {
            case "Rojo":
                this.color = Color.RED;
                break;
            case "Amarillo":
                this.color = Color.YELLOW;
                break;
            case "Verde":
                this.color = Color.GREEN;
                break;
            case "Azul":
                this.color = Color.BLUE;
                break;
            default:
                this.color = Color.GRAY;
                break;
        }
    }

    public Color getColor() {
        return color;
    }

    public void volverABase() {
        enBase = true;
        enMeta = false;
        indiceCasilla = -1;
        indiceCasillaPasillo = -1;
        haDadoVuelta = false; // Reinicia vuelta
    }

    public void sacarDeBase(int salidaIndex, Tablero tablero) {
        enBase = false;
        enMeta = false;
        indiceCasilla = salidaIndex;
        indiceCasillaPasillo = -1;
        haDadoVuelta = false;
        posicion = tablero.obtenerCasilla(salidaIndex);
    }

    public boolean isEnBase() {
        return enBase;
    }

    public boolean isEnMeta() {
        return enMeta;
    }

    public void setEnMeta(boolean val) {
        enMeta = val;
    }

    public Point getPosicion() {
        return posicion;
    }

    public void setPosicion(Point p) {
        this.posicion = p;
    }

    public String getColorStr() {
        return colorStr;
    }

    public int getNumero() {
        return numero;
    }

    public int getIndiceCasilla() {
        return indiceCasilla;
    }

    public void setIndiceCasilla(int idx) {
        this.indiceCasilla = idx;
    }

    public int getIndiceCasillaPasillo() {
        return indiceCasillaPasillo;
    }

    public void setIndiceCasillaPasillo(int idx) {
        this.indiceCasillaPasillo = idx;
    }

    public boolean estaEnPasillo() {
        return indiceCasillaPasillo >= 0;
    }

    public boolean haDadoVuelta() {
        return haDadoVuelta;
    }

    public void resetVuelta() {
        haDadoVuelta = false;
    }

    public boolean puedeEntrarPasillo(Tablero tablero) {
        int idx = this.indiceCasilla;
        switch (colorStr) {
            case "Rojo":
                return idx == 55;
            case "Amarillo":
                return idx == 4;
            case "Verde":
                return idx == 21;
            case "Azul":
                return idx == 38;
            default:
                return false;
        }
    }

    public void mover(int pasos, Tablero tablero) {

        int totalCasillas = tablero.getCasillas().size();
        int entradaPasillo = tablero.getEntradaPasillo(colorStr);
        int destinoFinal = (indiceCasilla + pasos) % totalCasillas;

        // -------------------------------------------------------------------
        // 1. VERIFICAR SI EL MOVIMIENTO TERMINA EXACTAMENTE EN LA ENTRADA
        // -------------------------------------------------------------------
        if (!estaEnPasillo() && destinoFinal == entradaPasillo) {
            // entrar directamente
            indiceCasilla = -1;         // fuera del tablero
            indiceCasillaPasillo = 0;   // primer casilla del pasillo
            posicion = tablero.getPasillos().get(colorStr).get(0).getPosicion();
            return;
        }

        // -------------------------------------------------------------------
        // 2. SI YA ESTÁ EN PASILLO
        // -------------------------------------------------------------------
        if (estaEnPasillo()) {
            ArrayList<Casilla> pasillo = tablero.getPasillos().get(colorStr);

            int destinoPasillo = indiceCasillaPasillo + pasos;

            // Si excede la meta → NO SE MUEVE
            if (destinoPasillo >= pasillo.size()) {
                return;
            }

            indiceCasillaPasillo = destinoPasillo;
            posicion = pasillo.get(destinoPasillo).getPosicion();

            if (destinoPasillo == pasillo.size() - 1) {
                enMeta = true;
                posicion = tablero.getMetaPorColor(colorStr);
            }

            return;
        }

        // -------------------------------------------------------------------
        // 3. MOVIMIENTO NORMAL, SIN ENTRAR AL PASILLO
        // -------------------------------------------------------------------
        indiceCasilla = destinoFinal;
        posicion = tablero.obtenerCasilla(indiceCasilla);
    }

    public boolean haLlegadoAMeta() {
        return enMeta;
    }

}
