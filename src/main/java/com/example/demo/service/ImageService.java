package com.example.demo.service;

import com.example.demo.files.FileService;
import com.example.demo.model.FileMetadata;
import com.example.demo.model.ImageType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

  private final FileService fileService;
  private final FileMetaDataService fileMetaDataService;
  private final String IMAGINE_FORMAT = "jpg";

  @Autowired
  public ImageService(FileService fileService, FileMetaDataService fileMetaDataService) {
    this.fileService = fileService;
    this.fileMetaDataService = fileMetaDataService;
  }
    @Cacheable(
            value = "imageCache",
            key = "#imageId + '_' + #type.name()"
    )
  public byte[] getProcessedImage(Long imageId, ImageType type) throws IOException {

    FileMetadata fileMetadata = fileMetaDataService.getFileMetadata(imageId);

    byte[] originalImage = fileService.downloadFile(fileMetadata);

    try {
      BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(originalImage));

      if (inputImage.getWidth() <= type.getWidth()) {
        return originalImage;
      }

      BufferedImage resized =
          Scalr.resize(
              inputImage,
              Scalr.Method.QUALITY,
              Scalr.Mode.AUTOMATIC,
              type.getWidth(),
              type.getHeight());

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(resized, IMAGINE_FORMAT, baos);

      return baos.toByteArray();

    } catch (Exception e) {
      throw new RuntimeException("Image processing failed", e);
    }
  }
}
