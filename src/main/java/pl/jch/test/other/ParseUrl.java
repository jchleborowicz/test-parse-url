package pl.jch.test.other;

import java.util.function.Function;

public class ParseUrl {

    private static final UrlElementNode<OpenApiFileDef> ROOT_URL_ELEMENT_NODE;

    static {
        ROOT_URL_ELEMENT_NODE = ParseUrlConstructor.readUrlElementNodeTree(Function.identity());
    }

    public static void main(String[] args) {
        final ParseUrl parseUrl = new ParseUrl();
        parseUrl.parsePath("/bikes/my/api/v1/cars/ueothueo/stats");
    }

    public void parsePath(String path) {
        final String[] pathElements = ParseUrlUtils.getPathElements(path);

        UrlElementNode<OpenApiFileDef> currentNode = ROOT_URL_ELEMENT_NODE;
        UrlElementNode<OpenApiFileDef> nextNode;
        for (final String pathElement : pathElements) {
            nextNode = currentNode.getUrlElementNodesByPathElement().get(pathElement);

            if (nextNode == null) {
                nextNode = currentNode.getVariableNode();
            }

            if (nextNode == null) {
                throw new RuntimeException("Path not found: " + path);
            }

            currentNode = nextNode;
        }

        final OpenApiFileDef pathHandler = currentNode.getPathHandler();
        System.out.println(
                "Handling path " + path + " from file " + pathHandler.getFileName()
                        + ", base path: " + pathHandler.getBasePath());
    }

}
