package com.example.demo.utility;

import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class UriBuilderService {

  public String buildFileLocation(UUID id) {
    return ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/files/{id}")
        .buildAndExpand(id)
        .toUriString();
  }
}
