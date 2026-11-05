# BI-KANTIN

Aplikasi desktop **kantin online sekolah** untuk tugas mandiri
Pemrograman Berbasis Objek — dibuat dengan **Java (NetBeans, Java with Ant)**,
**Swing**, dan **MySQL**.

Tema visual: putih / light dengan aksen gradasi biru `#0602CA` → ungu `#8B0BDD`.

---

## 1. Fitur

| Peran | Bisa apa saja |
|-------|---------------|
| **Admin Sekolah** | CRUD menu (tambah, edit, hapus), melihat semua pesanan, mengubah status pesanan |
| **Kasir / Petugas Pembayaran** | melihat pesanan, memvalidasi pembayaran menjadi **Lunas**, melihat & mencetak struk |
| **Siswa** | login, memilih menu, mengisi jumlah, memesan, memantau status pesanan (otomatis tiap 5 detik) |

Alur status pesanan:

```
Menunggu Konfirmasi  →  Sedang Dimasak  →  Siap Diambil  →  Selesai
```

Alur pembayaran: `Belum Lunas` → (divalidasi kasir) → `Lunas`.

---

## 2. Kebutuhan

- JDK 21 (sudah dipakai project ini)
- NetBeans (bisa versi 26 atau lebih baru)
- MySQL / MariaDB yang menyala di `localhost:3306`
- Driver `mysql-connector-j` sudah ada di folder `lib/`

---

## 3. Setup Database

Buat database `db_bikantin` beserta tabel dan data awalnya dengan **salah satu** cara:

**A. phpMyAdmin**

1. Buka `http://localhost/phpmyadmin`
2. Tab **Import** → pilih file `database/setup.sql` → **Go**

**B. Command line**

```bash
mysql -u root < database/setup.sql
```

**C. WSL / Linux (tanpa XAMPP)**

Kalau MySQL-nya jalan di WSL (bukan XAMPP), root biasanya hanya bisa login
lewat `sudo`. Supaya aplikasi bisa connect seperti di XAMPP (root tanpa password),
jalankan sekali:

```bash
sudo mariadb -e "ALTER USER 'root'@'localhost' IDENTIFIED VIA unix_socket OR mysql_native_password USING PASSWORD('');"
sudo mariadb < database/setup.sql
```

> Kalau tidak mau mengubah user root, buat user khusus lalu sesuaikan
> `USER` dan `PASS` di `src/koneksi/Koneksi.java`.

### Akun untuk uji coba

| Username | Password | Role |
|----------|----------|------|
| `admin_kantin` | `admin123` | admin (pihak sekolah) |
| `kasir_kantin` | `kasir123` | kasir (petugas pembayaran) |
| `budi_siswa` | `123` | siswa |

---

## 4. Cara Menjalankan

**Di NetBeans**

1. `File` → `Open Project` → pilih folder `BIKantin`
2. Klik kanan project → **Run** (atau tekan `F6`)

**Tanpa NetBeans (command line, dari folder project)**

```bash
ant -f build.xml run
```

Atau jalankan file `dist/BIKantin.jar`:

```bash
java -jar dist/BIKantin.jar
```

Cek koneksi database saja (tanpa membuka form):

```
Klik kanan src/koneksi/CekKoneksi.java → Run File (Shift + F6)
```

---

## 5. Struktur Project

```
BIKantin/
├── build.xml                  # build Ant (dibuat NetBeans)
├── manifest.mf
├── database/
│   └── setup.sql              # buat database + tabel + data awal
├── lib/
│   └── mysql-connector-j-26.7.0.jar
├── nbproject/                 # konfigurasi project NetBeans
└── src/
    ├── form/
    │   ├── Main.java          # titik masuk aplikasi
    │   ├── FormLogin.java     # login, pilih form sesuai role
    │   ├── FormAdmin.java     # CRUD menu + ubah status pesanan
    │   ├── FormKasir.java     # validasi pembayaran + struk
    │   ├── FormSiswa.java     # pesan menu + tracking status
    │   └── Theme.java         # warna, font, tombol, tabel
    └── koneksi/
        ├── Koneksi.java       # koneksi JDBC ke db_bikantin
        └── CekKoneksi.java    # tes koneksi
```

---

## 6. Catatan Struktur Database

Struktur mengikuti modul tugas, dengan **tiga tambahan kecil**:

1. `users.role` ditambah nilai `kasir`, karena modul menyebut 3 aktor
   (Admin, Siswa, Kasir).
2. `pesanan.status_bayar` (`Belum Lunas` / `Lunas`) — dibutuhkan untuk
   tugas kasir memvalidasi pembayaran.
3. `pesanan.created_at` — dipakai untuk menampilkan waktu pesanan dan
   menghitung pendapatan kasir hari ini.

Tabel `pesanan` memakai foreign key:

- `menu` tidak bisa dihapus kalau sudah pernah dipesan
  (riwayat pesanan tetap aman),
- pesanan ikut terhapus kalau user-nya dihapus.

---

## 7. Kalau Ada Masalah

| Gejala | Penyebab & solusi |
|--------|-------------------|
| `Koneksi Database Gagal ... Access denied` | User/password MySQL beda. Ubah `USER`/`PASS` di `Koneksi.java`, atau jalankan langkah WSL di atas. |
| `Unknown database 'db_bikantin'` | `database/setup.sql` belum dijalankan. |
| `Communications link failure` | MySQL/MariaDB belum menyala. Nyalakan dulu (`sudo systemctl start mariadb` atau XAMPP → Start MySQL). |
| Data tidak berubah di tabel siswa | Tunggu maksimal 5 detik (ada auto-refresh), atau klik **Muat Ulang**. |
| Menu tidak bisa dihapus | Menu itu sudah pernah dipesan. Itu disengaja agar riwayat pesanan tidak hilang. |
| Huruf berantakan / kotak-kotak | Font `Segoe UI` tidak ada (bukan Windows). Ganti `FONT_FAMILY` di `Theme.java` menjadi `SansSerif`. |
