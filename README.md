# PROYECTO LIKEQUEST

Este proyecto consiste en una aplicación con un **Backend en Java (Spring Boot)** y un **Frontend en Flutter**, conectados a una base de datos **MySQL**.

---

## Requisitos previos

Asegúrate de tener instalado lo siguiente antes de empezar:

- Java 17+ e IntelliJ IDEA
- Flutter SDK y Visual Studio Code
- MySQL Server (8.0 o superior)
- Navegador Chrome (para ejecutar el frontend)
-

---

## Estructura del proyecto

```
PROYECTO-LIKEQUEST/
├── LIKEQUEST_BACKEND/     # Backend en Java Spring Boot
├── FRONTED_LIKEQUEST/     # Frontend en Flutter
└── likequest_schema.sql   # Script de creación de la base de datos
```

---

## Pasos para ejecutar el proyecto

### 1.Base de Datos (MySQL)

Antes de iniciar las aplicaciones, es necesario configurar la base de datos:

1. Abre tu gestor de MySQL (MySQL Workbench)
2. Importa y ejecuta el archivo `likequest_schema.sql` incluido en este repositorio
3. **IMPORTANTE:** El sistema busca una base de datos llamada `likequest`. No cambies el nombre
4. Credenciales por defecto:
   - Usuario: `root`
   - Contraseña: `root`
   - Si tus credenciales son distintas, edítalas en:
     `src/main/java/com/tiktokgame/likequest_backend/controler/database/DBControler.java`

---

### 2. Backend (Java Spring Boot)

1. Descomprime la carpeta `LIKEQUEST_BACKEND`
2. Ábrela en IntelliJ IDEA (`File → Open → selecciona la carpeta`)
3. Espera a que IntelliJ descargue automáticamente las dependencias de Maven (verás una barra de progreso en la parte inferior). Esto puede tardar unos minutos la primera vez
4. Busca y abre la clase principal:
   `src/main/java/com/tiktokgame/likequest_backend/LikestBackendApplication.java`
5. Pulsa el botón ▶ **Run** (o `Shift + F10`)
6. El servidor arrancará en: **http://localhost:8080**

> Asegúrate de que la base de datos esté corriendo antes de iniciar el backend.

---

### 3. Frontend (Flutter)

1. Descomprime la carpeta `FRONTED_LIKEQUEST`
2. Ábrela en Visual Studio Code (`File → Open Folder → selecciona la carpeta`)
3. Abre una terminal en VSCode (`Ctrl + ñ`) y ejecuta:
   ```bash
   flutter pub get
   ```
   Esto descargará todas las dependencias del proyecto
4. Asegúrate de tener **Chrome** seleccionado como dispositivo (esquina inferior derecha de VSCode)
5. Abre el archivo `lib/main.dart` y pulsa ▶ **Run** (o `F5`)
6. La aplicación se abrirá automáticamente en Chrome

> Asegúrate de que el backend esté corriendo en http://localhost:8080 antes de iniciar el frontend.

---

## Orden de arranque recomendado

```
1. MySQL  →  2. Backend (Spring Boot)  →  3. Frontend (Flutter)
```

---

## Problemas comunes

| Problema | Solución |
|----------|----------|
| El backend no conecta con la base de datos | Verifica que MySQL esté corriendo y que las credenciales en `DBControler.java` sean correctas |
| El frontend no carga datos | Verifica que el backend esté corriendo en `http://localhost:8080` |
| Flutter no reconoce los paquetes | Ejecuta `flutter pub get` en la terminal dentro de la carpeta del frontend |
| El procesamiento de vídeo no funciona | Verifica que FFmpeg y yt-dlp estén correctamente colocados en la carpeta `tools` dentro del backend |
