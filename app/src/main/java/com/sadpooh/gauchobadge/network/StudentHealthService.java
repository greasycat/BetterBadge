package com.sadpooh.gauchobadge.network;

import android.util.Pair;

import com.sadpooh.gauchobadge.network.HtmlParser.Tag;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

public class StudentHealthService extends NetworkHandler {

    private final String requestVerificationToken = "__RequestVerificationToken";
    private final String weirdToken = "0nfi2kv9sjs0";
    private final String dualAuthSelectionURL = "https://studenthealthoc.sa.ucsb.edu/login_dualauthentication.aspx";
    private final String loginAuthURL = "https://studenthealthoc.sa.ucsb.edu/login_directory.aspx";
    private final String homePageURL = "https://studenthealthoc.sa.ucsb.edu/home.aspx";
    private final String surveyStartPageURL = "https://studenthealthoc.sa.ucsb.edu/CheckIn/Survey/Intro/24";
    private final String surveyPageURL = "https://studenthealthoc.sa.ucsb.edu/CheckIn/Survey/ShowAll/24";
    private final String surveyConfirmURL = "https://studenthealthoc.sa.ucsb.edu/SurveyFormsHome.aspx?success=1";
    private final String badgeURL = "https://studenthealthoc.sa.ucsb.edu/SurveyFormsHome.aspx?success=1;";
    public String lastResponse;


    public StudentHealthService() {
        super();
    }

    @Override
    public void extractRequiredFormDataToJson(JSONObject jsonObject, String content) throws JSONException {
        HashMap<String, String> map = new HashMap<>();
        extractRequiredFormDataToMap(map, content);
        jsonObject.put(requestVerificationToken, map.get(requestVerificationToken));
        jsonObject.put(weirdToken, map.get(weirdToken));
    }

    @Override
    public void extractRequiredFormDataToMap(HashMap<String, String> map, String content) {
        List<Tag> results = HtmlParser.findAllTagWithAttributeName(content, "input", new Pair<>("name", "__RequestVerificationToken"), new Pair<>("name", "0nfi2kv9sjs0"));
        for (Tag tag : results) {
            if (tag.getAttribute("name").equals("__RequestVerificationToken")) {
                map.put("__RequestVerificationToken", tag.getAttribute("value"));
            }
            if (tag.getAttribute("name").equals("0nfi2kv9sjs0")) {
                map.put("0nfi2kv9sjs0", tag.getAttribute("value"));
            }
        }
    }

    @Override
    public void run() {
    }

    public void writeFormToConnection(HttpURLConnection httpURLConnection, JSONObject jsonObject) {

    }

    public void bypassDualAuthPage() {
        HttpURLConnection httpURLConnection;
        HashMap<String, String> formParams = new HashMap<>();
        if ((httpURLConnection = super.getHttpURLConnection(dualAuthSelectionURL)) != null) {
            this.lastResponse = super.sendGetRequest(httpURLConnection, NetworkHandler.DEFAULT_HEADER);
            formParams.put("__VIEWSTATE", "");
            formParams.put("cmdStudentDualAuthentication", "UCSB Students, Faculty and Staff");
            extractRequiredFormDataToMap(formParams, this.lastResponse);
        }

        //Start to post
        if ((httpURLConnection = super.getHttpURLConnection(dualAuthSelectionURL)) != null) {
            this.lastResponse = sendPostWithPlainText(httpURLConnection, formParams);
        }
    }

    public void bypassLogin() {

    }
}
