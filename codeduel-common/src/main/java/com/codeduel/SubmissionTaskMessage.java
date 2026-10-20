package com.codeduel;

public record SubmissionTaskMessage(
        int schemaVersion,
        String submissionId,
        String matchId,
        String userId,
        String problemId,
        String code,
        String submittedAt
) {}
