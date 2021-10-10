package com.sadpooh.gauchobadge.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public abstract class NetworkHandler {

    public static final String DEFAULT_HEADER = "Connection: keep-alive\n" +
            "Cache-Control: max-age=0\n" +
            "Content-Type: application/x-www-form-urlencoded\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
//            "Accept-Encoding: gzip, deflate, br\n";
            "Accept-Language: en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7";

    public NetworkHandler() {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    private void initializeConnection() {

    }


    public abstract void extractRequiredFormDataToJson(JSONObject jsonObject, String content) throws JSONException;

    public abstract void extractRequiredFormDataToMap(HashMap<String, String> map, String content);

    public abstract void run();


    public final String readFromInputStream(InputStream inputStream) {
        try {

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException ioException) {
            Log.e("Gaucho", "IO Error");
            return "";
        }
    }

    public String sendGetRequest(HttpURLConnection httpURLConnection, String header) {
        try {
            httpURLConnection.setRequestMethod("GET");
            setHttpHeader(httpURLConnection, header);
            httpURLConnection.connect();
            return readFromInputStream(httpURLConnection.getInputStream());
        } catch (IOException ioException) {
            return "";
        }
    }

    public void sendPostWithJson(HttpURLConnection httpURLConnection, JSONObject jsonObject) {
        try {
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
            outputStreamWriter.write(jsonObject.toString());
        } catch (Exception exception) {
            Log.e("Gaucho:", exception.getLocalizedMessage());
        }
    }


    public String sendPostWithPlainText(HttpURLConnection httpURLConnection,
                                        HashMap<String, String> postFormParams) {

        StringBuilder response = new StringBuilder();
        try {
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);


            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            bufferedWriter.write(getPostDataString(postFormParams));

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            } else {
                response = new StringBuilder();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public HttpURLConnection getHttpURLConnection(String text) {

        try {
            return (HttpURLConnection) (new URL(text)).openConnection();
        } catch (Exception e) {
            Log.e("Gaucho", e.getLocalizedMessage());
            return null;
        }
    }

    public final void setHttpHeader(HttpURLConnection httpURLConnection, String header) {
        if (!header.isEmpty()) {
            BufferedReader bufferedReader = new BufferedReader(new StringReader(header));
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    String[] property = line.split(":");
                    if (property.length != 2) {
                        continue;
                    }
                    httpURLConnection.setRequestProperty(property[0].trim(), property[1].trim());
                }
            } catch (IOException ioException) {
                Log.e("Gaucho", ioException.getLocalizedMessage());
            }
        }
    }

}
