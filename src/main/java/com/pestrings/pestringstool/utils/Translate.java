package com.pestrings.pestringstool.utils;

import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Objects;

public class Translate {

    static public String translateYandex(String text) throws IOException, ParseException {

        JSONObject jsonData = new JSONObject();
        jsonData.put("folderId", AppSettings.folderId);
        jsonData.put("texts", text);
        jsonData.put("targetLanguageCode", AppSettings.translateTarget);
        jsonData.put("sourceLanguageCode", AppSettings.translateSource);

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonData.toJSONString());
        Request request = new Request.Builder()
                .url("https://translate.api.cloud.yandex.net/translate/v2/translate")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + AppSettings.aimToken)
                .build();

        Response response = client.newCall(request).execute();

//        System.out.println("Code: " + response.code());
//        System.out.println(response.body().string());

        if(response.code() != 200) return null;

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(
                Objects.requireNonNull(
                        response.body()
                ).string()
        );
        JSONObject json = (JSONObject) obj;

        JSONArray resTranslations = (JSONArray) json.get("translations");
        JSONObject translatedText = (JSONObject) resTranslations.get(0);

        response.body().close();

        return (String) translatedText.get("text");
    }

    static public String translatePES(String text) throws IOException, ParseException {

        JSONObject jsonData = new JSONObject();
        jsonData.put("text", text);
        jsonData.put("target", AppSettings.translateTarget);
        jsonData.put("source", AppSettings.translateSource);

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonData.toJSONString());
        Request request = new Request.Builder()
                .url("http://192.168.1.53:8860/api/translate")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("access_token", AppSettings.aimToken)
                .build();

        Response response = client.newCall(request).execute();

        if(response.code() != 200) return null;

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(
                Objects.requireNonNull(
                        response.body()
                ).string()
        );
        JSONObject json = (JSONObject) obj;

        response.body().close();

        return (String) json.get("text");
    }

}
