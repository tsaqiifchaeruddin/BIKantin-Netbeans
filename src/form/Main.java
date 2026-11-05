package form;

import javax.swing.SwingUtilities;

/**
 * Titik masuk aplikasi BI-KANTIN.
 * Jalankan file ini (Run Project / F6) untuk membuka Form Login.
 */
public class Main {

    public static void main(String[] args) {
        Theme.installLookAndFeel();

        SwingUtilities.invokeLater(() -> new FormLogin().setVisible(true));
    }
}
