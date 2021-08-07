package com.random.things.service;

import com.random.things.config.MovieConfig;
import com.random.things.utilities.QueryUtility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MovieService {

    RestTemplate restTemplate = new RestTemplate();
    QueryUtility queryUtility = new QueryUtility();
    Random random = new Random();
    MovieConfig movieConfig;

    public MovieService(MovieConfig movieConfig) {
        this.movieConfig = movieConfig;
    }

    private String getMovieFromTMDB(){
        HttpHeaders headers = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity<>(headers);

        Map<String, String> uriVariables = new HashMap<>();

        uriVariables.put("query", queryUtility.randomQueryGenerator(10));
        uriVariables.put("apiKey", movieConfig.getKey());

        ResponseEntity<String> response;


        response = restTemplate.exchange(movieConfig.getUrl(), HttpMethod.GET, requestEntity, String.class, uriVariables);

        while(new JSONObject(response.getBody()).getJSONArray("results").length()<1){
            uriVariables.put("query", queryUtility.randomQueryGenerator(10));
            response = restTemplate.exchange(movieConfig.getUrl(), HttpMethod.GET, requestEntity, String.class, uriVariables);
        }

        return response.getBody();
    }

    public Map<String, String> getForRandomMovie(){
        String movieResponse = getMovieFromTMDB();
        String imageUrl;
        JSONArray jsonResponse = new JSONObject(movieResponse).getJSONArray("results");

        JSONObject jsonMovie = jsonResponse.getJSONObject(random.nextInt(jsonResponse.length()));
        try {
            imageUrl = jsonMovie.getString("poster_path");
        }catch (Exception e){
            imageUrl = "";
        }
        String title = jsonMovie.getString("title");
        String overview = jsonMovie.getString("overview");

        HashMap<String, String> movie = new HashMap<>();

        movie.put("imageUrl", imageUrl);
        movie.put("movieTitle",title);
        movie.put("overview", overview);

        return movie;

    }



}
