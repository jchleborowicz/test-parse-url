package pl.jch.test.other;

import java.util.Set;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OpenApiFileDef {
    String fileName;
    String basePath;
    Set<String> endpoints;
}
