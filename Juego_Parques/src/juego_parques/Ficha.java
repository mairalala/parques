package juego_parques;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Ficha {

    private String colorStr;
    private Color color;
    private int numero;
    private Point posicion;
    private int indiceCasilla = 0;
    private int indiceCasillaPasillo = -1;
    private boolean enBase = true;
    private boolean enMeta = false;
    private boolean haDadoVuelta = false;

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
        haDadoVuelta = false;
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

    public void mover(int pasos, Tablero tablero) {

        int total = tablero.getCasillas().size();
        int entrada = tablero.getEntradaPasillo(colorStr);

        // -------------------------------------------------------------
        // 1. SI ESTÁ EN PASILLO → mover dentro del pasillo
        // -------------------------------------------------------------
        if (estaEnPasillo()) {

            ArrayList<Casilla> pasillo = tablero.getPasillos().get(colorStr);
            int destino = indiceCasillaPasillo + pasos;

            if (destino >= pasillo.size()) {
                destino = pasillo.size() - 1; // meta
                enMeta = true;
                posicion = tablero.getMetaPorColor(colorStr);
            } else {
                posicion = pasillo.get(destino).getPosicion();
            }

            indiceCasillaPasillo = destino;
            return;
        }

        // -------------------------------------------------------------
        // 2. Movimiento normal por la ruta
        // -------------------------------------------------------------
        int destinoRuta = (indiceCasilla + pasos) % total;

        // Marca si completó la vuelta (pasa por su salida)
        if (!haDadoVuelta && destinoRuta < indiceCasilla) {
            haDadoVuelta = true;
        }

        // -------------------------------------------------------------
        // 3. SI YA DIO VUELTA → permitir entrada o paso al pasillo
        // -------------------------------------------------------------
        if (haDadoVuelta) {

            // Caso A → cae EXACTO al seguro de entrada
            if (destinoRuta == entrada) {

                indiceCasilla = -1;
                indiceCasillaPasillo = 0;

                posicion = tablero.getPasillos()
                        .get(colorStr)
                        .get(0)
                        .getPosicion();

                return;
            }

            // Caso B → SE PASÓ del seguro y debe usar movimientos dentro del pasillo
            if (indiceCasilla < entrada && destinoRuta > entrada) {

                int pasosHastaEntrada = entrada - indiceCasilla;
                int pasosRestantes = pasos - pasosHastaEntrada;

                // entrar al pasillo
                indiceCasilla = -1;
                indiceCasillaPasillo = 0;

                ArrayList<Casilla> pasillo = tablero.getPasillos().get(colorStr);

                int destinoPasillo = pasosRestantes;

                if (destinoPasillo >= pasillo.size()) {
                    destinoPasillo = pasillo.size() - 1;
                    enMeta = true;
                    posicion = tablero.getMetaPorColor(colorStr);
                } else {
                    posicion = pasillo.get(destinoPasillo).getPosicion();
                }

                indiceCasillaPasillo = destinoPasillo;
                return;
            }
        }

        // -------------------------------------------------------------
        // 4. Movimiento normal si no entró al pasillo
        // -------------------------------------------------------------
        indiceCasilla = destinoRuta;
        posicion = tablero.obtenerCasilla(indiceCasilla);
    }

    public boolean puedeEntrarPasillo(Tablero tablero) {

        if (!haDadoVuelta) {
            return false; // no ha dado la vuelta completa
        }

        int seguro = tablero.getEntradaPasillo(colorStr);

        // Solo puede entrar si está EXACTAMENTE sobre su seguro
        return this.indiceCasilla == seguro;
    }

    public boolean haLlegadoAMeta() {
        return enMeta;
    }
}
