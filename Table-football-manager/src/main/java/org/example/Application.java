package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

public class Application {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException, ExecutionException {
        System.out.println("Welcome to Table football manager!");
        new DBInitializer().initDB();
        DataLoader.loadFilesToDB();
        Menu menu = new Menu();
        ScheduledFuture<?> scheduledFuture = ScheduledExecutor.updateGameStatuses();
        System.out.println("Current Thread Group - " + Thread.currentThread().getThreadGroup().getName());
        while (true) {
           menu.getMenu();
        }
    }
}
