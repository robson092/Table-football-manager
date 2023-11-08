package org.example;

import java.io.IOException;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        System.out.println("Welcome to Table football manager!");
        new DBInitializer().initDB();
        DataLoader.loadFilesToDB();
        Menu menu = new Menu();
        while (true) {
            ScheduledExecutor.updateGameStatuses();
            menu.getMenu();
        }
    }
}
