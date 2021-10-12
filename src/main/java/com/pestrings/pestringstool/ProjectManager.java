package com.pestrings.pestringstool;


import com.pestrings.pestringstool.pe.PEReplaceItem;
import com.pestrings.pestringstool.pe.PEStringItem;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectManager {

    public String exePath = "";
    public String projectPath = "";

    @SuppressWarnings("unchecked")
    public void saveFile(ObservableList<PEReplaceItem> items, String path) {

        JSONObject projectFile = new JSONObject();
        JSONArray stringsList = new JSONArray();
        projectFile.put("exePath", exePath);

        for(PEReplaceItem item : items) {

            JSONObject strItem = new JSONObject();
            strItem.put("original", item.stringItem.data);
            strItem.put("offset", item.stringItem.offset);
            strItem.put("new", item.newText);
            stringsList.add(strItem);

        }

        projectFile.put("strings", stringsList);

        projectPath = path;
        String jsonContent = projectFile.toJSONString();

        try(FileWriter file = new FileWriter(path)){
            file.write(jsonContent);
            file.flush();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public String loadFile(ObservableList<PEReplaceItem> items, String path) throws IOException, ParseException {

        JSONParser jsonParser = new JSONParser();
        FileReader file = new FileReader(path);

        Object obj = jsonParser.parse(file);
        JSONObject projectFile = (JSONObject) obj;

        projectPath = path;
        exePath = (String) projectFile.get("exePath");
        if(exePath == null) {
            throw new ParseException(0);
        }
        JSONArray strings = (JSONArray) projectFile.get("strings");
        strings.forEach( item -> {
            PEReplaceItem repItem = parseStringObject( (JSONObject) item );
            items.add(repItem);
        });

        return exePath;

    }

    private PEReplaceItem parseStringObject(JSONObject item)
    {
        String newStr = (String) item.get("new");
        String original = (String) item.get("original");
        long offset = (long) item.get("offset");
        return new PEReplaceItem(new PEStringItem((int) offset, original), newStr);
    }

    public void reset(String path) {
        exePath = path;
    }
}
