package com.gsu.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by cnytync on 07/11/2016.
 */
public class Util {

    public static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> files = new ArrayList<>();

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                files.addAll(listFilesForFolder(fileEntry));
            } else {
                if (fileEntry.getAbsolutePath().contains(".pdf"))
                    files.add(fileEntry.getAbsolutePath());
            }
        }

        return files;
    }

    public static <K, V extends Comparable<? super V>>
    List<Map.Entry<K, V>> sortMapByValues(Map<K, V> map) {

        List<Map.Entry<K, V>> sortedEntries = new ArrayList<Map.Entry<K, V>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        return sortedEntries;
    }

    public static String fetchRedirectURL(String url) throws IOException
    {
        HttpURLConnection con =(HttpURLConnection) new URL( url ).openConnection();
//System.out.println( "orignal url: " + con.getURL() );
        con.setInstanceFollowRedirects(false);
        con.connect();


        InputStream is = con.getInputStream();
        if(con.getResponseCode()==301)
            return con.getHeaderField("Location");
        else return null;
    }

    public static boolean isUTF8MB4(String s) {
        for (int i = 0; i < s.length(); ++i) {
            byte[] bytes = s.substring(i, i + 1).getBytes(StandardCharsets.UTF_8);
            if (bytes.length > 3) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        String url="http://commons.wikimedia.org/wiki/Special:FilePath/Kate_Bush_at_1986_Comic_Relief_(cropped).png";
        String fetchedUrl=fetchRedirectURL(url);
        System.out.println("FetchedURL is:"+fetchedUrl);
        while(fetchedUrl!=null)
        {   url=fetchedUrl;
            System.out.println("The url is:"+url);
            fetchedUrl=fetchRedirectURL(url);


        }
        System.out.println(url);

    }
}
