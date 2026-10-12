package com.hostelfinder.service;

import com.hostelfinder.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/** Saves uploaded images to a local folder (default: ./uploads) and returns a URL like /uploads/<random>.jpg */
@Service
public class FileStorageService {
    private final Path root;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String dir) throws IOException {
        this.root = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    public Path root() { return root; }

    /** Returns the public URL, or null when no file was sent. */
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try (InputStream in = file.getInputStream()) {
            byte[] h = in.readNBytes(12);
            String ext = detectExtension(h);   // decided from the real file bytes, never from the user's file name
            if (ext == null) throw new ApiException(HttpStatus.BAD_REQUEST, "Only JPG, PNG, WEBP or GIF images are allowed");
            String name = UUID.randomUUID() + ext;
            try (InputStream full = file.getInputStream()) { Files.copy(full, root.resolve(name)); }
            return "/uploads/" + name;
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save the image");
        }
    }

    /** Deletes a previously saved upload (best effort). Only files inside the uploads folder can be removed. */
    public void delete(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;
        try {
            Path target = root.resolve(url.substring("/uploads/".length())).normalize();
            if (target.startsWith(root)) Files.deleteIfExists(target);
        } catch (IOException ignored) { /* a leftover file is harmless */ }
    }

    private static String detectExtension(byte[] h) {
        if (h.length >= 3 && (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF) return ".jpg";
        if (h.length >= 4 && (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G') return ".png";
        if (h.length >= 4 && h[0] == 'G' && h[1] == 'I' && h[2] == 'F' && h[3] == '8') return ".gif";
        if (h.length >= 12 && h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F'
                && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P') return ".webp";
        return null;
    }
}
