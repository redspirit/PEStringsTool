package com.pestrings.pestringstool;


import com.pestrings.pestringstool.pe.PEReplaceItem;
import com.pestrings.pestringstool.pe.PEStringItem;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    public ArrayList<PEReplaceItem> loadFile(String path) throws IOException, ParseException {

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
        return (ArrayList<PEReplaceItem>) strings.stream().map(item -> {
            return parseStringObject( (JSONObject) item );
        }).collect(Collectors.toList());

    }

    private PEReplaceItem parseStringObject(JSONObject item)
    {
        String newStr = (String) item.get("new");
        String original = (String) item.get("original");
        long offset = (long) item.get("offset");
        PEStringItem str = new PEStringItem((int) offset, original);
        str.setTranslated(true);
        return new PEReplaceItem(str, newStr);
    }

    public void reset(String path) {
        exePath = path;
    }
}
