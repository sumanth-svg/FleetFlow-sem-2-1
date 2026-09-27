package app;

import java.util.Scanner;
import menu.MainMenu;

public class FleetFlowApplication {

    private Scanner scanner;
    private MainMenu mainMenu;

    public FleetFlowApplication() {
        scanner = new Scanner(System.in);
        mainMenu = new MainMenu(scanner);
    }

    public void start() {

        boolean running = true;

        while (running) {

            mainMenu.display();

            int choice = mainMenu.getChoice();

            switch (choice) {

                case 1:
                    shipmentManagement();
                    break;

                case 2:
                    vehicleDriverManagement();
                    break;

                case 3:
                    warehouseManagement();
                    break;

                case 4:
                    shipmentSearch();
                    break;

                case 5:
                    documentManagement();
                    break;

                case 6:
                    addressServices();
                    break;

                case 7:
                    deliveryPlanning();
                    break;

                case 8:
                    networkManagement();
                    break;

                case 9:
                    systemStatistics();
                    break;

                case 0:
                    running = false;
                    System.out.println();
                    System.out.println("Thank you for using FleetFlow!");
                    System.out.println("Application closed.");
                    break;

                default:
                    System.out.println();
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private void shipmentManagement() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("              SHIPMENT MANAGEMENT");
        System.out.println("====================================================");

        System.out.println("Shipment management module will be implemented here.");

        pause();
    }

    private void vehicleDriverManagement() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("          VEHICLE & DRIVER MANAGEMENT");
        System.out.println("====================================================");

        System.out.println("Vehicle and driver management module will be implemented here.");

        pause();
    }

    private void warehouseManagement() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("             WAREHOUSE MANAGEMENT");
        System.out.println("====================================================");

        System.out.println("Warehouse management module will be implemented here.");

        pause();
    }

    private void shipmentSearch() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("                SHIPMENT SEARCH");
        System.out.println("====================================================");

        System.out.println("Search module will be implemented here.");

        pause();
    }

    private void documentManagement() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("              DOCUMENT MANAGEMENT");
        System.out.println("====================================================");

        System.out.println("Document management module will be implemented here.");

        pause();
    }

    private void addressServices() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("                ADDRESS SERVICES");
        System.out.println("====================================================");

        System.out.println("Address correction module will be implemented here.");

        pause();
    }

    private void deliveryPlanning() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("                DELIVERY PLANNING");
        System.out.println("====================================================");

        System.out.println("Delivery planning module will be implemented here.");

        pause();
    }

    private void networkManagement() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("          NETWORK & RESOURCE MANAGEMENT");
        System.out.println("====================================================");

        System.out.println("Network management module will be implemented here.");

        pause();
    }

    private void systemStatistics() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("               SYSTEM STATISTICS");
        System.out.println("====================================================");

        System.out.println("Vehicles       : 0");
        System.out.println("Drivers        : 0");
        System.out.println("Warehouses     : 0");
        System.out.println("Shipments      : 0");
        System.out.println("Delivery Tasks : 0");

        pause();
    }

    private void pause() {

        System.out.println();
        System.out.println("Press ENTER to return to the main menu...");

        scanner.nextLine();
        scanner.nextLine();
    }
}