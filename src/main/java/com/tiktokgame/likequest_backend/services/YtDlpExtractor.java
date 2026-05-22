package com.tiktokgame.likequest_backend.services;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class YtDlpExtractor {

    private final String YT_DLP_PATH = System.getProperty("user.dir") + "/tools/yt-dlp.exe";
    private final String FFMPEG_PATH = System.getProperty("user.dir") + "/tools/ffmpeg.exe";

    public String downloadVideo(String tiktokUrl, String username, int ronda) {
        try {
            String outputFolder = System.getProperty("user.dir") + "/src/main/resources/static/videos/";
            new java.io.File(outputFolder).mkdirs();

            String tempPath = outputFolder + "temp_ronda_" + ronda + ".mp4";
            String finalPath = outputFolder + "ronda_" + ronda + ".mp4";

            // Borra archivos previos
            new java.io.File(tempPath).delete();
            new java.io.File(finalPath).delete();

            // Paso 1: yt-dlp descarga en temp
            List<String> dlCommand = new ArrayList<>();
            dlCommand.add(YT_DLP_PATH);
            dlCommand.add("-o");
            dlCommand.add(tempPath);
            dlCommand.add("--format");
            dlCommand.add("best");
            dlCommand.add(tiktokUrl);

            ProcessBuilder pb1 = new ProcessBuilder(dlCommand);
            pb1.redirectErrorStream(true);
            Process p1 = pb1.start();
            new BufferedReader(new InputStreamReader(p1.getInputStream()))
                    .lines().forEach(l -> System.out.println("[yt-dlp] " + l));
            p1.waitFor();

            // Paso 2: ffmpeg convierte a H.264
            List<String> ffCommand = new ArrayList<>();
            ffCommand.add(FFMPEG_PATH);
            ffCommand.add("-i");
            ffCommand.add(tempPath);
            ffCommand.add("-vcodec");
            ffCommand.add("libx264");
            ffCommand.add("-preset");
            ffCommand.add("ultrafast");  // ← velocidad máxima, algo menos de compresión
            ffCommand.add("-crf");
            ffCommand.add("28");          // ← calidad (18=alta, 28=media, 35=baja)
            ffCommand.add("-acodec");
            ffCommand.add("aac");
            ffCommand.add("-y");
            ffCommand.add(finalPath);

            ProcessBuilder pb2 = new ProcessBuilder(ffCommand);
            pb2.redirectErrorStream(true);
            Process p2 = pb2.start();
            new BufferedReader(new InputStreamReader(p2.getInputStream()))
                    .lines().forEach(l -> System.out.println("[ffmpeg] " + l));
            int exitCode = p2.waitFor();

            new java.io.File(tempPath).delete(); // limpia el temp

            if (exitCode == 0) {
                System.out.println("✅ Descargado y convertido: ronda_" + ronda + ".mp4");
                return "http://localhost:8080/videos/ronda_" + ronda + ".mp4";
            }
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
        return null;
    }

    public void clearVideosFolder() {
        try {
            String outputFolder = System.getProperty("user.dir") + "/src/main/resources/static/videos/";
            java.io.File folder = new java.io.File(outputFolder);

            if (folder.exists()) {
                for (java.io.File file : folder.listFiles()) {
                    file.delete();
                }
                System.out.println("🗑Carpeta de videos limpiada");
            } else {
                folder.mkdirs();
                System.out.println("Carpeta de videos creada");
            }
        } catch (Exception e) {
            System.err.println("Error limpiando carpeta: " + e.getMessage());
        }
    }

    public void deleteVideoIfExists(int ronda) {
        String outputFolder = System.getProperty("user.dir") + "/src/main/resources/static/videos/";
        java.io.File folder = new java.io.File(outputFolder);
        if (!folder.exists()) return;

        for (java.io.File f : folder.listFiles()) {
            if (f.getName().startsWith("ronda_" + ronda + "_")) {
                f.delete();
                System.out.println("🗑️ Borrado previo: " + f.getName());
            }
        }
    }
}
