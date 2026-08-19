package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record AcademicTrack(String id, String code, String name) {}
