package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Course(
    String id, String ref, String title, int credits, String teachingUnitId, String trackId) {}
