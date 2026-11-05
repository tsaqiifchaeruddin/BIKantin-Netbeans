package koneksi;

import java.awt.GraphicsEnvironment;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 * Jembatan koneksi Java (NetBeans) ke database MySQL.
 *
 * Database : db_bikantin
 * Host     : localhost
 * Port     : 3306
 * User     : root
 * Password : (kosong, sama seperti bawaan XAMPP)
 *
 * Kalau di komputer lain user/password MySQL-mu berbeda, cukup ubah
 * konstanta USER dan PASS di bawah ini.
 */
public class Koneksi {

    private static final String URL =
            "jdbc:mysql://localhost:3306/db_bikantin"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASS = "";

    /**
     * Membuat koneksi baru ke database.
     *
     * @return Connection jika berhasil, atau null jika gagal.
     */
    public static Connection configDB() {
        Connection conn = null;
        try {
            // 1. Panggil driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Buka koneksi ke alamat database
            conn = DriverManager.getConnection(URL, USER, PASS);

        } catch (Exception e) {
            tampilkanError("Koneksi Database Gagal: " + e.getMessage());
        }
        return conn;
    }

    private static void tampilkanError(String pesan) {
        // Selalu cetak ke konsol supaya CekKoneksi bisa dibaca.
        System.err.println(pesan);

        // Jangan tampilkan dialog jika aplikasi berjalan tanpa tampilan (headless).
        if (!GraphicsEnvironment.isHeadless()) {
            JOptionPane.showMessageDialog(null, pesan);
        }
    }
}
