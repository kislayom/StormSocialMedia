/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.api.services.samples.youtube.cmdline.data;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.samples.youtube.cmdline.Auth;
import static com.google.api.services.samples.youtube.cmdline.Auth.HTTP_TRANSPORT;
import static com.google.api.services.samples.youtube.cmdline.Auth.JSON_FACTORY;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author impadmin
 */
public class GetComment {

    private static final String APPLICATION_NAME = "aeris-161512";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), "/home/impadmin/google/");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY
            = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES
            = Arrays.asList(YouTubeScopes.YOUTUBE_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String args[]) {

        try {
            YouTube youtube = getYouTubeService();
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet,replies");
            parameters.put("videoId", "m4Jtj2lCMAA");

            YouTube.CommentThreads.List commentThreadsListByVideoIdRequest = youtube.commentThreads().list(parameters.get("part").toString());
            if (parameters.containsKey("videoId") && parameters.get("videoId") != "") {
                commentThreadsListByVideoIdRequest.setVideoId(parameters.get("videoId").toString());
            }

            CommentThreadListResponse response = commentThreadsListByVideoIdRequest.execute();
            System.out.println(response);
        } catch (Exception exc) {
            System.out.println(exc);
        }

    }

    public static YouTube getYouTubeService() throws IOException {
        Credential credential = authorize();
        return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static Credential authorize() throws IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("/home/impadmin/google/Aeris-eb71f4fd0294.json"))
                .createScoped(Collections.singletonList(YouTubeScopes.YOUTUBE));
        return credential;//Auth.authorize(SCOPES, "fetchComment");
    }

}
