package utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class PixabayImageSearcher {
    private static final String ACCESS_KEY = "48078689-6625afe0836a368a0f22bda19";

    public static List<String> getImage(String query) {
        List<String> imageUrlList = new ArrayList<String>();

        try {
            String url = "https://pixabay.com/api/?key=" + ACCESS_KEY
                    + "&q=" + query + "&image_type=photo";
            Document document = Jsoup.connect(url).ignoreContentType(true).get();

            String jsonResponse = document.body().text();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray hits = jsonObject.getJSONArray("hits");

            for (int i = 0; i < hits.length(); i++) {
                JSONObject hitObject = hits.getJSONObject(i);
                String imageUrl = hitObject.getString("largeImageURL");
                imageUrlList.add(imageUrl);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        return imageUrlList;
    }
}
