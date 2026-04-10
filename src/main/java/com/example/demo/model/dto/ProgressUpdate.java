package com.example.demo.model.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProgressUpdate {
  private String uuid;
  private String status;
  private int progress; // 0-100
  private String message;
}
