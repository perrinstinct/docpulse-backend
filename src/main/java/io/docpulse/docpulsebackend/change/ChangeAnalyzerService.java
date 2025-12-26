package io.docpulse.docpulsebackend.change;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChangeAnalyzerService {

    public boolean hasPotentialDocumentationImpact(ChangeEvent event) {

        boolean impact = event.getFilesChanged().stream()
                .anyMatch(file -> file.startsWith("src/"));

        log.info("📄 Documentation impact detected = {}", impact);
        return impact;
    }
}