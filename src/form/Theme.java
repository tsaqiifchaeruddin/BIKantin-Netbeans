package form;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Tema visual aplikasi BI-KANTIN.
 * Light / putih dengan aksen gradasi biru #0602CA -> ungu #8B0BDD.
 */
public final class Theme {

    public static final Color PRIMARY   = new Color(0x06, 0x02, 0xCA);
    public static final Color ACCENT    = new Color(0x8B, 0x0B, 0xDD);
    public static final Color BG        = new Color(0xF6, 0xF7, 0xFB);
    public static final Color SURFACE   = Color.WHITE;
    public static final Color TEXT      = new Color(0x1F, 0x24, 0x30);
    public static final Color MUTED     = new Color(0x6B, 0x72, 0x80);
    public static final Color BORDER    = new Color(0xE3, 0xE6, 0xEE);
    public static final Color SUCCESS   = new Color(0x15, 0x80, 0x3D);
    public static final Color WARNING   = new Color(0xB4, 0x53, 0x09);
    public static final Color DANGER    = new Color(0xC0, 0x39, 0x2B);
    public static final Color SOFT_BLUE = new Color(0xEE, 0xEF, 0xFF);

    private static final String FONT_FAMILY = "Segoe UI";

    public static final Font FONT       = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_BOLD  = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_H1    = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font FONT_H2    = new Font(FONT_FAMILY, Font.BOLD, 18);

    private Theme() {
    }

