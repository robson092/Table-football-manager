package org.example;

import java.io.IOException;
import java.sql.SQLException;

public class MenuService {

    static Menu menu = new Menu();

    private MenuService(){}

    static boolean hasBackToMenuChosen(String input) throws SQLException, IOException, InterruptedException {
        if (input.equalsIgnoreCase("back")) {
            menu.getMenu();
            return true;
        }
        return false;
    }
}
