package school.hei.stdgrade.file.transcript;

import org.springframework.http.MediaType;

public record GeneratedFile(byte[] content, String filename, MediaType contentType) {}
