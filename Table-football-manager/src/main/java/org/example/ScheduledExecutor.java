package org.example;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutor {

    private static final GameStatusManager gameStatusManager = new GameStatusManager();

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    static void updateGameStatuses() {
        Runnable updateStatuses = () -> {
            try {
                gameStatusManager.updateInFile();
                gameStatusManager.updateInDB();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        executorService.scheduleAtFixedRate(updateStatuses, 0, 2, TimeUnit.MINUTES);
    }
}
