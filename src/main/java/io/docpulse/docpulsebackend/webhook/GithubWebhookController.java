package io.docpulse.docpulsebackend.webhook;

import io.docpulse.docpulsebackend.change.ChangeAnalyzerService;
import io.docpulse.docpulsebackend.change.ChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/webhooks/github")
@Slf4j
public class GithubWebhookController {

    // TODO: move to config later
    private static final String EXPECTED_REPO = "docpulse-backend";
    private static final String MAIN_BRANCH_REF = "refs/heads/main";

    private final ChangeAnalyzerService analyzerService;

    public GithubWebhookController(ChangeAnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(@RequestBody Map<String, Object> payload) {

        log.info("📦 GitHub webhook received");

        try {
            Map<String, Object> repository =
                    (Map<String, Object>) payload.get("repository");

            String repoName = (String) repository.get("name");
            String ref = (String) payload.get("ref");

            if (!EXPECTED_REPO.equals(repoName)) {
                log.warn("❌ Ignored webhook: unknown repo {}", repoName);
                return ResponseEntity.badRequest().build();
            }

            if (!MAIN_BRANCH_REF.equals(ref)) {
                log.info("ℹ️ Ignored webhook: not main branch ({})", ref);
                return ResponseEntity.ok().build();
            }

            List<Map<String, Object>> commits =
                    (List<Map<String, Object>>) payload.get("commits");

            if (commits == null || commits.isEmpty()) {
                log.warn("No commits found in payload");
                return ResponseEntity.ok().build();
            }

            Map<String, Object> lastCommit = commits.getLast();
            String commitSha = (String) lastCommit.get("id");

            List<String> filesChanged = new ArrayList<>();
            filesChanged.addAll((List<String>) lastCommit.getOrDefault("added", List.of()));
            filesChanged.addAll((List<String>) lastCommit.getOrDefault("modified", List.of()));
            filesChanged.addAll((List<String>) lastCommit.getOrDefault("removed", List.of()));

            ChangeEvent event = new ChangeEvent(commitSha, filesChanged);

            log.info("🧠 ChangeEvent created: {}", event);

            analyzerService.hasPotentialDocumentationImpact(event);

        } catch (Exception e) {
            log.error("❌ Invalid GitHub webhook payload", e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}