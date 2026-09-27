package menu;

import java.util.Scanner;

public class MainMenu {

    private Scanner scanner;

    public MainMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void display() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("                  FLEETFLOW");
        System.out.println("        INTELLIGENT LOGISTICS PLATFORM");
        System.out.println("====================================================");

        System.out.println("1. Shipment Management");
        System.out.println("2. Vehicle & Driver Management");
        System.out.println("3. Warehouse Management");
        System.out.println("4. Shipment Search (String Algorithms Module)");
        System.out.println("5. Document Management");
        System.out.println("6. Address Services");
        System.out.println("7. Delivery Planning");
        System.out.println("8. Network & Resource Management");
        System.out.println("9. View System Statistics");
        System.out.println("0. Exit");

        System.out.println("----------------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    public int getChoice() {

        while (!scanner.hasNextInt()) {

            System.out.println("Invalid input.");
            System.out.print("Please enter a number: ");

            scanner.next();
        }

        int choice = scanner.nextInt();

        if (choice == 4) {
            SearchMenu searchMenu = new SearchMenu(scanner);
            searchMenu.executeMenu();
        }

        return choice;
    }
}