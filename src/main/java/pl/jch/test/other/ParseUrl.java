package pl.jch.test.other;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

public class ParseUrl {
    public static void main(String[] args) {
        new ParseUrl().parsePath(new HttpRequest("/cars/v1/api/cars"));
    }

    private static final UrlElementNode rootNode;

    static {
        rootNode = UrlElementNode.builder()
                .pathHandler()
                .build();
    }

    public void parsePath(HttpRequest request) {
        final String strippedPath = StringUtils.strip(request.getPath(), "/");

        final String[] pathElements = strippedPath.split("/");

        UrlElementNode currentNode =
        for (int i = 0; i < pathElements.length; i++) {
            final String pathElement = pathElements[i];

        }
    }

    @Value
    @Builder
    static class UrlElementNode {
        Map<String, UrlElementNode> urlElementNodesByPathElement;
        UrlElementNode variableNode;
        PathHandler pathHandler;
    }

    static interface PathHandler {
        void handle(String path);
    }

    @Value
    @AllArgsConstructor
    static class HttpRequest {
        String path;
    }
}
