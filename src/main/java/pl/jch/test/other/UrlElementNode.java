package pl.jch.test.other;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
class UrlElementNode<T> {
    Map<String, UrlElementNode<T>> urlElementNodesByPathElement;
    UrlElementNode<T> variableNode;
    T pathHandler;
}
