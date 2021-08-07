package com.random.things.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.random.things.config.MusicConfig;
import com.random.things.dto.SpotifyAccessTokenDto;
import com.random.things.utilities.QueryUtility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class MusicService {

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    ObjectMapper objectMapper = new ObjectMapper();
    QueryUtility queryUtility = new QueryUtility();
    SpotifyAccessTokenDto accessTokenDto;
    MusicConfig musicConfig;
    private long tokenUpdateTime;
    Logger log = LoggerFactory.getLogger(MusicService.class);

    public MusicService(MusicConfig musicConfig) {
        this.musicConfig = musicConfig;
    }

    private void refreshToken(){

        //set headers
        headers.clear();
        headers.add("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString("d7ceec1a959e49afbee8f7c8a50e9048:c013f5ac73404a58948d062b6a57dbc9".getBytes(StandardCharsets.UTF_8)));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //set body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        //create and send request
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(musicConfig.getTokenUrl(), request, String.class);

        //map response to dto
        try {
            accessTokenDto  = objectMapper.readValue(response.getBody(), SpotifyAccessTokenDto.class);
            tokenUpdateTime = System.currentTimeMillis();
        } catch (JsonProcessingException e) {
            log.error("Music Service Exception: ", e);
        }
    }

    private String getSongFromSpotify(){
        if(isTokenExpired()){
            refreshToken();
        }
        // get token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessTokenDto.getAccess_token());

        Map<String, String> uriVariables = new HashMap<>();

        uriVariables.put("query", queryUtility.randomQueryGenerator(10));
        uriVariables.put("offset", "0");
        uriVariables.put("limit", "1");
        uriVariables.put("type", "track");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response;

        response = restTemplate.exchange(musicConfig.getUrl(), HttpMethod.GET, request, String.class, uriVariables);

        while(response.getStatusCode() == HttpStatus.NOT_FOUND){
            uriVariables.put("query", queryUtility.randomQueryGenerator(10));
            response = restTemplate.exchange(musicConfig.getUrl(), HttpMethod.GET, request, String.class, uriVariables);
        }

        return response.getBody();
    }

    public Map<String, String> getForRandomMusic(){
        String musicResponse = getSongFromSpotify();

        while(!(new JSONObject(musicResponse).has("tracks")) || new JSONObject(musicResponse).getJSONObject("tracks").getJSONArray("items").length()<1 ){
            musicResponse = getSongFromSpotify();
        }

        String previewUrl;
        JSONObject jsonResponse = new JSONObject(musicResponse).getJSONObject("tracks").getJSONArray("items").getJSONObject(0);

        String imageUrl = jsonResponse.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
        String title = jsonResponse.getString("name");
        try{
            previewUrl = jsonResponse.getString("preview_url");
        }catch(Exception e){
            previewUrl = "";
        }
        String spotifyUrl = jsonResponse.getJSONObject("external_urls").getString("spotify");
        String album = jsonResponse.getJSONObject("album").getString("name");
        JSONArray artistList = jsonResponse.getJSONObject("album").getJSONArray("artists");
        String artists = "";
        if (artistList != null) {
            for (int i=0;i<artistList.length();i++){
                artists = artists.concat(artistList.getJSONObject(i).getString("name"));
            }
        }
        HashMap<String, String> song = new HashMap<>();

        song.put("imageUrl", imageUrl);
        song.put("musicTitle",title);
        song.put("album", album);
        song.put("artists", artists);
        song.put("spotifyUrl", spotifyUrl);
        song.put("previewUrl", previewUrl);

        return song;
    }
    private boolean isTokenExpired(){

        if(accessTokenDto == null || (System.currentTimeMillis() - tokenUpdateTime) < accessTokenDto.getExpires_in()){
            return true;
        }else{
            return false;
        }
    }


}
