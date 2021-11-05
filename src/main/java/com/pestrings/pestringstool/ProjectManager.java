package com.pestrings.pestringstool;


import com.pestrings.pestringstool.exceptions.ExeNotFound;
import com.pestrings.pestringstool.pe.PEReplaceItem;
import com.pestrings.pestringstool.pe.PEStringItem;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
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

        try(Writer file = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)){
            file.write(jsonContent);
            file.flush();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public ArrayList<PEReplaceItem> loadFile(String path, Stage stage) throws IOException, ParseException, ExeNotFound {

        JSONParser jsonParser = new JSONParser();
        String file = Files.readString(Path.of(path), StandardCharsets.UTF_8);

        Object obj = jsonParser.parse(file);
        JSONObject projectFile = (JSONObject) obj;

        projectPath = path;
        exePath = (String) projectFile.get("exePath");
        if(exePath == null) {
            throw new ParseException(0);
        }

        //check exe file exists
        File tempFile = new File(exePath);
        if(!tempFile.exists()) {
            // require new exe file for use

            Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "EXE file not found! Find this file on disk?").showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) {
                throw new ExeNotFound("exe_not_found");
            }

            FileChooser fc = new FileChooser();
            fc.setTitle("Open new EXE file");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE files (*.exe)", "*.exe"));
            File dialogFile = fc.showOpenDialog(stage);
            if(dialogFile != null) {
                exePath = dialogFile.getPath();
            } else {
                throw new ExeNotFound("exe_not_found");
            }

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
