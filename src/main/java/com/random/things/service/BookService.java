package com.random.things.service;

import com.random.things.config.BookConfig;
import com.random.things.utilities.QueryUtility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class BookService {

    QueryUtility queryUtility = new QueryUtility();
    RestTemplate restTemplate = new RestTemplate();
    Random random = new Random();
    Logger log = LoggerFactory.getLogger(BookService.class);

    @Autowired
    BookConfig bookConfig;

    @Autowired
    public BookService(BookConfig bookConfig) {
        this.bookConfig = bookConfig;
    }

    private String getBookFromGBooks(){

        HttpHeaders headers = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity<>(headers);
        Map<String, String> uriVariables = new HashMap<>();

        uriVariables.put("query", queryUtility.randomQueryGenerator(10));
        uriVariables.put("apiKey", bookConfig.getKey());

        ResponseEntity<String> response;

        response = restTemplate.exchange(bookConfig.getUrl(), HttpMethod.GET, requestEntity, String.class, uriVariables);

        while((new JSONObject(response.getBody()).getInt("totalItems")) <1){
            uriVariables.put("query", queryUtility.randomQueryGenerator(10));
            response = restTemplate.exchange(bookConfig.getUrl(), HttpMethod.GET, requestEntity, String.class, uriVariables);
        }
        return response.getBody();
    }

    public Map<String, String> getForRandomBook(){
        String bookResponse = getBookFromGBooks();

        JSONArray jsonResponse = new JSONObject(bookResponse).getJSONArray("items");

        JSONObject jsonBook = jsonResponse.getJSONObject(random.nextInt(jsonResponse.length())).getJSONObject("volumeInfo");

        HashMap<String, String> book = new HashMap<>();

        try{book.put("bookTitle",jsonBook.getString("title"));}catch(Exception e){}
        try{book.put("authors", jsonBook.getJSONArray("authors").join(", "));}catch(Exception e){}
        try{book.put("publisher",jsonBook.getString("publisher"));}catch(Exception e){}
        try{book.put("description", jsonBook.getString("description"));}catch(Exception e){}
        try{book.put("gBooksUrl", jsonBook.getString("infoLink"));}catch(Exception e){}
        try{book.put("bookImg", jsonBook.getJSONObject("imageLinks").getString("thumbnail"));}catch(Exception e){}

        return book;
    }
}
