package com.example.demo.repository.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class FileAuditState {

  @Id private UUID id;

  @Enumerated(EnumType.STRING)
  private FileProcessingStep step;

  private String code;
}
