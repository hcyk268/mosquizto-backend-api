package com.mosquizto.api.configuration;

import com.mosquizto.api.service.SeederService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
* How to use seeder:
* Run ./mvnw spring-boot:run "-Dspring-boot.run.profiles=seed" "-Dspring-boot.run.arguments=--seed.userId={} --seed.command={}"
* */

@Component
@Profile("seed")  // Chỉ chạy khi active profile là "seed"
@RequiredArgsConstructor
@Slf4j
public class SeedCommandRunner implements ApplicationRunner {
    private final SeederService seederService;

    @Value("${seed.userId:1}")
    private int userId;

    @Value("${seed.command:all}")
    private String command;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("🌱 Seed command='{}' cho userId={}", command, userId);

        switch (command) {
           //  case "create_collection_item" -> seederService.seedCollectionItems(userId);
           // case "create_study_session"   -> seederService.seedStudySessions(userId);
            case "create_collection_item" -> seederService.initAndSeedDataForUser(userId);
            default -> log.warn("⚠️ Unknown command: {}", command);
        }

        log.info("✅ Done.");
        System.exit(0);
    }
}
