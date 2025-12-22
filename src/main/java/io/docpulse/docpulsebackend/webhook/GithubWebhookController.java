package io.docpulse.docpulsebackend.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhooks/github")
public class GithubWebhookController {

    private static final Logger log = LoggerFactory.getLogger(GithubWebhookController.class);

    // TODO: move to config later
    private static final String EXPECTED_REPO = "docpulse-backend";
    private static final String MAIN_BRANCH_REF = "refs/heads/main";

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(@RequestBody Map<String, Object> payload) {

        log.info("📦 GitHub webhook received");

        // raw log (wanted for debugging purposes)
        log.info("Payload = {}", payload);

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

            log.info("✅ Valid webhook received for repo={} branch=main", repoName);

        } catch (Exception e) {
            log.error("❌ Invalid GitHub webhook payload", e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}