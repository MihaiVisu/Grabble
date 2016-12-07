package com.grabble.CustomClasses;


import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWebpageTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        try {
            return downloadUrl(params[0]);
        } catch (IOException e) {
            return "Unable to retrieve webpage";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
    }

    public String readIt(InputStream stream, int len) throws IOException,
            UnsupportedEncodingException{
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private String downloadUrl(String myUrl) throws IOException {
        InputStream is = null;
        int len = 500;

        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            is = conn.getInputStream();
            String contentAsString = readIt(is, len);
            return contentAsString;

        } catch (IOException e) {
            return "Unable to get response.";
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
