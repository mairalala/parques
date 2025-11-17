package juego_parques;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReproductorSonido {

    // Clip que mantiene la música de fondo
    private Clip musicaFondo;

    // Executor para reproducir efectos en hilos separados (evita bloqueos)
    private ExecutorService efectosExecutor = Executors.newCachedThreadPool();

    // Volúmenes independientes para música y efectos
    private float volumenMusica = 0.7f;
    private float volumenEfectos = 0.7f;

    // ------------------------
    //  CONTROL DE VOLUMEN
    // ------------------------
    /**
     * Ajusta el volumen de la música de fondo. Si la música está sonando,
     * aplica el cambio inmediatamente.
     */
    public void ajustarVolumenMusica(float v) {
        volumenMusica = Math.max(0, Math.min(1, v)); // Limita entre 0 y 1

        // Si hay música sonando, ajusta el volumen en tiempo real
        if (musicaFondo != null && musicaFondo.isActive()) {
            setClipVolumen(musicaFondo, volumenMusica);
        }
    }

    /**
     * Ajusta el volumen de los efectos de sonido. Este valor se aplica cuando
     * un efecto se reproduce.
     */
    public void ajustarVolumenEfectos(float v) {
        volumenEfectos = Math.max(0, Math.min(1, v));
    }

    // ------------------------
    //    MÚSICA DE FONDO
    // ------------------------
    /**
     * Reproduce una música de fondo especificada por archivo en bucle infinito.
     */
    public void reproducirMusicaFondo(String archivo) {
        detenerMusicaFondo(); // Detiene la música actual, si existe

        try {
            // Cargar archivo desde la carpeta de recursos
            // Asegúrate de que esta ruta de archivo sea la correcta para tu música de fondo
            InputStream is = getClass().getResourceAsStream("/juego_parques/Inspiring-Ascent-_0be33efa125b4940864f156cafbaa28c_.wav");

            if (is == null) {
                System.out.println("❌ No se encontró música: " + archivo);
                return;
            }

            // Leer audio
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(is);

            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audioIn);

            // Aplicar el volumen configurado
            setClipVolumen(musicaFondo, volumenMusica);

            // Repetir infinito
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
            musicaFondo.start();

        } catch (Exception e) {
            System.out.println("❌ Error música: " + e.getMessage());
        }
    }

    // ------------------------------------
    //  NUEVOS MÉTODOS DE EFECTOS DE SONIDO
    // ------------------------------------

    /**
     * Reproduce el sonido de lanzar los dados.
     */
    public void lanzarDados() {
        reproducirEfecto("dice-142528.wav"); // <--- Archivo para lanzar dados
    }

    /**
     * Reproduce el sonido cuando una ficha es "comida" (capturada).
     */
    public void comerFicha() {
        reproducirEfecto("07-comerpersonaje-98937.wav"); // <--- Archivo para comer ficha
    }

    /**
     * Reproduce el sonido cuando una ficha sale de la base al tablero.
     */
    public void sacarFichaDeBase() {
        reproducirEfecto("winner-game-sound-404167.wav"); // <--- Archivo para sacar ficha
    }

    /**
     * Reproduce el sonido de victoria o cuando un jugador llega a la meta con todas las fichas.
     */
    public void victoria() {
        reproducirEfecto("level-win-6416.wav"); // <--- Archivo de victoria/fin de juego
    }

    // ------------------------
    //    EFECTOS DE SONIDO (EXISTENTE)
    // ------------------------
    /**
     * Reproduce un efecto de sonido sin detener la música. Cada efecto se
     * reproduce en un hilo separado.
     */
    public void reproducirEfecto(String archivo) {
        efectosExecutor.submit(() -> {
            try {
                InputStream is = getClass().getResourceAsStream("/juego_parques/" + archivo);

                if (is == null) {
                    System.out.println("❌ No se encontró efecto: " + archivo);
                    return;
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(is);

                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);

                // Aplicar volumen de efectos
                setClipVolumen(clip, volumenEfectos);

                clip.start(); // Reproduce una vez

            } catch (Exception e) {
                System.out.println("❌ Error efecto: " + e.getMessage());
            }
        });
    }

    // ------------------------
    //    AJUSTE DE VOLUMEN EN DECIBELES (EXISTENTE)
    // ------------------------
    /**
     * Ajusta el volumen de un clip en decibelios (dB) Convierte 0-1 → escala de
     * decibeles.
     */
    private void setClipVolumen(Clip clip, float volumen) {
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Conversión logarítmica para volumen de audio
            float dB = (float) (20 * Math.log10(volumen <= 0 ? 0.0001 : volumen));

            gainControl.setValue(dB);

        } catch (Exception e) {
            System.out.println("❌ No se pudo ajustar volumen: " + e.getMessage());
        }
    }

    // ------------------------
    //    CONTROL DE REPRODUCCIÓN (EXISTENTE)
    // ------------------------
    /**
     * Detiene la música de fondo si está en reproducción.
     */
    public void detenerMusicaFondo() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop();
            musicaFondo.close();
        }
    }

    /**
     * Detiene toda la música y efectos. Se usa al cerrar el juego.
     */
    public void detenerTodo() {
        detenerMusicaFondo();
        efectosExecutor.shutdownNow();
    }

    /**
     * Indica si la música de fondo está sonando.
     */
    public boolean estaReproduciendoFondo() {
        return musicaFondo != null && musicaFondo.isRunning();
    }

    // ------------------------
    //    GETTERS (EXISTENTE)
    // ------------------------
    public float getVolumenMusica() {
        return volumenMusica;
    }

    public float getVolumenEfectos() {
        return volumenEfectos;
    }
}