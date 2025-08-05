package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RelationshipHandlerFactory {
    private final Map<RelationshipType, RelationshipHandler> handlers;

    @Autowired
    public RelationshipHandlerFactory(List<RelationshipHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toUnmodifiableMap(RelationshipHandler::getHandledType, Function.identity()));
    }

    public RelationshipHandler getHandler(RelationshipType type) {
        RelationshipHandler handler = handlers.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException("Handler not implemented for relationship type: " + type);
        }
        return handler;
    }
}