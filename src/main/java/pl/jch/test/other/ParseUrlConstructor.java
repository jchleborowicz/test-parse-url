package pl.jch.test.other;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import static java.util.stream.Collectors.toList;

public class ParseUrlConstructor {

    private static final String URL_DEFS_YAML_FILE = "/url-defs.yaml";

    public static <T> UrlElementNode<T> readUrlElementNodeTree(Function<OpenApiFileDef, T> urlHandlerFunction) {

        final List<OpenApiFileDef> openApiFileDefs = readInputData();

        final TempUrlElementNode tempRootNode = new TempUrlElementNode();
        for (OpenApiFileDef openApiFileDef : openApiFileDefs) {
            addOpenApiFileDef(tempRootNode, openApiFileDef);
        }

        return toUrlNode(tempRootNode, urlHandlerFunction);
    }

    @SuppressWarnings("unchecked")
    private static List<OpenApiFileDef> readInputData() {
        final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        final URL inputUrl = ParseUrlConstructor.class.getResource(URL_DEFS_YAML_FILE);

        final Map<String, Map<String, Object>> input;
        try {
            input = objectMapper.readValue(inputUrl, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return input.entrySet()
                .stream()
                .map(entry -> {
                    final Map<String, Object> value = entry.getValue();
                    final String basePath = "/" + StringUtils.strip((String) value.get("basePath"), "/ ");
                    final Set<String> endpoints = Set.copyOf((Collection<String>) value.get("endpoints"));
                    return OpenApiFileDef.builder()
                            .fileName(entry.getKey())
                            .basePath(basePath)
                            .endpoints(endpoints)
                            .build();
                })
                .collect(toList());
    }

    private static <T> UrlElementNode<T> toUrlNode(TempUrlElementNode tempRootNode,
                                                   Function<OpenApiFileDef, T> urlHandlerFunction) {
        final UrlElementNode.UrlElementNodeBuilder<T> builder = UrlElementNode.builder();

        if (tempRootNode.getFileDef() != null) {
            builder.pathHandler(urlHandlerFunction.apply(tempRootNode.getFileDef()));
        }

        if (tempRootNode.getVariableNode() != null) {
            builder.variableNode(toUrlNode(tempRootNode.getVariableNode(), urlHandlerFunction));
        }


        final Map<String, UrlElementNode<T>> urlElementNodesByPathElement = new HashMap<>();
        if (tempRootNode.getUrlElementNodesByPathElement() != null) {
            tempRootNode.getUrlElementNodesByPathElement().forEach((pathElement, tempUrlElementNode) ->
                    urlElementNodesByPathElement.put(pathElement, toUrlNode(tempUrlElementNode, urlHandlerFunction)));
        }
        builder.urlElementNodesByPathElement(Collections.unmodifiableMap(urlElementNodesByPathElement));

        return builder.build();
    }

    private static void addOpenApiFileDef(TempUrlElementNode tempRootNode,
                                          OpenApiFileDef openApiFileDef) {
        final String[] basePath = ParseUrlUtils.getPathElements(openApiFileDef.getBasePath());

        TempUrlElementNode currentRootNode = tempRootNode;

        for (final String basePathElement : basePath) {
            TempUrlElementNode nextNode =
                    currentRootNode.getUrlElementNodesByPathElement().get(basePathElement);

            if (nextNode == null) {
                nextNode = new TempUrlElementNode();
                currentRootNode.getUrlElementNodesByPathElement().put(basePathElement, nextNode);
            }

            currentRootNode = nextNode;
        }

        for (String endpoint : openApiFileDef.getEndpoints()) {
            addEndpointDef(currentRootNode, endpoint, openApiFileDef);
        }

    }

    private static void addEndpointDef(TempUrlElementNode tempRootNode, String endpoint,
                                       OpenApiFileDef openApiFileDef) {
        final String[] pathElements = ParseUrlUtils.getPathElements(endpoint);

        TempUrlElementNode currentRootNode = tempRootNode;
        for (String pathElement : pathElements) {
            TempUrlElementNode nextNode;

            if (pathElement.startsWith("{")) {
                if (!pathElement.endsWith("}")) {
                    throw new RuntimeException(
                            "Invalid variable path element " + pathElement + ", endpoint " + endpoint);
                }

                nextNode = currentRootNode.getVariableNode();

                if (nextNode == null) {
                    nextNode = new TempUrlElementNode();
                    currentRootNode.setVariableNode(nextNode);
                }
            } else {
                nextNode = currentRootNode.getUrlElementNodesByPathElement().get(pathElement);

                if (nextNode == null) {
                    nextNode = new TempUrlElementNode();
                    currentRootNode.getUrlElementNodesByPathElement().put(pathElement, nextNode);
                }
            }
            currentRootNode = nextNode;
        }

        if (currentRootNode.getFileDef() != null) {
            throw new RuntimeException("Definition conflict for endpoint " + endpoint
                    + ". Definition #1: " + currentRootNode.getFileDef()
                    + ", definition #2: " + openApiFileDef);
        }

        currentRootNode.setFileDef(openApiFileDef);
    }

    @Data
    private static class TempUrlElementNode {
        Map<String, TempUrlElementNode> urlElementNodesByPathElement = new HashMap<>();
        TempUrlElementNode variableNode;
        OpenApiFileDef fileDef;
    }
}
