package tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Austin on 3/20/2018.
 */
public class HttpStringifier {
    public static String getCurrentDirectory() {
        return new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().lastIndexOf("."));
    }

    public static String stringify(String path) throws IOException
    {
        Charset encoding = StandardCharsets.UTF_8;
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
