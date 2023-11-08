package org.example;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutor {

    private static final GameStatusManager gameStatusManager = new GameStatusManager();

    static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private ScheduledExecutor() {
    }

    static void updateGameStatuses() {
        executorService.scheduleAtFixedRate(() -> {
            try {
                gameStatusManager.updateInDB();
                gameStatusManager.updateInFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }
}
