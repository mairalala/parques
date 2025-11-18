package juego_parques;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Ventana principal del juego Parqués GUI. Maneja tablero, jugadores, barra
 * superior, panel de turnos, configuración y sonidos.
 */
public class JuegoParquesGUI extends JFrame {

    private JPanel barraSuperior;           // Barra roja superior con botones cerrar/minimizar
    private Tablero tablero;                // Lógica interna del tablero
    private TableroPanel panelTablero;      // Panel gráfico donde se dibuja el tablero
    private Jugador[] jugadores;            // Lista de jugadores
    private JugadorGUI controladorTurnos;   // Panel de control: dados, turno, pausa, etc
    private ReproductorSonido reproductor;  // Reproductor de sonidos

    private PanelPausa panelPausa;          // Panel superpuesto cuando el juego está en pausa
    private boolean modoOscuro;             // Tema actual (claro u oscuro)
    private FondoPanel fondo;               // Panel de fondo con imágenes de tema claro/oscuro
    private String categoriaPreguntas;      // Categoría actual para casillas de preguntas
    private PanelConfiguracion panelConfiguracion; // Panel de configuración del juego

    public JuegoParquesGUI(
            int cantidadJugadores,
            ReproductorSonido reproductor,
            boolean modoOscuro,
            String[] nombres,
            String[] colores) {

        this.reproductor = reproductor;
        this.modoOscuro = modoOscuro;

        inicializarBase(cantidadJugadores);
        crearJugadoresPorDefecto(cantidadJugadores, nombres, colores);
        terminarInicializacion();
        generarCasillasPregunta();
    }

    JButton botonInfo = new JButton("Información");

    public void mostrarMensaje(String texto) {
        new MensajeEmergente(this, texto);
    }

    private void inicializarBase(int cantidadJugadores) {
        setUndecorated(true);
        setLayout(new BorderLayout());
        crearBarraSuperior();

        fondo = new FondoPanel(
                "/juego_parques/imagenClaro.png",
                "/juego_parques/imagenOscuro.JPG",
                modoOscuro);
        fondo.setLayout(new BorderLayout());
        add(fondo, BorderLayout.CENTER);

        tablero = new Tablero();
    }

    private void crearJugadoresPorDefecto(int cantidadJugadores, String[] nombres, String[] colores) {
        jugadores = new Jugador[cantidadJugadores];
        Color[] coloresActivos = new Color[cantidadJugadores];

        for (int i = 0; i < cantidadJugadores; i++) {
            coloresActivos[i] = obtenerColor(colores[i]);
            jugadores[i] = new Jugador(nombres[i], coloresActivos[i], tablero);

            Point[] posBase = tablero.getPosicionesBase(jugadores[i].getColorStr());
            for (int f = 0; f < jugadores[i].getFichas().size(); f++) {
                Ficha ficha = jugadores[i].getFichas().get(f);
                ficha.volverABase();
                if (posBase != null && posBase.length > f) {
                    ficha.setPosicion(posBase[f]);
                }
            }
        }
    }

    private Color obtenerColor(String c) {
        if (c == null) return Color.WHITE;

        switch (c.toUpperCase()) {
            case "ROJO":
                return Color.RED;
            case "VERDE":
                return Color.GREEN;
            case "AZUL":
                return Color.BLUE;
            case "AMARILLO":
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }
    }

