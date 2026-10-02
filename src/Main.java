import app.FleetFlowApplication;
import service.DPService;
import service.DocumentService;
import service.SearchService;

public class Main {

    public static void main(String[] args) {

        if (args != null && args.length > 0) {
            String arg = args[0].toLowerCase();

            if (arg.equals("--demo") || arg.equals("-d") || arg.equals("--all")) {
                System.out.println("Running all FleetFlow Algorithm Demos (Modules M1, M2, M3)...");
                new SearchService().runAllAlgorithmDemos();
                new DocumentService().runAllDocumentDemos();
                new DPService().runAllDPDemos();
                return;
            } else if (arg.equals("--m1") || arg.equals("-m1")) {
                new SearchService().runAllAlgorithmDemos();
                return;
            } else if (arg.equals("--m2") || arg.equals("-m2") || arg.equals("--doc-demo")) {
                new DocumentService().runAllDocumentDemos();
                return;
            } else if (arg.equals("--m3") || arg.equals("-m3") || arg.equals("--dp-demo") || arg.equals("-dp")) {
                new DPService().runAllDPDemos();
                return;
            }
        }

        FleetFlowApplication application = new FleetFlowApplication();
        application.start();
    }
}