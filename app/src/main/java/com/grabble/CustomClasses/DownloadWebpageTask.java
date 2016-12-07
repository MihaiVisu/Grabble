package com.grabble.CustomClasses;


import android.os.AsyncTask;
import android.util.Xml;

import com.grabble.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadWebpageTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        try {
            return downloadUrl(params[0]);
        } catch (IOException e) {
            return "Unable to retrieve webpage";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return "Error from xml parser";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
    }

    private String downloadUrl(String myUrl) throws IOException, XmlPullParserException {
        InputStream is;

        KMLParser kmlParser = new KMLParser();

        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        is = conn.getInputStream();
        List<KMLParser.Entry>entries = kmlParser.parse(is);

        String htmlString = "";
        for (KMLParser.Entry entry :
                entries) {
            System.out.println(entry.getCoordinates().toString());
        }
        return htmlString;
    }
}
