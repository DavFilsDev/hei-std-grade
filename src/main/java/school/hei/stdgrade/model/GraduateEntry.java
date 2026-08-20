package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record GraduateEntry(
    int rank, String std, String lastName, String firstName, double average) {}
