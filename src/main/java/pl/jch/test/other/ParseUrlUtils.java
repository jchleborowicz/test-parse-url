package pl.jch.test.other;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.notBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParseUrlUtils {
    public static String[] getPathElements(String path) {
        requireNonNull(path);
        final String strippedPath = notBlank(StringUtils.strip(path, "/ "));
        return strippedPath.split("/");
    }
}
