/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youtube.tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 *
 * @author impadmin
 */
public class InstaCall {

    public static double getCount(String args) throws MalformedURLException, IOException {
        URL url = new URL("https://api.instagram.com/v1/tags/trump?access_token=1427695591.655ef10.a1c47f128e5e43d79631b6b370c9f693");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        //System.out.println("Output from Server .... \n");
        String json = "";
        Gson gson = new Gson();

        while ((output = br.readLine()) != null) {
           // System.out.println(output);
            json += output;
        }
        Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());

        map = gson.fromJson(map.get("data").toString(), new TypeToken<Map<String, Object>>() {
        }.getType());
        //System.out.println(map.get("media_count"));
        conn.disconnect();
        return (Double) map.get("media_count");
    }
    public static void main(String args[]) throws IOException{
        System.out.println(InstaCall.getCount("trump"));
    }

}
