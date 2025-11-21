package juego_parques;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PreguntaDialog extends JDialog {

    private boolean respuestaCorrecta = false;

    public PreguntaDialog(JFrame parent, generarCasillasPregunta preguntaObj) {
        super(parent, "Pregunta", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Obtener la pregunta y respuesta
        String enunciado = preguntaObj.getPregunta();
        String respuestaCorrectaTexto = preguntaObj.getRespuestaCorrecta();

        JLabel lblPregunta = new JLabel("<html><p style='width:340px;'>" + enunciado + "</p></html>");
        lblPregunta.setFont(new Font("Arial", Font.PLAIN, 16));

        JTextField txtRespuesta = new JTextField();
        txtRespuesta.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton btnResponder = new JButton("Responder");
        btnResponder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String respuestaJugador = txtRespuesta.getText().trim();

                if (respuestaJugador.equalsIgnoreCase(respuestaCorrectaTexto)) {
                    respuestaCorrecta = true;
                    JOptionPane.showMessageDialog(PreguntaDialog.this, "¡Correcto!");
                } else {
                    respuestaCorrecta = false;
                    JOptionPane.showMessageDialog(PreguntaDialog.this, "Incorrecto. La respuesta correcta era:\n" + respuestaCorrectaTexto);
                }

                dispose();
            }
        });

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(lblPregunta, BorderLayout.NORTH);
        centro.add(txtRespuesta, BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);
        add(btnResponder, BorderLayout.SOUTH);
    }

    public boolean esCorrecta() {
        return respuestaCorrecta;
    }
}
