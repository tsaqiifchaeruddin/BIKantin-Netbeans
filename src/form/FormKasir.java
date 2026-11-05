package form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import koneksi.Koneksi;

/**
 * Panel Kasir / Petugas Pembayaran BI-KANTIN.
 *
 * Tugas kasir (petugas kantin, bukan admin sekolah):
 *  - menerima laporan pesanan siswa
 *  - memvalidasi pembayaran menjadi "Lunas"
 *  - menampilkan / mencetak struk
 */
public class FormKasir extends JFrame {

    private final String username;

    private JTable tblPesanan;
    private DefaultTableModel modelPesanan;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtCari;
    private JComboBox<String> cmbFilter;

    private JLabel lblBelumLunas;
    private JLabel lblPendapatan;

    public FormKasir(String username) {
        this.username = username;
        initComponents();
        muatPesanan();
        muatStatistik();
    }

    // ================================================================= FRAME
    private void initComponents() {
        setTitle("BI-KANTIN - Kasir / Petugas Pembayaran");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 620));
        setSize(Theme.ukuranJendela(1180, 740));
        setLocationRelativeTo(null);

        buatTabelPesanan();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(header(), BorderLayout.NORTH);
        root.add(body(), BorderLayout.CENTER);

        JLabel kaki = Theme.muted(
                "BI-KANTIN  •  Kasir  •  validasi pembayaran mengubah status menjadi LUNAS");
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

        JLabel sub = new JLabel("Kasir  •  Petugas Pembayaran Kantin");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(new Color(255, 255, 255, 205));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        kiri.add(brand);
        kiri.add(Box.createVerticalStrut(4));
        kiri.add(sub);

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        kanan.setOpaque(false);

        JLabel siapa = new JLabel("Masuk sebagai " + username);
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
                "Keluar dari panel kasir?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (jawab == JOptionPane.YES_OPTION) {
            dispose();
            new FormLogin().setVisible(true);
        }
    }

    // ================================================================== BODY
    private JComponent body() {
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(Theme.BG);
        body.setBorder(new EmptyBorder(18, 22, 8, 22));

        JPanel atas = new JPanel();
        atas.setLayout(new BoxLayout(atas, BoxLayout.Y_AXIS));
        atas.setOpaque(false);

        lblBelumLunas = Theme.badge("0 pesanan belum lunas", Theme.WARNING);
        lblPendapatan = Theme.badge("Pendapatan lunas hari ini: Rp0", Theme.SUCCESS);

        JPanel statistik = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statistik.setOpaque(false);
        statistik.add(lblBelumLunas);
        statistik.add(lblPendapatan);

        JPanel baris1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        baris1.setOpaque(false);
        baris1.add(Theme.muted("Cari"));
        txtCari = new JTextField(24);
        Theme.styleField(txtCari);
        txtCari.setToolTipText("Cari nama siswa, menu, atau status pembayaran");
        baris1.add(txtCari);

        JPanel baris2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        baris2.setOpaque(false);

        cmbFilter = new JComboBox<>(new String[]{"Semua", "Belum Lunas", "Lunas"});
        cmbFilter.setFont(Theme.FONT);
        cmbFilter.setSelectedIndex(1);
        cmbFilter.addActionListener(e -> pasangFilter());

        JButton btnValidasi = Theme.gradient("Validasi Lunas");
        btnValidasi.addActionListener(e -> validasi());

        JButton btnStruk = Theme.soft("Lihat Struk");
        btnStruk.addActionListener(e -> lihatStruk());

        JButton btnMuat = Theme.soft("Muat Ulang");
        btnMuat.addActionListener(e -> {
            muatPesanan();
            muatStatistik();
        });

        baris2.add(Theme.muted("Tampilkan"));
        baris2.add(cmbFilter);
        baris2.add(btnValidasi);
        baris2.add(btnStruk);
        baris2.add(btnMuat);

        atas.add(statistik);
        atas.add(Box.createVerticalStrut(14));
        atas.add(baris1);
        atas.add(Box.createVerticalStrut(8));
        atas.add(baris2);

        body.add(atas, BorderLayout.NORTH);
        body.add(new JScrollPane(tblPesanan), BorderLayout.CENTER);

        JLabel ket = Theme.muted(
                "Pilih pesanan, lalu klik Validasi Lunas. Struk bisa dicetak lewat tombol Lihat Struk.");
        body.add(ket, BorderLayout.SOUTH);

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                pasangFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                pasangFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                pasangFilter();
            }
        });

        pasangFilter();
        return body;
    }

    private void buatTabelPesanan() {
        modelPesanan = new DefaultTableModel(
                new Object[]{"ID", "Siswa", "Menu", "Jumlah", "Total", "Status", "Bayar", "Waktu"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPesanan = new JTable(modelPesanan);
        Theme.styleTabel(tblPesanan);
        tblPesanan.getColumnModel().getColumn(0).setPreferredWidth(55);
        tblPesanan.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblPesanan.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblPesanan.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblPesanan.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblPesanan.getColumnModel().getColumn(5).setPreferredWidth(165);
        tblPesanan.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblPesanan.getColumnModel().getColumn(7).setPreferredWidth(125);

        tblPesanan.getColumnModel().getColumn(4).setCellRenderer(new Theme.RupiahRenderer());
        tblPesanan.getColumnModel().getColumn(5).setCellRenderer(new Theme.StatusRenderer(false));
        tblPesanan.getColumnModel().getColumn(6).setCellRenderer(new Theme.StatusRenderer(true));

        tblPesanan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(modelPesanan);
        tblPesanan.setRowSorter(sorter);
    }

    // ================================================================ FILTER
    private void pasangFilter() {
        final String kata = txtCari.getText().trim().toLowerCase();
        final String pilihan = (String) cmbFilter.getSelectedItem();

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                boolean cocokKata = kata.isEmpty();
                if (!cocokKata) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        Object nilai = entry.getValue(i);
                        if (nilai != null && nilai.toString().toLowerCase().contains(kata)) {
                            cocokKata = true;
                            break;
                        }
                    }
                }

                boolean cocokFilter = pilihan == null || "Semua".equals(pilihan)
                        || pilihan.equals(entry.getStringValue(6));

                return cocokKata && cocokFilter;
            }
        });
    }

    // ================================================================== DATA
    private void muatPesanan() {
        modelPesanan.setRowCount(0);

        String sql = "SELECT p.id, u.username, m.nama_menu, p.jumlah, p.total_harga, "
                + "p.status, p.status_bayar, p.created_at "
                + "FROM pesanan p "
                + "JOIN users u ON u.id = p.user_id "
                + "JOIN menu  m ON m.id = p.menu_id "
                + "ORDER BY p.id DESC";

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    modelPesanan.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("nama_menu"),
                        rs.getInt("jumlah"),
                        rs.getInt("total_harga"),
                        rs.getString("status"),
                        rs.getString("status_bayar"),
                        Theme.waktu(rs.getTimestamp("created_at"))
                    });
                }
            }
        } catch (Exception ex) {
            kesalahan("Gagal memuat pesanan: " + ex.getMessage());
        }
    }

    private void muatStatistik() {
        String sql = "SELECT "
                + "COALESCE(SUM(CASE WHEN status_bayar = 'Belum Lunas' THEN 1 ELSE 0 END), 0) AS belum, "
                + "COALESCE(SUM(CASE WHEN status_bayar = 'Lunas' "
                + "AND DATE(created_at) = CURDATE() THEN total_harga ELSE 0 END), 0) AS total "
                + "FROM pesanan";

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    Theme.paintBadge(lblBelumLunas,
                            rs.getInt("belum") + " pesanan belum lunas", Theme.WARNING);
                    Theme.paintBadge(lblPendapatan,
                            "Pendapatan lunas hari ini: " + Theme.rupiah(rs.getInt("total")),
                            Theme.SUCCESS);
                }
            }
        } catch (Exception ex) {
            // statistik bukan data kritis, cukup diabaikan
        }
    }

    private void validasi() {
        int row = tblPesanan.getSelectedRow();
        if (row < 0) {
            peringatan("Pilih dulu pesanan yang mau divalidasi.");
            return;
        }

        int id = (Integer) tblPesanan.getValueAt(row, 0);
        String bayar = String.valueOf(tblPesanan.getValueAt(row, 6));

        if ("Lunas".equalsIgnoreCase(bayar)) {
            informasi("Pesanan #" + id + " sudah LUNAS.");
            return;
        }

        int jawab = JOptionPane.showConfirmDialog(this,
                "Tandai pembayaran pesanan #" + id + " sebagai LUNAS?",
                "Validasi Pembayaran", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE pesanan SET status_bayar = 'Lunas' WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            informasi("Pembayaran pesanan #" + id + " berhasil divalidasi.\n"
                    + "Status pembayaran sekarang: LUNAS.");
            muatPesanan();
            muatStatistik();
        } catch (Exception ex) {
            kesalahan("Gagal memvalidasi pembayaran: " + ex.getMessage());
        }
    }

    private void lihatStruk() {
        int row = tblPesanan.getSelectedRow();
        if (row < 0) {
            peringatan("Pilih dulu pesanan yang mau dilihat struknya.");
            return;
        }
        int id = (Integer) tblPesanan.getValueAt(row, 0);

        String sql = "SELECT p.id, u.username, m.nama_menu, p.jumlah, m.harga, "
                + "p.total_harga, p.status, p.status_bayar, p.created_at "
                + "FROM pesanan p "
                + "JOIN users u ON u.id = p.user_id "
                + "JOIN menu  m ON m.id = p.menu_id "
                + "WHERE p.id = ?";

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        peringatan("Pesanan tidak ditemukan.");
                        return;
                    }

                    String garis = "----------------------------------------\n";
                    String struk = "            BI-KANTIN\n"
                            + "       SMK Bina Informatika\n"
                            + garis
                            + "No. Pesanan : #" + rs.getInt("id") + "\n"
                            + "Siswa       : " + rs.getString("username") + "\n"
                            + "Waktu       : " + Theme.waktu(rs.getTimestamp("created_at")) + "\n"
                            + garis
                            + rs.getString("nama_menu") + "\n"
                            + "   " + rs.getInt("jumlah") + " x "
                            + Theme.rupiah(rs.getInt("harga")) + "\n"
                            + garis
                            + "TOTAL       : " + Theme.rupiah(rs.getInt("total_harga")) + "\n"
                            + "Pembayaran  : " + rs.getString("status_bayar") + "\n"
                            + "Status      : " + rs.getString("status") + "\n"
                            + garis
                            + "         Terima kasih!\n";

                    tampilkanStruk(id, struk);
                }
            }
        } catch (Exception ex) {
            kesalahan("Gagal membuka struk: " + ex.getMessage());
        }
    }

    private void tampilkanStruk(int id, String isi) {
        JTextArea ta = new JTextArea(isi);
        ta.setFont(new Font("Consolas", Font.PLAIN, 13));
        ta.setEditable(false);
        ta.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(420, 380));

        JButton cetak = Theme.gradient("Cetak");
        cetak.addActionListener(e -> {
            try {
                ta.print();
            } catch (Exception ex) {
                peringatan("Tidak bisa mencetak: " + ex.getMessage());
            }
        });

        Object[] opsi = {cetak, "Tutup"};
        JOptionPane.showOptionDialog(this, sp, "Struk Pesanan #" + id,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, opsi, opsi[1]);
    }

    // ============================================================== BANTUAN
    private void informasi(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "BI-KANTIN",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "BI-KANTIN",
                JOptionPane.WARNING_MESSAGE);
    }

    private void kesalahan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "BI-KANTIN",
                JOptionPane.ERROR_MESSAGE);
    }
}
