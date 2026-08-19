package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record ClassGroup(String id, String ref) {}
