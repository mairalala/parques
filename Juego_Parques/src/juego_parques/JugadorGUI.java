package juego_parques;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class JugadorGUI extends JPanel {

    private Jugador[] jugadores;
    private Tablero tablero;
    private TableroPanel panelTablero;

    private int turnoActual = 0;
    private Random random = new Random();

    private int paresConsecutivos = 0;
    private int intentosIniciales = 0;

    private Ficha fichaSeleccionada;
    private ReproductorSonido reproductor;

    private String categoriaSeleccionada; // formato: "Programación Java básica - Fácil"

    public JugadorGUI(Jugador[] jugadores, Tablero tablero, TableroPanel panelTablero,
            ReproductorSonido reproductor, String categoriaSeleccionada) {

        this.jugadores = jugadores;
        this.tablero = tablero;
        this.panelTablero = panelTablero;
        this.reproductor = reproductor;
        this.categoriaSeleccionada = categoriaSeleccionada != null ? categoriaSeleccionada : "Programación Java básica - Fácil";

        setLayout(new BorderLayout());

        // Panel inferior con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 10));
        panelBotones.setOpaque(false);
        panelBotones.setPreferredSize(new Dimension(1, 80));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton botonLanzar = new BotonModerno("Lanzar Dados");
        botonLanzar.addActionListener(e -> lanzarDados());
        panelBotones.add(botonLanzar);

        JButton botonPausa = new BotonModerno("Pausa");
        botonPausa.addActionListener(e -> pausarJuego());
        panelBotones.add(botonPausa);

        JButton botonInfo = new BotonModerno("?");
        botonInfo.addActionListener(e -> mostrarInformacion());
        panelBotones.add(botonInfo);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void mostrarInformacion() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ImageIcon imagen = new ImageIcon("src/juego_parques/informacion.jpg");
        Image img = imagen.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
        JLabel etiqueta = new JLabel(new ImageIcon(img));
        JOptionPane.showMessageDialog(parentFrame, etiqueta, "Información del Juego", JOptionPane.PLAIN_MESSAGE);
    }

    private void lanzarDados() {
        Jugador jugador = jugadores[turnoActual];
        Dado dado = new Dado();
        int[] valores = dado.lanzar();
        int total = valores[0] + valores[1];

        reproductor.lanzarDados();
        panelTablero.setDados(valores[0], valores[1]);
        panelTablero.repaint();

        boolean esPar = (valores[0] == valores[1]);
        if (esPar) {
            paresConsecutivos++;
            intentosIniciales = 0;
        } else {
            paresConsecutivos = 0;
            intentosIniciales++;
        }

        if (jugador.todasEnBase() && !esPar) {
            if (intentosIniciales >= 3) {
                siguienteTurno("No sacó par en 3 intentos. Pierde turno.");
                return;
            } else {
                return;
            }
        }

        if (esPar && jugador.tieneFichasEnBase()) {
            elegirFichaParaSacar(jugador);
            panelTablero.actualizar();
            siguienteTurno("Ficha sacada, siguiente turno");
            return;
        }

        List<Ficha> activas = jugador.getFichasActivas();
        if (!activas.isEmpty()) {
            if (activas.size() > 1) {
                elegirFichaParaMover(jugador, total);
            } else {
                fichaSeleccionada = activas.get(0);
                moverFichaConPregunta(fichaSeleccionada, total, jugador);
            }
            panelTablero.actualizar();
        }

        if (!esPar) {
            siguienteTurno("Turno terminado");
        }
    }

    private void moverFichaConPregunta(Ficha ficha, int pasos, Jugador jugador) {
        int destino = ficha.getIndiceCasilla() + pasos;
        if (destino >= tablero.getCasillas().size()) {
            destino = tablero.getCasillas().size() - 1;
        }

        Casilla casillaDestino = tablero.getCasillas().get(destino);

        // Separar categoría y dificultad correctamente
        String categoriaBase = "Programación Java básica";
        String dificultad = "Fácil";
        if (categoriaSeleccionada != null && categoriaSeleccionada.contains("-")) {
            String[] partes = categoriaSeleccionada.split("-");
            if (partes.length >= 2) {
                categoriaBase = partes[0].replace("\u00A0", "").trim();
                dificultad = partes[1].replace("\u00A0", "").trim();
            }
        }

        if ("pregunta".equalsIgnoreCase(casillaDestino.getTipo()) && !casillaDestino.isPreguntaRespondida()) {
            ficha.mover(pasos, tablero);
            panelTablero.actualizar();

            // Generar y mostrar pregunta
            generarCasillasPregunta preguntaObj = generarCasillasPregunta.generarPregunta(categoriaBase, dificultad);
            PreguntaDialog pd = new PreguntaDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    preguntaObj
            );
            pd.setVisible(true);

            casillaDestino.setPreguntaRespondida(true);
            panelTablero.repaint();
            return;
        }

        ficha.mover(pasos, tablero);

        if (ficha.puedeEntrarPasillo(tablero)) {
            ficha.setIndiceCasilla(-1);
            ficha.setIndiceCasillaPasillo(0);
            ArrayList<Casilla> pasillo = tablero.getPasillos().get(ficha.getColorStr());
            if (pasillo != null && !pasillo.isEmpty()) {
                ficha.setPosicion(pasillo.get(0).getPosicion());
            }
        }

        panelTablero.setFichaActiva(ficha);
        panelTablero.actualizar();
    }

    private void elegirFichaParaSacar(Jugador jugador) {
        Object[] opciones = jugador.getFichasEnBase().stream().map(f -> "Ficha " + f.getNumero()).toArray();
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        Object seleccion = JOptionPane.showInputDialog(parentFrame,
                "¿Qué ficha deseas sacar?", "Seleccionar ficha",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion != null) {
            int num = Integer.parseInt(seleccion.toString().replace("Ficha ", ""));
            Ficha ficha = jugador.getFichaPorNumero(num);
            int salida = tablero.getSalidaIndex(jugador.getColorStr(), tablero.getCantidadJugadores());
            ficha.sacarDeBase(salida, tablero);
            reproductor.sacarFichaDeBase();
            fichaSeleccionada = ficha;
            panelTablero.setFichaActiva(fichaSeleccionada);
            panelTablero.actualizar();
        }
    }

    private void elegirFichaParaMover(Jugador jugador, int pasos) {
        List<Ficha> activas = jugador.getFichasActivas();
        Object[] opciones = activas.stream().map(f -> "Ficha " + f.getNumero()).toArray();
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        Object seleccion = JOptionPane.showInputDialog(parentFrame,
                "¿Qué ficha deseas mover?", "Mover ficha",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion != null) {
            int num = Integer.parseInt(seleccion.toString().replace("Ficha ", ""));
            fichaSeleccionada = jugador.getFichaPorNumero(num);
            moverFichaConPregunta(fichaSeleccionada, pasos, jugador);
        }
    }

    private void siguienteTurno(String mensaje) {
        paresConsecutivos = 0;
        intentosIniciales = 0;

        turnoActual = (turnoActual + 1) % jugadores.length;
        JuegoParquesGUI ventana = (JuegoParquesGUI) SwingUtilities.getWindowAncestor(this);
        ventana.mostrarMensaje("Turno de " + jugadores[turnoActual].getColorStr());
        ventana.actualizarColorBarraPorTurno(jugadores[turnoActual].getColor());

        fichaSeleccionada = null;
        panelTablero.setFichaActiva(null);
        panelTablero.actualizar();
    }

    private void pausarJuego() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame instanceof JuegoParquesGUI) {
            ((JuegoParquesGUI) parentFrame).mostrarPanelPausa();
        }
    }

    public void setCategoriaSeleccionada(String categoria) {
        this.categoriaSeleccionada = categoria != null ? categoria : "Programación Java básica - Fácil";
    }

    public String getCategoriaSeleccionada() {
        return categoriaSeleccionada;
    }

    // ------------------- BOTÓN MODERNO -------------------
    class BotonModerno extends JButton {

        private Color colorNormal, colorHover;
        private Color colorTexto = Color.BLACK;

        public BotonModerno(String texto) {
            super(texto);

            if (texto.equals("Lanzar Dados")) {
                colorNormal = new Color(225, 250, 195);
                colorHover = new Color(56, 142, 60);
            } else if (texto.equals("Pausa")) {
                colorNormal = new Color(255, 152, 0);
                colorHover = new Color(230, 120, 0);
            } else if (texto.equals("?")) {
                colorNormal = new Color(33, 150, 243);
                colorHover = new Color(25, 118, 210);
            } else {
                colorNormal = new Color(30, 144, 255);
                colorHover = new Color(25, 120, 215);
            }

            setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 18));
            setForeground(colorTexto);
            setBackground(colorNormal);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new RoundedBorder(20));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(colorHover);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(colorNormal);
                }
            });
        }

        private class RoundedBorder implements javax.swing.border.Border {

            private int radio;

            RoundedBorder(int r) {
                radio = r;
            }

            public Insets getBorderInsets(Component c) {
                return new Insets(10, 20, 10, 20);
            }

            public boolean isBorderOpaque() {
                return false;
            }

            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                g.drawRoundRect(x, y, w - 1, h - 1, radio, radio);
            }
        }
    }
}
