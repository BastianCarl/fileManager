package com.example.demo.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "zipCache", key = "#files.keySet().hashCode()")
    public byte[] createZip(Map<String, byte[]> files) throws IOException {
        try {
            Thread.sleep(5000);
        }catch (InterruptedException e){

        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
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
