package bg.softuni.eventora.scheduling;

import bg.softuni.eventora.repository.PasswordResetTokenRepository;
import bg.softuni.eventora.repository.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class MaintenanceJobs {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceJobs.class);
    private final VerificationTokenRepository verificationTokens;
    private final PasswordResetTokenRepository resetTokens;
    private final CacheManager cacheManager;

    public MaintenanceJobs(VerificationTokenRepository verificationTokens,
                           PasswordResetTokenRepository resetTokens, CacheManager cacheManager) {
        this.verificationTokens = verificationTokens;
        this.resetTokens = resetTokens;
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void removeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        long removed = verificationTokens.deleteByExpiresAtBefore(now) + resetTokens.deleteByExpiresAtBefore(now);
        LOGGER.info("Removed {} expired authentication tokens", removed);
    }

    @Scheduled(fixedDelay = 300000)
    public void refreshEventCaches() {
        clear("events");
        clear("event");
        LOGGER.info("Refreshed event caches");
    }

    private void clear(String name) {
        if (cacheManager.getCache(name) != null) cacheManager.getCache(name).clear();
    }
}
