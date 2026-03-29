package com.example.demo.model;

public enum ImageType {
  THUMBNAIL(150, 150),
  MAIN_LIST(600, 600),
  FULL_HD(1920, 1080);

  private final int width;
  private final int height;

  ImageType(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
