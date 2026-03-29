package com.example.demo.controller;

import com.example.demo.model.ImageType;
import com.example.demo.service.ImageService;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
public class ImageController {

  private final ImageService imageService;

  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
  public byte[] getImage(@PathVariable Long imageId, @RequestParam ImageType type)
      throws IOException {
    return imageService.getProcessedImage(imageId, type);
  }
}
