package pl.jch.test.other;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UrlDef {
    String urlPattern;
    String file;

}
