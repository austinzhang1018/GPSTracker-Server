package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import tools.HttpStringifier;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Austin on 3/20/2018.
 */
public class MapHandler implements HttpHandler {
    private Configuration cfg;
    private String name;

    public MapHandler(Configuration cfg, String name) {
        this.cfg = cfg;
        this.name = name;
    }

    public void handle(HttpExchange t) throws IOException {
        Template template = cfg.getTemplate("mapgeocodetemplate.ftl");

        Map root = new HashMap();
        String[] locDataString = locationParser();
        String coordinateString = locDataString[0];
        root.put("coordinates", coordinateString);
        root.put("lastPosition", coordinateString.substring(coordinateString.lastIndexOf("{"), coordinateString.lastIndexOf(",")));
        root.put("name", getName());
        root.put("accuracy", locDataString[1]);
        root.put("timeLastPosition", locDataString[2]);

        StringWriter stringWriter = new StringWriter();
        try {
            template.process(root, stringWriter);
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        String response = stringWriter.toString();

        t.sendResponseHeaders(200, response.length());

        OutputStream os = t.getResponseBody();

        os.write(response.getBytes());

        os.close();
    }

    //Open file with locations and parse into http string for display
    private String[] locationParser() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(HttpStringifier.getCurrentDirectory() + "/locationhistories/" + getName() + "history.txt")));
        StringBuilder locString = new StringBuilder();
        String locLine;
        String lastLocAccuracy = "100";
        String lastLocTime = "";
        String batteryPercentage = "-1";
        while ((locLine = reader.readLine()) != null) {
            Scanner scanner = new Scanner(locLine);
            lastLocTime = scanner.next();
            locString.append(("{lat: " + scanner.next() + ", lng: " + scanner.next() + "},\n"));
            lastLocAccuracy = scanner.next();
            if (scanner.hasNext()) {
                batteryPercentage = scanner.next();
            }
        }

        lastLocTime = lastLocTime.substring(lastLocTime.indexOf("-") + 1, lastLocTime.indexOf("T")) + " " + lastLocTime.substring(lastLocTime.indexOf("T") + 1, lastLocTime.lastIndexOf(":"));
        return new String[]{locString.toString(), lastLocAccuracy, lastLocTime, batteryPercentage};
    }

    public int getBatteryPercentage() {
        try {
            return Integer.parseInt(locationParser()[3]);
        } catch (IOException e) {
            System.out.println("LOCATION PARSER ERROR");
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return name + "map.html";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return ((MapHandler) o).getName().equals(this.getName());
    }
}