package form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
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
 * Panel Admin Sekolah BI-KANTIN.
 *
 * Tugas admin (pihak sekolah):
 *  - mengelola daftar menu (tambah / edit / hapus)
 *  - mengubah status pesanan siswa
 *
 * Validasi pembayaran dan cetak struk ditangani Kasir (FormKasir).
 */
public class FormAdmin extends JFrame {

    private final String username;

    // ---- tab menu ----
    private JTable tblMenu;
    private DefaultTableModel modelMenu;
    private JTextField txtNama;
    private JTextField txtHarga;
    private JComboBox<String> cmbKategori;
    private JLabel lblJudulForm;
    private JLabel lblMenuInfo;
    private JButton btnSimpan;
    private JButton btnHapus;
    private Integer idEdit = null;

    // ---- tab pesanan ----
    private JTable tblPesanan;
    private DefaultTableModel modelPesanan;
    private TableRowSorter<DefaultTableModel> sorterPesanan;
    private JTextField txtCari;
    private JComboBox<String> cmbStatus;
    private JLabel lblJumlahPesanan;

    private static final String[] STATUS_PESANAN = {
        "Menunggu Konfirmasi", "Sedang Dimasak", "Siap Diambil", "Selesai"
    };

    public FormAdmin(String username) {
        this.username = username;
        initComponents();
        muatMenu();
        muatPesanan();
        bersihkanFormMenu();
    }

