package school.hei.stdgrade.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Grade(String id, String studentId, String examId, double score) {}
