package com.codeduel;

public record SubmissionResultMessage(
        int schemaVersion,
        String submissionId,
        String matchId,
        String userId,
        Verdict verdict,
        int passed,
        int total,
        Integer failedTest,
        long timeMs,
        String compileError,
        String submittedAt
) {
}
