package io.docpulse.docpulsebackend.change;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

public class ChangeEvent {

    @Getter private final String commitSha;
    @Getter private final List<String> filesChanged;
    private final Instant createdAt;

    public ChangeEvent(String commitSha, List<String> filesChanged) {
        this.commitSha = commitSha;
        this.filesChanged = filesChanged;
        this.createdAt = Instant.now();
    }

    @Override
    public String toString() {
        return "ChangeEvent{" +
                "commitSha='" + commitSha + '\'' +
                ", filesChanged=" + filesChanged +
                ", createdAt=" + createdAt +
                '}';
    }
}