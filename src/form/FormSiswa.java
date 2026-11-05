package form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import koneksi.Koneksi;

/**
 * Panel Siswa BI-KANTIN.
 *
 * Siswa bisa:
 *  - melihat daftar menu dan harganya
 *  - memesan menu beserta jumlahnya
 *  - memantau status pesanan (diperbarui otomatis tiap 5 detik)
 */
public class FormSiswa extends JFrame {

    private final int userId;
    private final String username;

    private JComboBox<MenuItem> cmbMenu;
    private JSpinner spJumlah;
    private JLabel lblTotal;
    private JButton btnPesan;

    private JTable tblPesanan;
    private DefaultTableModel modelPesanan;
    private JLabel lblJumlahPesanan;
    private JLabel lblStatusTerakhir;

    private Timer timer;

    public FormSiswa(int userId, String username) {
        this.userId = userId;
        this.username = username;
        initComponents();
        muatMenu();
        muatPesanan(false);
        mulaiTimer();
    }

    // ================================================================= FRAME
    private void initComponents() {
        setTitle("BI-KANTIN - Pesan Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 620));
        setSize(Theme.ukuranJendela(1180, 720));
        setLocationRelativeTo(null);

        buatTabelPesanan();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(header(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(18, 0));
        body.setBackground(Theme.BG);
        body.setBorder(new EmptyBorder(18, 22, 8, 22));
        body.add(panelPesan(), BorderLayout.WEST);
        body.add(panelRiwayat(), BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        JLabel kaki = Theme.muted(
                "BI-KANTIN  •  status pesanan diperbarui otomatis setiap 5 detik");
        kaki.setBorder(new EmptyBorder(0, 24, 10, 24));
        root.add(kaki, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel header() {
        Theme.GradientPanel p = new Theme.GradientPanel(Theme.PRIMARY, Theme.ACCENT, false);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 24, 16, 24));
        p.setPreferredSize(new Dimension(0, 88));

        JPanel kiri = new JPanel();
        kiri.setOpaque(false);
        kiri.setLayout(new BoxLayout(kiri, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("BI-KANTIN");
        brand.setFont(Theme.FONT_H1.deriveFont(Font.BOLD, 24f));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Pesan menu & pantau status pesananmu");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(new Color(255, 255, 255, 205));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        kiri.add(brand);
        kiri.add(Box.createVerticalStrut(4));
        kiri.add(sub);

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        kanan.setOpaque(false);

        JLabel siapa = new JLabel("Halo, " + username);
        siapa.setFont(Theme.FONT_BOLD);
        siapa.setForeground(Color.WHITE);

        JButton keluar = Theme.soft("Keluar");
        keluar.addActionListener(e -> logout());

        kanan.add(siapa);
        kanan.add(keluar);

        p.add(kiri, BorderLayout.WEST);
        p.add(kanan, BorderLayout.EAST);
        return p;
    }

    private void logout() {
        int jawab = JOptionPane.showConfirmDialog(this,
                "Keluar dari akun siswa?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (jawab == JOptionPane.YES_OPTION) {
            if (timer != null) {
                timer.stop();
            }
            dispose();
            new FormLogin().setVisible(true);
        }
    }

    // ============================================================ PANEL PESAN
    private JComponent panelPesan() {
        JPanel card = Theme.card(new BorderLayout());
        card.setPreferredSize(new Dimension(350, 0));

        JPanel isi = new JPanel();
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setOpaque(false);

        JLabel judul = Theme.heading("Pesan Menu");
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(judul);
        isi.add(Box.createVerticalStrut(4));

        JLabel sub = Theme.muted("Pilih menu, atur jumlah, lalu klik Pesan.");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(sub);
        isi.add(Box.createVerticalStrut(18));

        JLabel l1 = Theme.fieldLabel("Menu");
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(l1);
        isi.add(Box.createVerticalStrut(6));

        cmbMenu = new JComboBox<>();
        cmbMenu.setFont(Theme.FONT);
        cmbMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbMenu.addActionListener(e -> hitungTotal());
        isi.add(cmbMenu);
        isi.add(Box.createVerticalStrut(14));

        JLabel l2 = Theme.fieldLabel("Jumlah");
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(l2);
        isi.add(Box.createVerticalStrut(6));

        spJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        spJumlah.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        spJumlah.setAlignmentX(Component.LEFT_ALIGNMENT);
        spJumlah.addChangeListener(e -> hitungTotal());
        isi.add(spJumlah);
        isi.add(Box.createVerticalStrut(18));

        JPanel kotakTotal = new JPanel();
        kotakTotal.setLayout(new BoxLayout(kotakTotal, BoxLayout.Y_AXIS));
        kotakTotal.setBackground(Theme.SOFT_BLUE);
        kotakTotal.setBorder(new EmptyBorder(14, 14, 14, 14));
        kotakTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        kotakTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        JLabel cap = Theme.muted("Total bayar");
        cap.setAlignmentX(Component.LEFT_ALIGNMENT);
        kotakTotal.add(cap);
        kotakTotal.add(Box.createVerticalStrut(4));

        lblTotal = new JLabel("-");
        lblTotal.setFont(Theme.FONT_H1.deriveFont(Font.BOLD, 26f));
        lblTotal.setForeground(Theme.PRIMARY);
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        kotakTotal.add(lblTotal);

        isi.add(kotakTotal);
        isi.add(Box.createVerticalStrut(16));

        btnPesan = Theme.gradient("Pesan Sekarang");
        btnPesan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnPesan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPesan.addActionListener(e -> pesan());
        isi.add(btnPesan);

        isi.add(Box.createVerticalStrut(16));

        JLabel catatan = new JLabel("<html>"
                + "Pesanan masuk ke admin sekolah.<br>"
                + "Setelah siap, tunjukkan nomor<br>"
                + "pesanan ke petugas kantin untuk<br>"
                + "mengambil makanan."
                + "</html>");
        catatan.setFont(Theme.FONT_SMALL);
        catatan.setForeground(Theme.MUTED);
        catatan.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(catatan);

        card.add(isi, BorderLayout.NORTH);
        return card;
    }

    // =========================================================== PANEL RIWAYAT
    private JComponent panelRiwayat() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);

        JPanel judul = new JPanel(new BorderLayout());
        judul.setOpaque(false);
        judul.add(Theme.heading("Pesanan Saya"), BorderLayout.WEST);

        lblStatusTerakhir = Theme.badge("Belum ada pesanan", Theme.MUTED);
        judul.add(lblStatusTerakhir, BorderLayout.EAST);
        p.add(judul, BorderLayout.NORTH);

        p.add(new JScrollPane(tblPesanan), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);

        lblJumlahPesanan = Theme.muted("");
        bawah.add(lblJumlahPesanan, BorderLayout.WEST);

        JLabel alur = Theme.muted(
                "Menunggu Konfirmasi → Sedang Dimasak → Siap Diambil → Selesai");
        bawah.add(alur, BorderLayout.EAST);

        p.add(bawah, BorderLayout.SOUTH);
        return p;
    }

    private void buatTabelPesanan() {
        modelPesanan = new DefaultTableModel(
                new Object[]{"ID", "Menu", "Jumlah", "Total", "Status", "Bayar", "Waktu"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPesanan = new JTable(modelPesanan);
        Theme.styleTabel(tblPesanan);
        tblPesanan.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblPesanan.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblPesanan.getColumnModel().getColumn(2).setPreferredWidth(55);
        tblPesanan.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblPesanan.getColumnModel().getColumn(4).setPreferredWidth(160);
        tblPesanan.getColumnModel().getColumn(5).setPreferredWidth(90);
        tblPesanan.getColumnModel().getColumn(6).setPreferredWidth(115);

        tblPesanan.getColumnModel().getColumn(3).setCellRenderer(new Theme.RupiahRenderer());
        tblPesanan.getColumnModel().getColumn(4).setCellRenderer(new Theme.StatusRenderer(false));
        tblPesanan.getColumnModel().getColumn(5).setCellRenderer(new Theme.StatusRenderer(true));
    }

    // ================================================================ DATA
    private void muatMenu() {
        cmbMenu.removeAllItems();

        String sql = "SELECT id, nama_menu, harga FROM menu ORDER BY kategori, nama_menu";
        boolean ada = false;

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cmbMenu.addItem(new MenuItem(
                            rs.getInt("id"),
                            rs.getString("nama_menu"),
                            rs.getInt("harga")));
                    ada = true;
                }
            }
        } catch (Exception ex) {
            kesalahan("Gagal memuat menu: " + ex.getMessage());
        }

        btnPesan.setEnabled(ada);
        hitungTotal();
    }

    private void hitungTotal() {
        MenuItem m = (MenuItem) cmbMenu.getSelectedItem();
        if (m == null) {
            lblTotal.setText("-");
            return;
        }
        int jumlah = (Integer) spJumlah.getValue();
        lblTotal.setText(Theme.rupiah(m.harga * jumlah));
    }

    private void pesan() {
        MenuItem m = (MenuItem) cmbMenu.getSelectedItem();
        if (m == null) {
            peringatan("Belum ada menu yang bisa dipesan.");
            return;
        }

        int jumlah = (Integer) spJumlah.getValue();
        int total = m.harga * jumlah;

        int jawab = JOptionPane.showConfirmDialog(this,
                "Pesan " + jumlah + " x " + m.nama + "?\nTotal: " + Theme.rupiah(total),
                "Konfirmasi Pesanan", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "INSERT INTO pesanan (user_id, menu_id, jumlah, total_harga) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setInt(2, m.id);
                ps.setInt(3, jumlah);
                ps.setInt(4, total);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                    "Pesanan berhasil dibuat!\n\n"
                    + jumlah + " x " + m.nama + "\n"
                    + "Total: " + Theme.rupiah(total) + "\n\n"
                    + "Status awal: Menunggu Konfirmasi.",
                    "BI-KANTIN", JOptionPane.INFORMATION_MESSAGE);

            spJumlah.setValue(1);
            muatPesanan(false);
        } catch (Exception ex) {
            kesalahan("Gagal membuat pesanan: " + ex.getMessage());
        }
    }

    private void muatPesanan(boolean diam) {
        String sql = "SELECT p.id, m.nama_menu, p.jumlah, p.total_harga, "
                + "p.status, p.status_bayar, p.created_at "
                + "FROM pesanan p "
                + "JOIN menu m ON m.id = p.menu_id "
                + "WHERE p.user_id = ? "
                + "ORDER BY p.id DESC";

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    modelPesanan.setRowCount(0);

                    while (rs.next()) {
                        modelPesanan.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("nama_menu"),
                            rs.getInt("jumlah"),
                            rs.getInt("total_harga"),
                            rs.getString("status"),
                            rs.getString("status_bayar"),
                            Theme.waktu(rs.getTimestamp("created_at"))
                        });
                    }
                }
            }

            lblJumlahPesanan.setText(modelPesanan.getRowCount() + " pesanan");
            perbaruiBadge();
        } catch (Exception ex) {
            if (!diam) {
                kesalahan("Gagal memuat pesanan: " + ex.getMessage());
            }
        }
    }

    private void perbaruiBadge() {
        if (modelPesanan.getRowCount() == 0) {
            Theme.paintBadge(lblStatusTerakhir, "Belum ada pesanan", Theme.MUTED);
            return;
        }

        String status = String.valueOf(modelPesanan.getValueAt(0, 4));
        String nomor = String.valueOf(modelPesanan.getValueAt(0, 0));
        Theme.paintBadge(lblStatusTerakhir, "Pesanan #" + nomor + " • " + status,
                Theme.statusColor(status));
    }

    private void mulaiTimer() {
        timer = new Timer(5000, e -> muatPesanan(true));
        timer.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (timer != null) {
                    timer.stop();
                }
            }
        });
    }

    // ============================================================== BANTUAN

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "BI-KANTIN",
                JOptionPane.WARNING_MESSAGE);
    }

    private void kesalahan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "BI-KANTIN",
                JOptionPane.ERROR_MESSAGE);
    }

    /** Item combo menu: menyimpan id & harga asli, tampil sebagai "Nama - Rp15.000". */
    private static final class MenuItem {

        private final int id;
        private final String nama;
        private final int harga;

        MenuItem(int id, String nama, int harga) {
            this.id = id;
            this.nama = nama;
            this.harga = harga;
        }

        @Override
        public String toString() {
            return nama + "  -  " + Theme.rupiah(harga);
        }
    }
}
