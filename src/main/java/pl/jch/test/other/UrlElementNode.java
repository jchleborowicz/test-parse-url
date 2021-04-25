package pl.jch.test.other;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
class UrlElementNode {
    Map<String, UrlElementNode> urlElementNodesByPathElement;
    UrlElementNode variableNode;
    PathHandler pathHandler;
}
