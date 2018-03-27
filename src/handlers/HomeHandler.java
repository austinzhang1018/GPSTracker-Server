package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import server.Server;
import tools.HttpStringifier;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Austin on 3/20/2018.
 */
public class HomeHandler implements HttpHandler {
    Server server;
    Configuration cfg;

    public HomeHandler(Server server, Configuration cfg) {
        this.server = server;
        this.cfg = cfg;
    }

    public void handle(HttpExchange t) throws IOException {

        Template template = cfg.getTemplate("hometemplate.ftl");

        Map root = new HashMap();
        root.put("parsedmaplinks", parseMapLinks(server));

        StringWriter stringWriter = new StringWriter();
        try {
            template.process(root, stringWriter);
        } catch (TemplateException e) {
            e.printStackTrace();
            System.out.println("TEMPLATE ERROR HOME HANDLER");
        }

        String response = stringWriter.toString();

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }



    private static String parseMapLinks(Server server) {
        StringBuilder mapStringBuilder = new StringBuilder();

        HashSet<MapHandler> mapHandlers = server.getMapHandlers();
        for (MapHandler handler : mapHandlers) {
            mapStringBuilder.append("<a href=\"http://24.208.163.239:37896/");
            mapStringBuilder.append(handler.getLink());
            mapStringBuilder.append("\">");
            mapStringBuilder.append(handler.getName());
            mapStringBuilder.append("<a>");
            int batteryPercentage = handler.getBatteryPercentage();
            if (batteryPercentage != -1 && batteryPercentage != 0) {
                mapStringBuilder.append(" Battery Remaining: ");
                mapStringBuilder.append(batteryPercentage);
                mapStringBuilder.append("%");
            }
            else {
                mapStringBuilder.append(" No battery information is available.");
            }
            mapStringBuilder.append("\n<br>\n");
        }

        if (mapStringBuilder.toString().length() == 0) {
            return "No trackers are currently running.";
        }

        return mapStringBuilder.toString();
    }

}