    // ================================================================= FRAME
    private void initComponents() {
        setTitle("BI-KANTIN - Panel Admin Sekolah");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 620));
        setSize(Theme.ukuranJendela(1180, 740));
        setLocationRelativeTo(null);

        buatTabelMenu();
        buatTabelPesanan();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(header(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_BOLD);
        tabs.addTab("  Kelola Menu  ", tabMenu());
        tabs.addTab("  Pesanan Masuk  ", tabPesanan());

        JPanel tengah = new JPanel(new BorderLayout());
        tengah.setBackground(Theme.BG);
        tengah.setBorder(new EmptyBorder(12, 22, 8, 22));
        tengah.add(tabs, BorderLayout.CENTER);
        root.add(tengah, BorderLayout.CENTER);

        JLabel kaki = Theme.muted("BI-KANTIN  •  Panel Admin Sekolah  •  data tersimpan di MySQL db_bikantin");
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

        JLabel sub = new JLabel("Panel Admin Sekolah  •  SMK Bina Informatika");
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
                "Keluar dari panel admin?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (jawab == JOptionPane.YES_OPTION) {
            dispose();
            new FormLogin().setVisible(true);
        }
    }

    // ============================================================ TAB MENU
    private JComponent tabMenu() {
        JPanel p = new JPanel(new BorderLayout(18, 0));
        p.setBackground(Theme.BG);
        p.setBorder(new EmptyBorder(16, 4, 4, 4));

        p.add(panelFormMenu(), BorderLayout.WEST);

        JPanel kanan = new JPanel(new BorderLayout(0, 10));
        kanan.setOpaque(false);

        JPanel judul = new JPanel(new BorderLayout());
        judul.setOpaque(false);
        judul.add(Theme.heading("Daftar Menu Kantin"), BorderLayout.WEST);

        lblMenuInfo = Theme.muted("");
        judul.add(lblMenuInfo, BorderLayout.EAST);
        kanan.add(judul, BorderLayout.NORTH);

        kanan.add(new JScrollPane(tblMenu), BorderLayout.CENTER);

        JLabel ket = Theme.muted(
                "Klik satu baris untuk mengedit. Menu yang sudah pernah dipesan tidak bisa dihapus.");
        kanan.add(ket, BorderLayout.SOUTH);

        p.add(kanan, BorderLayout.CENTER);
        return p;
    }

    private JComponent panelFormMenu() {
        JPanel card = Theme.card(new BorderLayout());
        card.setPreferredSize(new Dimension(330, 0));

        JPanel isi = new JPanel();
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setOpaque(false);

        lblJudulForm = Theme.heading("Tambah Menu");
        lblJudulForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(lblJudulForm);
        isi.add(Box.createVerticalStrut(4));

        JLabel sub = Theme.muted("Isi data menu lalu klik Simpan.");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(sub);
        isi.add(Box.createVerticalStrut(18));

        txtNama = new JTextField();
        Theme.styleField(txtNama);
        tambahField(isi, "Nama Menu", txtNama);

        txtHarga = new JTextField();
        Theme.styleField(txtHarga);
        tambahField(isi, "Harga (angka)", txtHarga);

        cmbKategori = new JComboBox<>(new String[]{"Makanan", "Minuman", "Snack"});
        cmbKategori.setFont(Theme.FONT);
        cmbKategori.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbKategori.setAlignmentX(Component.LEFT_ALIGNMENT);
        tambahField(isi, "Kategori", cmbKategori);

        isi.add(Box.createVerticalStrut(2));

        btnSimpan = Theme.gradient("Simpan Menu");
        btnSimpan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSimpan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSimpan.addActionListener(e -> simpanMenu());
        isi.add(btnSimpan);

        isi.add(Box.createVerticalStrut(8));

        JPanel baris = new JPanel(new GridLayout(1, 2, 8, 0));
        baris.setOpaque(false);
        baris.setAlignmentX(Component.LEFT_ALIGNMENT);
        baris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnHapus = Theme.danger("Hapus");
        btnHapus.addActionListener(e -> hapusMenu());

        JButton btnBersih = Theme.soft("Bersihkan");
        btnBersih.addActionListener(e -> bersihkanFormMenu());

        baris.add(btnHapus);
        baris.add(btnBersih);
        isi.add(baris);

        isi.add(Box.createVerticalStrut(16));

        JLabel catatan = new JLabel("<html>"
                + "Harga ditulis tanpa titik,<br>"
                + "contoh <b>15000</b> untuk Rp15.000."
                + "</html>");
        catatan.setFont(Theme.FONT_SMALL);
        catatan.setForeground(Theme.MUTED);
        catatan.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(catatan);

        card.add(isi, BorderLayout.NORTH);
        return card;
    }

    private void tambahField(JPanel form, String label, JComponent field) {
        JLabel l = Theme.fieldLabel(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(l);
        form.add(Box.createVerticalStrut(6));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        form.add(field);
        form.add(Box.createVerticalStrut(14));
    }

    private void buatTabelMenu() {
        modelMenu = new DefaultTableModel(
                new Object[]{"ID", "Nama Menu", "Kategori", "Harga"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMenu = new JTable(modelMenu);
        Theme.styleTabel(tblMenu);
        tblMenu.getColumnModel().getColumn(0).setPreferredWidth(55);
        tblMenu.getColumnModel().getColumn(1).setPreferredWidth(260);
        tblMenu.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblMenu.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblMenu.getColumnModel().getColumn(3).setCellRenderer(new Theme.RupiahRenderer());
        tblMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblMenu.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pilihMenu();
            }
        });
    }

    private void muatMenu() {
        modelMenu.setRowCount(0);

        String sql = "SELECT id, nama_menu, kategori, harga FROM menu ORDER BY kategori, nama_menu";

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    modelMenu.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama_menu"),
                        rs.getString("kategori"),
                        rs.getInt("harga")
                    });
                }
            }
            lblMenuInfo.setText(modelMenu.getRowCount() + " menu");
        } catch (Exception ex) {
            kesalahan("Gagal memuat menu: " + ex.getMessage());
        }
    }

    private void pilihMenu() {
        int row = tblMenu.getSelectedRow();
        if (row < 0) {
            return;
        }

        idEdit = (Integer) modelMenu.getValueAt(row, 0);
        txtNama.setText(String.valueOf(modelMenu.getValueAt(row, 1)));
        txtHarga.setText(String.valueOf(modelMenu.getValueAt(row, 3)));
        cmbKategori.setSelectedItem(String.valueOf(modelMenu.getValueAt(row, 2)));

        lblJudulForm.setText("Edit Menu");
        btnSimpan.setText("Simpan Perubahan");
        btnHapus.setEnabled(true);
    }

    private void simpanMenu() {
        String nama = txtNama.getText().trim();
        String hargaBersih = txtHarga.getText().trim().replaceAll("[^0-9]", "");
        String kategori = (String) cmbKategori.getSelectedItem();

        if (nama.isEmpty()) {
            peringatan("Nama menu wajib diisi.");
            txtNama.requestFocus();
            return;
        }
        if (hargaBersih.isEmpty()) {
            peringatan("Harga wajib diisi dengan angka.");
            txtHarga.requestFocus();
            return;
        }

        int harga;
        try {
            harga = Integer.parseInt(hargaBersih);
        } catch (NumberFormatException ex) {
            peringatan("Harga harus berupa angka, contoh 15000.");
            return;
        }
        if (harga <= 0) {
            peringatan("Harga harus lebih besar dari 0.");
            return;
        }

        boolean baru = (idEdit == null);
        String sql = baru
                ? "INSERT INTO menu (nama_menu, harga, kategori) VALUES (?, ?, ?)"
                : "UPDATE menu SET nama_menu = ?, harga = ?, kategori = ? WHERE id = ?";

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, nama);
                ps.setInt(2, harga);
                ps.setString(3, kategori);
                if (!baru) {
                    ps.setInt(4, idEdit);
                }
                ps.executeUpdate();
            }

            informasi(baru
                    ? "Menu \"" + nama + "\" berhasil ditambahkan."
                    : "Menu \"" + nama + "\" berhasil diperbarui.");

            bersihkanFormMenu();
            muatMenu();
            muatPesanan();   // nama menu di tabel pesanan ikut berubah
        } catch (Exception ex) {
            kesalahan("Gagal menyimpan menu: " + ex.getMessage());
        }
    }

    private void hapusMenu() {
        int row = tblMenu.getSelectedRow();
        if (row < 0) {
            peringatan("Pilih dulu menu yang ingin dihapus.");
            return;
        }

        int id = (Integer) modelMenu.getValueAt(row, 0);
        String nama = String.valueOf(modelMenu.getValueAt(row, 1));

        int jawab = JOptionPane.showConfirmDialog(this,
                "Hapus menu \"" + nama + "\" dari daftar?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM menu WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            informasi("Menu \"" + nama + "\" berhasil dihapus.");
            bersihkanFormMenu();
            muatMenu();
        } catch (SQLIntegrityConstraintViolationException ex) {
            peringatan("Menu \"" + nama + "\" sudah pernah dipesan, jadi tidak bisa dihapus.\n"
                    + "Riwayat pesanan siswa tetap tersimpan.");
        } catch (Exception ex) {
            kesalahan("Gagal menghapus menu: " + ex.getMessage());
        }
    }

    private void bersihkanFormMenu() {
        idEdit = null;
        txtNama.setText("");
        txtHarga.setText("");
        cmbKategori.setSelectedIndex(0);

        if (lblJudulForm != null) {
            lblJudulForm.setText("Tambah Menu");
        }
        if (btnSimpan != null) {
            btnSimpan.setText("Simpan Menu");
            btnSimpan.setEnabled(true);
        }
        if (btnHapus != null) {
            btnHapus.setEnabled(false);
        }
        if (tblMenu != null) {
            tblMenu.clearSelection();
        }
        if (txtNama != null) {
            txtNama.requestFocus();
        }
    }

    // ========================================================= TAB PESANAN
    private JComponent tabPesanan() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Theme.BG);
        p.setBorder(new EmptyBorder(16, 4, 4, 4));

        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setOpaque(false);

        JPanel baris1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        baris1.setOpaque(false);

        lblJumlahPesanan = Theme.muted("0 pesanan");
        baris1.add(Theme.muted("Cari"));
        txtCari = new JTextField(22);
        Theme.styleField(txtCari);
        txtCari.setToolTipText("Cari nama siswa, menu, status, atau pembayaran");
        baris1.add(txtCari);
        baris1.add(Box.createHorizontalStrut(8));
        baris1.add(lblJumlahPesanan);

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            private void saring() {
                String kata = txtCari.getText().trim();
                if (kata.isEmpty()) {
                    sorterPesanan.setRowFilter(null);
                } else {
                    sorterPesanan.setRowFilter(
                            RowFilter.regexFilter("(?i)" + Pattern.quote(kata)));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                saring();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                saring();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                saring();
            }
        });

        JPanel baris2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        baris2.setOpaque(false);

        cmbStatus = new JComboBox<>(new DefaultComboBoxModel<>(STATUS_PESANAN));
        cmbStatus.setFont(Theme.FONT);

        JButton btnUbah = Theme.gradient("Ubah Status");
        btnUbah.addActionListener(e -> ubahStatus());

        JButton btnMuat = Theme.soft("Muat Ulang");
        btnMuat.addActionListener(e -> {
            muatPesanan();
            muatMenu();
        });

        baris2.add(Theme.muted("Status baru"));
        baris2.add(cmbStatus);
        baris2.add(btnUbah);
        baris2.add(btnMuat);

        bar.add(baris1);
        bar.add(Box.createVerticalStrut(10));
        bar.add(baris2);

        p.add(bar, BorderLayout.NORTH);
        p.add(new JScrollPane(tblPesanan), BorderLayout.CENTER);

        JLabel ket = Theme.muted(
                "Alur status: Menunggu Konfirmasi → Sedang Dimasak → Siap Diambil → Selesai.");
        p.add(ket, BorderLayout.SOUTH);

        return p;
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

        sorterPesanan = new TableRowSorter<>(modelPesanan);
        tblPesanan.setRowSorter(sorterPesanan);

        tblPesanan.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = tblPesanan.getSelectedRow();
            if (row >= 0) {
                Object status = tblPesanan.getValueAt(row, 5);
                if (status != null) {
                    cmbStatus.setSelectedItem(status.toString());
                }
            }
        });
    }

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
            lblJumlahPesanan.setText(modelPesanan.getRowCount() + " pesanan");
        } catch (Exception ex) {
            kesalahan("Gagal memuat pesanan: " + ex.getMessage());
        }
    }

    private void ubahStatus() {
        int id = idPesananTerpilih();
        if (id < 0) {
            return;
        }

        String baru = (String) cmbStatus.getSelectedItem();

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE pesanan SET status = ? WHERE id = ?")) {
                ps.setString(1, baru);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
            informasi("Status pesanan #" + id + " diubah menjadi \"" + baru + "\".");
            muatPesanan();
        } catch (Exception ex) {
            kesalahan("Gagal mengubah status: " + ex.getMessage());
        }
    }

    private int idPesananTerpilih() {
        int row = tblPesanan.getSelectedRow();
        if (row < 0) {
            peringatan("Pilih dulu satu pesanan di tabel.");
            return -1;
        }
        return (Integer) tblPesanan.getValueAt(row, 0);
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
