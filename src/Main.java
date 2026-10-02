import app.FleetFlowApplication;
import service.DPService;
import service.SearchService;

public class Main {

    public static void main(String[] args) {

        if (args != null && args.length > 0) {
            String arg = args[0].toLowerCase();
            if (arg.equals("--demo") || arg.equals("-d")) {
                System.out.println("Running all FleetFlow Algorithm Demos (String & Dynamic Programming)...");
                SearchService searchService = new SearchService();
                searchService.runAllAlgorithmDemos();

                DPService dpService = new DPService();
                dpService.runAllDPDemos();
                return;
            } else if (arg.equals("--dp-demo") || arg.equals("-dp")) {
                DPService dpService = new DPService();
                dpService.runAllDPDemos();
                return;
            }
        }

        FleetFlowApplication application = new FleetFlowApplication();
        application.start();
    }
}