package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Role(String id, RoleName name) {}
