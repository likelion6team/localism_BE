package com.sku.localism_be.global.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class ByteArrayMultipartFile implements MultipartFile {

  private final String name;
  private final String originalFilename;
  private final String contentType;
  private final byte[] content;

  public ByteArrayMultipartFile(byte[] content, String name, String originalFilename, String contentType) {
    this.content = (content != null) ? content : new byte[0];
    this.name = (name != null) ? name : "file";
    this.originalFilename = (originalFilename != null && !originalFilename.isBlank())
        ? originalFilename : this.name;
    this.contentType = contentType;
  }

  @Override public String getName() { return name; }
  @Override public String getOriginalFilename() { return originalFilename; }
  @Override public String getContentType() { return contentType; }
  @Override public boolean isEmpty() { return content.length == 0; }
  @Override public long getSize() { return content.length; }
  @Override public byte[] getBytes() { return content.clone(); }
  @Override public InputStream getInputStream() { return new ByteArrayInputStream(content); }

  @Override
  public void transferTo(File dest) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(dest)) {
      fos.write(content);
    }
  }
}
