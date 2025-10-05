package com.example.demo.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class Archiver {
    @Value("#{'${accepted.archive.types}'.split(',')}")
    private  Set<String> ACCEPTED_ARCHIVE_TYPES;

    public static byte[] createZip(Map<String, byte[]> files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        // use streams
        for (var entry : files.entrySet()) {
            ZipEntry element = new ZipEntry(entry.getKey());
            zos.putNextEntry(element);
            zos.write(entry.getValue());
            zos.closeEntry();
        }
        zos.close();
        return baos.toByteArray();
    }

    public boolean isArchiveTypeAccepted(String type) {
        return ACCEPTED_ARCHIVE_TYPES.contains(type);
    }
}
