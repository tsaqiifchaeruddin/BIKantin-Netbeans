package koneksi;

import java.sql.Connection;

/**
 * Uji cepat koneksi database.
 * Klik kanan file ini di NetBeans -> Run File (Shift + F6).
 */
public class CekKoneksi {

    public static void main(String[] args) {
        Connection c = Koneksi.configDB();

        if (c != null) {
            System.out.println("HORE! NetBeans Berhasil Terhubung ke Database BI-KANTIN!");
        } else {
            System.out.println("GAGAL TERHUBUNG! Pastikan MySQL menyala dan database db_bikantin sudah dibuat.");
        }
    }
}
