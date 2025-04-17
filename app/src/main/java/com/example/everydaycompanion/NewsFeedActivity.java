package com.example.everydaycompanion;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewsFeedActivity extends AppCompatActivity {

    private LinearLayout newsContainer;
    private final String NEWS_API_URL = "https://newsapi.org/v2/top-headlines?country=in&category=general&apiKey=77d99ae10c2f4c588d51e608fd25c7dc"; // Replace with your key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        newsContainer = findViewById(R.id.newsContainer);

        fetchNews();
    }

    private void fetchNews() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                NEWS_API_URL,
                null,
                response -> {
                    try {
                        JSONArray articles = response.getJSONArray("articles");

                        for (int i = 0; i < articles.length(); i++) {
                            JSONObject article = articles.getJSONObject(i);
                            String title = article.getString("title");

                            TextView newsItem = new TextView(this);
                            newsItem.setText("• " + title);
                            newsItem.setTextSize(18);
                            newsItem.setTextColor(Color.parseColor("#0066CC"));
                            newsItem.setPadding(0, 20, 0, 20);

                            newsContainer.addView(newsItem);
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing news", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to fetch news", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        queue.add(jsonRequest);
    }
}
