package haneum.troller.service.fullSearch.GameRecord;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

@Service
public class GetJsonFromUrl {

    public String Read(Reader re) throws io.jsonwebtoken.io.IOException, java.io.IOException {     // class Declaration
        StringBuilder str = new StringBuilder();     // To Store Url Data In String.
        int temp;
        do {

            temp = re.read();
            str.append((char) temp);

        } while (temp != -1);
        return str.toString();
    }

    public JSONObject readJsonFromUrlMethod(String link) throws io.jsonwebtoken.io.IOException, java.io.IOException, org.json.simple.parser.ParseException {
        InputStream input = new URL(link).openStream();
        BufferedReader re = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
        // Buffer Reading In UTF-8
        String text = Read(re);
        text = text.substring(0, text.length() - 1);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new StringReader(text));
        JSONObject jsonObj = (JSONObject) obj;
        input.close();
        return jsonObj;    // Returning JSON
    }

    public JSONArray readJsonArrayFromUrlMethod(String link)throws io.jsonwebtoken.io.IOException, java.io.IOException, org.json.simple.parser.ParseException {
        InputStream input = new URL(link).openStream();
        BufferedReader re = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
        // Buffer Reading In UTF-8
        String text = Read(re);
        text = text.substring(0, text.length() - 1);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new StringReader(text));
        JSONArray jsonArray = (JSONArray) obj;
        input.close();
        return jsonArray;    // Returning JSON
    }

    public JSONObject readJsonObjFromUrl(String url) throws IOException, org.json.simple.parser.ParseException {
        return readJsonFromUrlMethod(url);  // calling method in order to read.
    }

    public JSONArray readJsonArrayFromUrl(String url) throws IOException, org.json.simple.parser.ParseException {
        return readJsonArrayFromUrlMethod(url);
    }
}
