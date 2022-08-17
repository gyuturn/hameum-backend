package haneum.troller.service.fullSearch.metaDataParsing;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public interface Parse {
    public default String getPath(String path){
        return "../../../../../../resources/RuneMetaData/" + path + ".json";
    }

    public default JSONObject parsingJsonFIle(String realPath) throws IOException, ParseException {
        FileReader reader = new FileReader(realPath);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        return jsonObject;
    }

    public default JSONArray parsingJsonFIleArray(String realPath) throws IOException, ParseException {
        System.out.println("debug");
        FileReader reader = new FileReader(realPath);
        JSONParser parser = new JSONParser();
        JSONArray jsonA = (JSONArray) parser.parse(reader);
        System.out.println(jsonA.getClass().getName());
        return jsonA;
    }
}
