package juego_parques;

import java.awt.Point;
import java.util.*;

public class Tablero {

    private ArrayList<Casilla> ruta;
    private Map<String, ArrayList<Casilla>> pasillos;
    private Map<String, Point> metas;
    private int cantidadJugadores = 4;

    public Tablero() {
        this(4);
    }

    public Tablero(int cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;
        ruta = new ArrayList<>();
        pasillos = new HashMap<>();
        metas = new HashMap<>();

        inicializarRutaReal();
        inicializarPasillos();
        inicializarMetas();
        generarCasillasPregunta();   // ← YA MODIFICADO
    }

    public void setCantidadJugadores(int cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;
    }

    public ArrayList<Casilla> getCasillas() {
        return ruta;
    }

    public Map<String, ArrayList<Casilla>> getPasillos() {
        return pasillos;
    }

    public Point getMetaPorColor(String color) {
        return metas.get(color);
    }

    public int getCantidadJugadores() {
        return cantidadJugadores;
    }

    // -----------------------------
    // ✔ OBTENER SALIDA POR COLOR
    // -----------------------------
    public int getSalidaIndex(String color, int cantidadJugadores) {
        for (int i = 0; i < ruta.size(); i++) {
            Casilla c = ruta.get(i);
            if ("salida".equals(c.getTipo()) && color.equals(c.getColor())) {
                return i;
            }
        }
        return 0;
    }

    // ---------------------------------------
    // ✔ INDICES DE ENTRADA DEL PASILLO
    // ---------------------------------------
    public int getEntradaPasillo(String color) {
        switch (color) {
            case "Rojo": return 55;
            case "Amarillo": return 4;
            case "Verde": return 21;
            case "Azul": return 38;
            default: return -1;
        }
    }

    // NUEVO
    public Point getEntradaPasilloPoint(String color) {
        int idx = getEntradaPasillo(color);
        if (idx >= 0 && idx < ruta.size()) {
            return ruta.get(idx).getPosicion();
        }
        return null;
    }

    public Point obtenerCasilla(int indice) {
        if (indice < 0 || indice >= ruta.size()) return null;
        return ruta.get(indice).getPosicion();
    }

    // -------------------------------------------------------------
    // ✔ RUTA REAL
    // -------------------------------------------------------------
    private void inicializarRutaReal() {
        int[][] coords = new int[68][2];
        int i = 0;

        for (int x = 0; x <= 7; x++) coords[i++] = new int[]{x, 10};
        for (int y = 11; y <= 18; y++) coords[i++] = new int[]{7, y};
        coords[i++] = new int[]{8, 18};
        for (int y = 18; y >= 10; y--) coords[i++] = new int[]{9, y};

        for (int x = 10; x <= 16; x++) coords[i++] = new int[]{x, 10};
        coords[i++] = new int[]{16, 9};
        for (int x = 16; x >= 9; x--) coords[i++] = new int[]{x, 8};

        for (int y = 7; y >= 0; y--) coords[i++] = new int[]{9, y};
        coords[i++] = new int[]{8, 0};
        for (int y = 0; y <= 7; y++) coords[i++] = new int[]{7, y};

        for (int x = 7; x >= 0; x--) coords[i++] = new int[]{x, 8};
        coords[i++] = new int[]{0, 9};

        for (int k = 0; k < i; k++) {
            ruta.add(new Casilla(new Point(coords[k][0], coords[k][1]), "normal", null));
        }

        // Salidas
        int[] salidas = {55, 4, 21, 38};
        String[] colores = {"Rojo", "Amarillo", "Verde", "Azul"};
        for (int k = 0; k < salidas.length; k++) {
            ruta.get(salidas[k]).setTipo("salida");
            ruta.get(salidas[k]).setColor(colores[k]);
        }

        // Seguros
        int[] seguros = {11, 16, 28, 33, 45, 50, 62, 67};
        for (int s : seguros) {
            ruta.get(s).setTipo("seguro");
        }
    }

