-- ============================================================
-- BI-KANTIN - Setup Database MySQL
--
-- Cara pakai (pilih salah satu):
--   1. Command prompt / terminal:
--        mysql -u root < database/setup.sql
--   2. phpMyAdmin: tab Import -> pilih file ini -> Go
--
-- PERINGATAN: file ini menghapus tabel lama di db_bikantin
-- (DROP TABLE), jadi jangan dijalankan kalau datanya mau disimpan.
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_bikantin
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE db_bikantin;

DROP TABLE IF EXISTS pesanan;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS users;

-- ------------------------------------------------------------
-- Tabel users: admin sekolah & siswa
-- ------------------------------------------------------------
CREATE TABLE users (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  username   VARCHAR(50)  NOT NULL UNIQUE,
  password   VARCHAR(100) NOT NULL,
  role       ENUM('admin', 'siswa', 'kasir') NOT NULL DEFAULT 'siswa',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Tabel menu: daftar makanan / minuman kantin
-- ------------------------------------------------------------
CREATE TABLE menu (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  nama_menu VARCHAR(100) NOT NULL,
  harga     INT          NOT NULL,
  kategori  VARCHAR(30)  NOT NULL DEFAULT 'Makanan'
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Tabel pesanan: transaksi siswa
-- status       : Menunggu Konfirmasi -> Sedang Dimasak
--                -> Siap Diambil -> Selesai
-- status_bayar : Belum Lunas / Lunas (divalidasi admin sekolah)
-- ------------------------------------------------------------
CREATE TABLE pesanan (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  user_id      INT NOT NULL,
  menu_id      INT NOT NULL,
  jumlah       INT NOT NULL DEFAULT 1,
  total_harga  INT NOT NULL,
  status       VARCHAR(30) NOT NULL DEFAULT 'Menunggu Konfirmasi',
  status_bayar VARCHAR(20) NOT NULL DEFAULT 'Belum Lunas',
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pesanan_user
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_pesanan_menu
    FOREIGN KEY (menu_id) REFERENCES menu (id) ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Data awal (sesuai tugas)
-- ------------------------------------------------------------
INSERT INTO users (username, password, role) VALUES
  ('admin_kantin', 'admin123', 'admin'),
  ('kasir_kantin', 'kasir123', 'kasir'),
  ('budi_siswa',   '123',      'siswa');

INSERT INTO menu (nama_menu, harga, kategori) VALUES
  ('Nasi Goreng Spesial', 15000, 'Makanan'),
  ('Es Teh Manis',         4000, 'Minuman');

-- Satu contoh pesanan supaya tabel admin/kasir tidak kosong saat demo.
INSERT INTO pesanan (user_id, menu_id, jumlah, total_harga, status, status_bayar)
SELECT u.id, m.id, 2, m.harga * 2, 'Menunggu Konfirmasi', 'Belum Lunas'
FROM users u, menu m
WHERE u.username = 'budi_siswa' AND m.nama_menu = 'Nasi Goreng Spesial';