    private void terminarInicializacion() {

        panelTablero = new TableroPanel(tablero, jugadores, modoOscuro);

        JPanel contenedorTablero = new JPanel();
        contenedorTablero.setOpaque(false);
        contenedorTablero.setLayout(null);

        contenedorTablero.setPreferredSize(new Dimension(0, 0));

        panelTablero.setBounds(0, 15, 1800, 800);

        contenedorTablero.add(panelTablero);
        fondo.add(contenedorTablero, BorderLayout.CENTER);

        controladorTurnos = new JugadorGUI(jugadores, tablero, panelTablero, reproductor, categoriaPreguntas);
        controladorTurnos.setOpaque(false);
        controladorTurnos.setPreferredSize(new Dimension(200, 80));
        fondo.add(controladorTurnos, BorderLayout.SOUTH);

        panelPausa = new PanelPausa(this);
        panelPausa.setVisible(false);
        getLayeredPane().add(panelPausa, JLayeredPane.POPUP_LAYER);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void crearBarraSuperior() {
        barraSuperior = new JPanel();
        barraSuperior.setPreferredSize(new Dimension(1, 35));
        barraSuperior.setLayout(new BorderLayout());
        barraSuperior.setBackground(new Color(180, 0, 0));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setOpaque(false);

        JButton btnMin = new JButton("—");
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));

        JButton btnClose = new JButton("X");
        btnClose.addActionListener(e -> dispose());

        estiloBoton(btnMin);
        estiloBoton(btnClose);

        botones.add(btnMin);
        botones.add(btnClose);
        barraSuperior.add(botones, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);
    }

    /**
     * 🔥 MODIFICACIÓN: AQUI SE APLICAN COLORES A LOS BOTONES
     */
    private void estiloBoton(JButton btn) {
        btn.setFocusable(false);
        btn.setPreferredSize(new Dimension(45, 28));
        btn.setFont(new Font("Arial", Font.BOLD, 16));

        // 🔹 Colores personalizados
        btn.setBackground(new Color(225, 250, 195)); // Gris oscuro
        btn.setForeground(Color.BLACK);

        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // 🔹 Efecto HOVER
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(90, 93, 95));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(60, 63, 65));
            }
        });
    }

    public void setColorBarra(Color c) {
        barraSuperior.setBackground(c);
        barraSuperior.repaint();
    }

    public void mostrarPanelPausa() {
        panelPausa.setBounds(0, 0, getWidth(), getHeight());
        panelPausa.setVisible(true);
        panelPausa.repaint();
    }

    public void ocultarPanelPausa() {
        panelPausa.setVisible(false);
    }

    public void setCategoriaPreguntas(String categoria) {
        this.categoriaPreguntas = categoria;
        if (controladorTurnos != null) {
            controladorTurnos.setCategoriaSeleccionada(categoria);
        }
    }

    public String getCategoriaPreguntas() {
        return categoriaPreguntas;
    }

    public void cambiarTema(boolean modo) {
        this.modoOscuro = modo;

        if (fondo != null) {
            fondo.setModoOscuro(modo);
            fondo.repaint();
        }
        if (panelTablero != null) {
            panelTablero.setModoOscuro(modo);
            panelTablero.repaint();
        }

        if (barraSuperior != null) {
            barraSuperior.setBackground(modo ? Color.DARK_GRAY : new Color(180, 0, 0));
            barraSuperior.repaint();
        }
    }

    private void generarCasillasPregunta() {
        if (categoriaPreguntas == null || categoriaPreguntas.isEmpty()) {
            categoriaPreguntas = "default";
        }

        Random rand = new Random();
        int generadas = 0;
        List<Casilla> casillas = tablero.getCasillas();

        while (generadas < 6) {
            int index = rand.nextInt(casillas.size());
            Casilla c = casillas.get(index);
            if ("normal".equals(c.getTipo()) && !c.isPreguntaRespondida()) {
                c.setTipo("pregunta");
                c.setCategoria(categoriaPreguntas);
                generadas++;
            }
        }
    }

    public void mostrarPanelConfiguracion() {
        if (panelConfiguracion == null) {
            panelConfiguracion = new PanelConfiguracion(this, reproductor, modoOscuro);
        }
        panelConfiguracion.setLocationRelativeTo(this);
        panelConfiguracion.setVisible(true);
    }

    public ReproductorSonido getReproductor() {
        return this.reproductor;
    }

    public void actualizarTablero() {
        if (panelTablero != null) {
            panelTablero.actualizar();
        }
    }
}
