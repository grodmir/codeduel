package com.codeduel;

public record SubmissionResultMessage(
        int schemaVersion,
        String submissionId,
        String matchId,
        String userId,
        Verdict verdict,
        Integer failedTest,
        String compileError,
        String submittedAt
) {
}