    public static void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // pakai look and feel default Java
        }
        UIManager.put("Label.font", FONT);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("TextField.font", FONT);
        UIManager.put("PasswordField.font", FONT);
        UIManager.put("TextArea.font", FONT);
        UIManager.put("ComboBox.font", FONT);
        UIManager.put("Spinner.font", FONT);
        UIManager.put("FormattedTextField.font", FONT);
        UIManager.put("Table.font", FONT);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("TabbedPane.font", FONT_BOLD);
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT_BOLD);
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_H1);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_H2);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel text(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL);
        l.setForeground(MUTED);
        return l;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BOLD);
        l.setForeground(TEXT);
        return l;
    }

    public static JPanel card(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)));
        return p;
    }

    public static void styleField(JTextField field) {
        field.setFont(FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(7, 10, 7, 10)));
    }

    public static JButton gradient(String text) {
        return new GradientButton(text, PRIMARY, ACCENT);
    }

    public static JButton soft(String text) {
        return new SolidButton(text, SOFT_BLUE, PRIMARY);
    }

    public static JButton danger(String text) {
        return new SolidButton(text, DANGER, Color.WHITE);
    }

    /** Panel dengan latar gradasi (dipakai untuk header / branding). */
    public static class GradientPanel extends JPanel {
        private final Color from;
        private final Color to;
        private final boolean vertical;

        public GradientPanel(Color from, Color to, boolean vertical) {
            this.from = from;
            this.to = to;
            this.vertical = vertical;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(vertical
                    ? new GradientPaint(0, 0, from, 0, getHeight(), to)
                    : new GradientPaint(0, 0, from, getWidth(), 0, to));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Tombol dengan gradasi + sudut membulat. */
    public static class GradientButton extends JButton {
        private final Color from;
        private final Color to;

        public GradientButton(String text, Color from, Color to) {
            super(text);
            this.from = from;
            this.to = to;
            setForeground(Color.WHITE);
            setFont(FONT_BOLD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 20, 10, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color a = from;
            Color b = to;
            if (getModel().isPressed()) {
                a = from.darker();
                b = to.darker();
            } else if (getModel().isRollover()) {
                a = brighter(from);
                b = brighter(to);
            }
            g2.setPaint(new GradientPaint(0, 0, a, getWidth(), 0, b));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
            g2.dispose();
            super.paintComponent(g);
        }

        private static Color brighter(Color c) {
            return new Color(
                    Math.min(255, c.getRed() + 25),
                    Math.min(255, c.getGreen() + 25),
                    Math.min(255, c.getBlue() + 25));
        }
    }

    /** Tombol berwarna solid dengan sudut membulat. */
    public static class SolidButton extends JButton {

        private final Color dasar;

        public SolidButton(String text, Color dasar, Color teks) {
            super(text);
            this.dasar = dasar;
            setForeground(teks);
            setFont(FONT_BOLD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 18, 10, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color c = dasar;
            if (!isEnabled()) {
                c = new Color(0xE9, 0xEC, 0xF3);
            } else if (getModel().isPressed()) {
                c = dasar.darker();
            } else if (getModel().isRollover()) {
                c = new Color(
                        Math.min(255, dasar.getRed() + 20),
                        Math.min(255, dasar.getGreen() + 20),
                        Math.min(255, dasar.getBlue() + 20));
            }

            g2.setColor(c);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
            g2.dispose();

            super.paintComponent(g);
        }
    }

    /** Badge kecil berwarna untuk status tahapan. */
    public static JLabel badge(String text, Color color) {
        JLabel l = new JLabel();
        paintBadge(l, text, color);
        return l;
    }

    /** Ubah isi & warna badge yang sudah dibuat lewat {@link #badge}. */
    public static void paintBadge(JLabel l, String text, Color color) {
        l.setText("  " + text + "  ");
        l.setFont(FONT_SMALL.deriveFont(Font.BOLD));
        l.setForeground(color);
        l.setOpaque(true);
        l.setBackground(new Color(
                Math.min(255, color.getRed() + 205),
                Math.min(255, color.getGreen() + 205),
                Math.min(255, color.getBlue() + 205)));
        l.setBorder(BorderFactory.createLineBorder(new Color(
                Math.min(255, color.getRed() + 170),
                Math.min(255, color.getGreen() + 170),
                Math.min(255, color.getBlue() + 170))));
    }

    /** Renderer kolom uang: nilai Integer ditampilkan sebagai Rp15.000,00. */
    public static class RupiahRenderer extends javax.swing.table.DefaultTableCellRenderer {

        public RupiahRenderer() {
            setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Object tampil = value;
            if (value instanceof Number angka) {
                // tanpa ",00" supaya kolom tabel tidak sempit
                tampil = rupiah(angka.intValue()).replace(",00", "");
            }
            return super.getTableCellRendererComponent(
                    table, tampil, isSelected, hasFocus, row, column);
        }
    }

    /** Format uang Rupiah: 15000 -> Rp15.000,00 */
    public static String rupiah(int nilai) {
        return java.text.NumberFormat
                .getCurrencyInstance(new java.util.Locale("id", "ID"))
                .format(nilai);
    }

    /** Ukuran jendela yang tetap aman di layar kecil atau DPI besar. */
    public static Dimension ukuranJendela(int lebar, int tinggi) {
        Dimension layar = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension(
                Math.min(lebar, layar.width - 40),
                Math.min(tinggi, layar.height - 40));
    }

    /** Format waktu database: 02-11-2026 14:05 */
    public static String waktu(java.sql.Timestamp ts) {
        if (ts == null) {
            return "-";
        }
        return ts.toLocalDateTime()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    /** Tampilan tabel yang rapi dan konsisten di semua form. */
    public static void styleTabel(javax.swing.JTable t) {
        t.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.setRowHeight(38);
        t.setFont(FONT);
        t.setShowVerticalLines(false);
        t.setGridColor(BORDER);
        t.setIntercellSpacing(new java.awt.Dimension(0, 1));
        t.setSelectionBackground(SOFT_BLUE);
        t.setSelectionForeground(TEXT);
        t.setFillsViewportHeight(true);

        javax.swing.table.JTableHeader h = t.getTableHeader();
        h.setFont(FONT_BOLD);
        h.setBackground(new Color(0xF0, 0xF2, 0xF8));
        h.setForeground(TEXT);
        h.setReorderingAllowed(false);
        h.setPreferredSize(new java.awt.Dimension(0, 38));
    }

    /** Warna untuk status pesanan BI-KANTIN. */
    public static Color statusColor(String status) {
        if (status == null) {
            return MUTED;
        }
        switch (status) {
            case "Menunggu Konfirmasi":
                return WARNING;
            case "Sedang Dimasak":
                return new Color(0x1D, 0x4E, 0xD8);
            case "Siap Diambil":
                return PRIMARY;
            case "Selesai":
                return SUCCESS;
            default:
                return MUTED;
        }
    }

    /** Warna untuk status pembayaran. */
    public static Color bayarColor(String status) {
        return "Lunas".equalsIgnoreCase(status) ? SUCCESS : DANGER;
    }

    /**
     * Renderer tabel: mewarnai teks sesuai status pesanan / pembayaran.
     * new StatusRenderer(false) untuk kolom status, true untuk kolom bayar.
     */
    public static class StatusRenderer extends javax.swing.table.DefaultTableCellRenderer {

        private final boolean pembayaran;

        public StatusRenderer(boolean pembayaran) {
            this.pembayaran = pembayaran;
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            java.awt.Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String teks = value == null ? "" : value.toString();
            if (!isSelected) {
                setForeground(pembayaran ? bayarColor(teks) : statusColor(teks));
            }
            setFont(FONT_SMALL.deriveFont(Font.BOLD));
            return c;
        }
    }

    public static void opaque(JComponent component, Color color) {
        component.setOpaque(true);
        component.setBackground(color);
    }
}
