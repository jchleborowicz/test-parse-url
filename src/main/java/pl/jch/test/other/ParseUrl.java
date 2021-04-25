package pl.jch.test.other;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Value;

public class ParseUrl {

    private static final UrlElementNode<OpenApiFileDef> ROOT_URL_ELEMENT_NODE;

    static {
        ROOT_URL_ELEMENT_NODE = ParseUrlConstructor.readUrlElementNodeTree(Function.identity());
    }

    public static void main(String[] args) {
        final ParseUrl parseUrl = new ParseUrl();
        parseUrl.parsePath(new HttpRequest("/bikes/api/v1/cars/ueothueo/stats"));
    }

    public void parsePath(HttpRequest request) {
        final String[] pathElements = ParseUrlUtils.getPathElements(request.getPath());

        UrlElementNode<OpenApiFileDef> currentNode = ROOT_URL_ELEMENT_NODE;
        UrlElementNode<OpenApiFileDef> nextNode;
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

        System.out.println(
                "Handling path " + request.getPath() + " from file " + currentNode.getPathHandler().getFileName());
    }

    @Value
    @AllArgsConstructor
    static class HttpRequest {
        String path;
    }
}