    // -------------------------------------------------------------
    // ✔ PASILLOS
    // -------------------------------------------------------------
    private void inicializarPasillos() {

        pasillos.put("Amarillo", new ArrayList<>());
        pasillos.put("Verde", new ArrayList<>());
        pasillos.put("Azul", new ArrayList<>());
        pasillos.put("Rojo", new ArrayList<>());

        // Amarillo
        for (int y = 17; y >= 11; y--) {
            pasillos.get("Amarillo").add(
                new Casilla(new Point(8, y), "pasillo", "Amarillo")
            );
        }

        // Verde
        for (int x = 15; x >= 9; x--) {
            pasillos.get("Verde").add(
                new Casilla(new Point(x, 9), "pasillo", "Verde")
            );
        }

        // Azul
        for (int y = 1; y <= 7; y++) {
            pasillos.get("Azul").add(
                new Casilla(new Point(8, y), "pasillo", "Azul")
            );
        }

        // Rojo
        for (int x = 1; x <= 7; x++) {
            pasillos.get("Rojo").add(
                new Casilla(new Point(x, 9), "pasillo", "Rojo")
            );
        }
    }

    // -------------------------------------------------------------
    // ✔ METAS
    // -------------------------------------------------------------
    private void inicializarMetas() {

        metas.put("Rojo",
            pasillos.get("Rojo").get(pasillos.get("Rojo").size() - 1).getPosicion());

        metas.put("Amarillo",
            pasillos.get("Amarillo").get(pasillos.get("Amarillo").size() - 1).getPosicion());

        metas.put("Verde",
            pasillos.get("Verde").get(pasillos.get("Verde").size() - 1).getPosicion());

        metas.put("Azul",
            pasillos.get("Azul").get(pasillos.get("Azul").size() - 1).getPosicion());
    }

    // -------------------------------------------------------------
    // ✔ GENERAR CASILLAS DE PREGUNTA (MEJORADO)
    // -------------------------------------------------------------
    private void generarCasillasPregunta() {

        Set<Integer> usadas = new HashSet<>();
        Random rand = new Random();

        int generadas = 0;

        while (generadas < 10) {

            int index = rand.nextInt(ruta.size());
            Casilla c = ruta.get(index);

            // No sobrescribir salidas, seguros o pasillos
            if (!"normal".equals(c.getTipo())) continue;

            if (usadas.contains(index)) continue;

            c.setTipo("pregunta");
            c.setPreguntaRespondida(false);   // ← NUEVO ATRIBUTO
            usadas.add(index);

            generadas++;
        }
    }

    // -------------------------------------------------------------
    // ✔ API PARA PREGUNTAS
    // -------------------------------------------------------------

    public boolean esCasillaPregunta(int idx) {
        if (idx < 0 || idx >= ruta.size()) return false;
        return "pregunta".equals(ruta.get(idx).getTipo());
    }

    public boolean preguntaYaRespondida(int idx) {
        if (idx < 0 || idx >= ruta.size()) return true;
        return ruta.get(idx).isPreguntaRespondida();
    }

    public void marcarPreguntaRespondida(int idx) {
        if (idx < 0 || idx >= ruta.size()) return;
        ruta.get(idx).setPreguntaRespondida(true);
    }

    // -------------------------------------------------------------
    // ✔ POSICIONES DE BASE
    // -------------------------------------------------------------
    public Point[] getPosicionesBase(String color) {
        if (color == null) return new Point[]{};

        switch (color) {
            case "Rojo":
                return new Point[]{new Point(3, 4), new Point(1, 0), new Point(0, 1), new Point(1, 1)};
            case "Amarillo":
                return new Point[]{new Point(3, 14), new Point(16, 15), new Point(15, 16), new Point(16, 16)};
            case "Verde":
                return new Point[]{new Point(13, 14), new Point(16, 0), new Point(15, 1), new Point(16, 1)};
            case "Azul":
                return new Point[]{new Point(13, 4), new Point(1, 0), new Point(0, 1), new Point(1, 1)};
            default:
                return new Point[]{};
        }
    }

    // -------------------------------------------------------------
    // ✔ SEGUROS / SALIDAS
    // -------------------------------------------------------------
    public boolean esSeguro(int indice) {
        if (indice < 0 || indice >= ruta.size()) return false;
        String tipo = ruta.get(indice).getTipo();
        return "seguro".equals(tipo) || "salida".equals(tipo);
    }

    public boolean esSalida(int indice) {
        if (indice < 0 || indice >= ruta.size()) return false;
        return "salida".equals(ruta.get(indice).getTipo());
    }
}
