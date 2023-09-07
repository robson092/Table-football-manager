package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

public class Application {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        System.out.println("Welcome to Table football manager!");
        new DBInitializer().initDB();
        DataLoader.loadFilesToDB();
        Menu menu = new Menu();
        while (true) {
           menu.getMenu();
        }
    }
}
