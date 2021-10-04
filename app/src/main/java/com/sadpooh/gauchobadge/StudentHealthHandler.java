package com.sadpooh.gauchobadge;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class StudentHealthHandler extends AsyncTask<StudentAccount, Void, Boolean> {

    private final String dualAuthSelectionURL = "https://studenthealthoc.sa.ucsb.edu/login_dualauthentication.aspx";
    private final String loginAuthURL = "https://studenthealthoc.sa.ucsb.edu/login_directory.aspx";
    private final String homePageURL = "https://studenthealthoc.sa.ucsb.edu/home.aspx";
    private final String surveyStartPageURL = "https://studenthealthoc.sa.ucsb.edu/CheckIn/Survey/Intro/24";
    private final String surveyPageURL = "https://studenthealthoc.sa.ucsb.edu/CheckIn/Survey/ShowAll/24";
    private final String surveyConfirmURL = "https://studenthealthoc.sa.ucsb.edu/SurveyFormsHome.aspx?success=1";
    private final String badgeURL = "https://studenthealthoc.sa.ucsb.edu/SurveyFormsHome.aspx?success=1;";

    StudentAccount studentAccount;
    OnBadgeCompleteListener onBadgeCompleteListener;
    URLConnection URLConnection;
    String output = "";

    public StudentHealthHandler(OnBadgeCompleteListener onBadgeCompleteListener) {
        this.onBadgeCompleteListener = onBadgeCompleteListener;
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    private void initializeConnection()
    {

    }

    private void extractASPFormParams(String content)
    {
        ResponseParser parser = new ResponseParser(content);
        parser.findAllAbsoluteTagWithAttribute("input", "name", "0nfi2kv9sjs0");
    }

    public static String readFromInputStream(InputStream inputStream)
    {
        try {

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException ioException)
        {
            Log.e("Gaucho", "IO Error");
            return "";
        }
    }

    private String sendGetRequest(URL url)
    {
        try{
            return readFromInputStream(url.openStream());
        }catch (IOException ioException)
        {
            Log.e("Gaucho", ioException.toString());
            return "";
        }
    }

    private void passDualAuthSelection()
    {
    }


    @Override
    protected final Boolean doInBackground(StudentAccount... info) {
        if (info[0] != null) {
            //Name and password exist
            try {
                output = sendGetRequest(new URL(dualAuthSelectionURL));

            }
            catch (MalformedURLException malformedURLException)
            {
                output = malformedURLException.getLocalizedMessage();
            }
        }
        return false;
    }

    protected void onPostExecute(Boolean o) {
            onBadgeCompleteListener.onTestShow(output);

    }

}
