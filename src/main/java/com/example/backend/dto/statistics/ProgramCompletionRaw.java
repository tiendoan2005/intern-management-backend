package com.example.backend.dto.statistics;

public interface ProgramCompletionRaw {
    Long getProgramId();
    String getProgramName();
    Long getTotalInterns();
    Long getCompletedInterns();
}
