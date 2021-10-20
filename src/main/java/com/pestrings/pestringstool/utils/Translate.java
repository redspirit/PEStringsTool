package com.pestrings.pestringstool.utils;


import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Translate {

    static public String apiKey = "";

    public static void setApiKey(String apiKey) {
        Translate.apiKey = apiKey;
    }

    static public void test() throws IOException {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "q=Hello%2C%20world!&target=es&source=en");
        Request request = new Request.Builder()
                .url("https://google-translate1.p.rapidapi.com/language/translate/v2")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "*/*")
                .addHeader("x-rapidapi-host", "google-translate1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "c4eea3bfadmsh934820856b3aa2dp176aa7jsn57064c8b13a1")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Code: " + response.code());
        System.out.println(response.body().string());

    }

    static public String translate(String text, String source, String target) throws IOException, ParseException {

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("q", text)
                .add("target", target)
                .add("source", source)
                .build();

        Request request = new Request.Builder()
                .url("https://google-translate1.p.rapidapi.com/language/translate/v2")
                .post(formBody)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "*/*")
                .addHeader("x-rapidapi-host", "google-translate1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", apiKey)
                .build();

        Response response = client.newCall(request).execute();

        System.out.println("Code: " + response.code());
        System.out.println(response.body().string());

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(response.body().string());
        JSONObject json = (JSONObject) obj;

        JSONObject resData = (JSONObject) json.get("data");
//        JSONObject resTranslations = (JSONObject) json.get("translations");
        JSONArray resTranslations = (JSONArray) json.get("translations");
        JSONObject translatedText = (JSONObject) resTranslations.get(0);
        String resText = (String) json.get("translatedText");

        System.out.println(">> " + resText);

        return "";
    }

}
