package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record TeachingUnit(String id, String code, String title, String semesterId, int credits) {}
