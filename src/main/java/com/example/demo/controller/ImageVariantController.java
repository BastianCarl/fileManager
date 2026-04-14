package com.example.demo.controller;

import com.example.demo.model.ImageType;
import com.example.demo.service.ImageVariantService;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
public class ImageVariantController {

  private final ImageVariantService imageVariantService;

  public ImageVariantController(ImageVariantService imageVariantService) {
    this.imageVariantService = imageVariantService;
  }

  @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
  public byte[] getImage(@PathVariable Long imageId, @RequestParam ImageType type)
      throws IOException {
    return imageVariantService.getProcessedImage(imageId, type);
  }
}
