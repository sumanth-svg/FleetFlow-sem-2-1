import app.FleetFlowApplication;
import service.SearchService;

public class Main {

    public static void main(String[] args) {

        if (args != null && args.length > 0 && (args[0].equalsIgnoreCase("--demo") || args[0].equalsIgnoreCase("-d"))) {
            SearchService searchService = new SearchService();
            searchService.runAllAlgorithmDemos();
            return;
        }

        FleetFlowApplication application = new FleetFlowApplication();
        application.start();
    }
}