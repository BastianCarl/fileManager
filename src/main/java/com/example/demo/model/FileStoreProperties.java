package com.example.demo.model;
import java.util.Set;

public class FileStoreProperties {
     public static final String basePath = "./files";
     public static final Set<String> allowedMimeTypes = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
}
