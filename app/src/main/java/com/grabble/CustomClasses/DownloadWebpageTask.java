package com.grabble.CustomClasses;


import android.os.AsyncTask;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;
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
            return loadXmlFromNetwork(params[0]);
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

    private String loadXmlFromNetwork(String urlString) throws
            XmlPullParserException, IOException {
        InputStream stream = null;
        KMLParser kmlParser = new KMLParser();

        List<KMLParser.Entry> entries = null;

        StringBuilder htmlString = new StringBuilder();

        try {
            stream = downloadUrl(urlString);
            entries = kmlParser.parse(stream);
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }
        for (KMLParser.Entry entry:
             entries) {
            htmlString.append("<name>");
            htmlString.append(entry.getName());
            htmlString.append("</name>");
            htmlString.append("<coordinates>");
            htmlString.append(entry.getCoordinates().toString());
            htmlString.append("</coordinates>");
        }

        return htmlString.toString();
    }

    private InputStream downloadUrl(String myUrl) throws IOException, XmlPullParserException {

        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        return conn.getInputStream();
    }
}
