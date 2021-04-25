package pl.jch.test.other;

import lombok.AllArgsConstructor;
import lombok.Value;

public class ParseUrl {

    public static void main(String[] args) {
        new ParseUrl().parsePath(new HttpRequest("/bikes/api/v1/cars/ueothueo/stats"));
    }

    private static final UrlElementNode rootNode = ParseUrlConstructor.readUrlElementNodeTree();

    public void parsePath(HttpRequest request) {
        final String[] pathElements = ParseUrlUtils.getPathElements(request.getPath());

        UrlElementNode currentNode = rootNode;
        UrlElementNode nextNode;
        for (final String pathElement : pathElements) {
            nextNode = currentNode.getUrlElementNodesByPathElement().get(pathElement);

            if (nextNode == null) {
                nextNode = currentNode.getVariableNode();
            }

            if (nextNode == null) {
                throw new RuntimeException("Path not found: " + request.getPath());
            }

            currentNode = nextNode;
        }

        currentNode.getPathHandler().handle(request.getPath());
    }

    @Value
    @AllArgsConstructor
    static class HttpRequest {
        String path;
    }
}
