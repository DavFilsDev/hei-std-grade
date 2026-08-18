package school.hei.stdgrade.endpoint.event.consumer.model;

import school.hei.stdgrade.PojaGenerated;
import school.hei.stdgrade.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
