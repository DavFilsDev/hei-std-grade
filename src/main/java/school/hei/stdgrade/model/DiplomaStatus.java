package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record DiplomaStatus(boolean diplomed) {}
