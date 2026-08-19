package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Semester(String id, String code, int position, int totalCredits) {}
