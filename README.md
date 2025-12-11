# Spring v4.0 Proyek Starter

## Logs

### 6-12-2025

- Membuat sistem (backend) aplikasi
- Membuat test jacoco

### 9-12-2025

- Membuat UI pada aplikasi

### 11-12-2025

- Memperbaiki fitur dan melengkapi fitur serta fungsinya

## 12-12-2025

- Melakukan submit/pengumpulan proyek

## Syntax

### Melakukan Instal Ulang Kebutuhan Paket

command: `mvn clean install`

#### Windows: elakukan build ulang proyek dan membuka hasil laporan
command with open jacoco: `mvn clean test; start target\site\jacoco\index.html`

#### Mac: melakukan build ulang proyek dan membuka hasil laporan
command with open jacoco: `mvn clean test && open target\site\jacoco\index.html`

#### Linux: melakukan build ulang proyek dan membuka hasil laporan
command with open jacoco: `mvn clean test && xdg-open target\site\jacoco\index.html`

### Menjalankan Aplikasi

Command: `mvn spring-boot:run`

URL: http://localhost:8080

### Menjalankan Test Covertage

pre-command: `mvn clean install`

command: `./mvnw test jacoco:report`

command-check: `./mvnw clean test jacoco:check`

## Purpose

Proyek ini dibuat untuk tujuan **Pendidikan**.
