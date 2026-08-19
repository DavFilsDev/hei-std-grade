package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record AcademicYear(String id, String label, int startYear) {}
