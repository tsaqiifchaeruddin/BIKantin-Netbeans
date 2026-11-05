package form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import koneksi.Koneksi;

/**
 * Form Login BI-KANTIN.
 * Jika role = admin  -> FormAdmin  (pihak sekolah)
 * Jika role = kasir  -> FormKasir  (petugas pembayaran)
 * Jika role = siswa  -> FormSiswa  (pembeli)
 */
public class FormLogin extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnMasuk;

    public FormLogin() {
        initComponents();
    }

    private void initComponents() {
        setTitle("BI-KANTIN - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(880, 560));
        setSize(Theme.ukuranJendela(980, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(26, 26, 26, 26));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.SURFACE);
        card.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        card.add(brandPanel(), BorderLayout.WEST);
        card.add(formPanel(), BorderLayout.CENTER);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);

        getRootPane().setDefaultButton(btnMasuk);
    }

    // ------------------------------------------------------------------ brand
    private JPanel brandPanel() {
        Theme.GradientPanel p = new Theme.GradientPanel(Theme.PRIMARY, Theme.ACCENT, false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(46, 42, 46, 42));
        p.setPreferredSize(new Dimension(380, 0));

        JLabel logo = new JLabel("BI", JLabel.CENTER);
        logo.setFont(Theme.FONT_H1.deriveFont(Font.BOLD, 30f));
        logo.setForeground(Theme.PRIMARY);
        logo.setBackground(Color.WHITE);
        logo.setOpaque(true);
        logo.setPreferredSize(new Dimension(66, 66));
        logo.setMinimumSize(new Dimension(66, 66));
        logo.setMaximumSize(new Dimension(66, 66));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(logo);

        p.add(Box.createVerticalStrut(22));

        JLabel nama = new JLabel("BI-KANTIN");
        nama.setFont(Theme.FONT_H1.deriveFont(Font.BOLD, 30f));
        nama.setForeground(Color.WHITE);
        nama.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(nama);

        JLabel sekolah = new JLabel("Kantin Online SMK Bina Informatika");
        sekolah.setFont(Theme.FONT_SMALL);
        sekolah.setForeground(new Color(255, 255, 255, 205));
        sekolah.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(sekolah);

        p.add(Box.createVerticalStrut(38));

        p.add(fitur("Pesan menu tanpa perlu antre"));
        p.add(Box.createVerticalStrut(12));
        p.add(fitur("Pantau status pesanan secara berkala"));
        p.add(Box.createVerticalStrut(12));
        p.add(fitur("Pembayaran divalidasi admin sekolah"));

        p.add(Box.createVerticalGlue());

        JLabel kaki = new JLabel("Tugas Mandiri - Pemrograman Berbasis Objek");
        kaki.setFont(Theme.FONT_SMALL);
        kaki.setForeground(new Color(255, 255, 255, 170));
        kaki.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(kaki);

        return p;
    }

    private JLabel fitur(String teks) {
        JLabel l = new JLabel("•  " + teks);
        l.setFont(Theme.FONT);
        l.setForeground(Color.WHITE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ------------------------------------------------------------------- form
    private JPanel formPanel() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(Theme.SURFACE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.SURFACE);
        form.setPreferredSize(new Dimension(400, 430));

        JLabel judul = Theme.title("Masuk ke akun");
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(judul);

        form.add(Box.createVerticalStrut(6));

        JLabel sub = Theme.muted("Gunakan akun yang diberikan oleh admin sekolah.");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(sub);

        form.add(Box.createVerticalStrut(26));

        JLabel l1 = Theme.fieldLabel("Username");
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(l1);
        form.add(Box.createVerticalStrut(6));

        txtUsername = new JTextField();
        Theme.styleField(txtUsername);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(txtUsername);

        form.add(Box.createVerticalStrut(16));

        JLabel l2 = Theme.fieldLabel("Password");
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(l2);
        form.add(Box.createVerticalStrut(6));

        txtPassword = new JPasswordField();
        Theme.styleField(txtPassword);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(txtPassword);

        form.add(Box.createVerticalStrut(24));

        btnMasuk = Theme.gradient("Masuk");
        btnMasuk.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnMasuk.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMasuk.addActionListener(e -> login());
        form.add(btnMasuk);

        form.add(Box.createVerticalStrut(20));

        JLabel demo = new JLabel("<html>"
                + "<b>Akun demo</b><br>"
                + "Admin sekolah: <b>admin_kantin</b> / <b>admin123</b><br>"
                + "Kasir: <b>kasir_kantin</b> / <b>kasir123</b><br>"
                + "Siswa: <b>budi_siswa</b> / <b>123</b>"
                + "</html>");
        demo.setFont(Theme.FONT_SMALL);
        demo.setForeground(Theme.MUTED);
        demo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(demo);

        wrap.add(form);
        return wrap;
    }

    // ------------------------------------------------------------------ login
    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            peringatan("Username dan password wajib diisi.");
            return;
        }

        btnMasuk.setEnabled(false);

        String sql = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";

        try (Connection c = Koneksi.configDB()) {
            if (c == null) {
                return;
            }

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        peringatan("Username atau password salah.");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                        return;
                    }

                    int id = rs.getInt("id");
                    String nama = rs.getString("username");
                    String role = rs.getString("role");

                    dispose();

                    if ("admin".equalsIgnoreCase(role)) {
                        new FormAdmin(nama).setVisible(true);
                    } else if ("kasir".equalsIgnoreCase(role)) {
                        new FormKasir(nama).setVisible(true);
                    } else {
                        new FormSiswa(id, nama).setVisible(true);
                    }
                }
            }
        } catch (Exception ex) {
            peringatan("Gagal login: " + ex.getMessage());
        } finally {
            btnMasuk.setEnabled(true);
        }
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "BI-KANTIN", JOptionPane.WARNING_MESSAGE);
    }
}
