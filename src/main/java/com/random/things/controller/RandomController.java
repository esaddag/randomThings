package com.random.things.controller;

import com.random.things.dto.SpotifyAccessTokenDto;
import com.random.things.service.BookService;
import com.random.things.service.MovieService;
import com.random.things.service.MusicService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
public class RandomController {

    MovieService movieService;
    MusicService musicService;
    BookService bookService;


    public RandomController(MovieService movieService, MusicService musicService, BookService bookService) {
        this.movieService = movieService;
        this.musicService = musicService;
        this.bookService = bookService;
    }

    @GetMapping("/random")
    String movie(Model model){

        Map<String, String> movieResponse = movieService.getForRandomMovie();
        model.addAttribute("movieImg", "https://image.tmdb.org/t/p/original/"+movieResponse.get("imageUrl"));
        model.addAttribute("movieTitle", movieResponse.get("movieTitle"));
        model.addAttribute("movieOverview", movieResponse.get("overview"));

        Map<String, String> song = musicService.getForRandomMusic();
        model.addAttribute("musicTitle", song.get("musicTitle"));
        model.addAttribute("album", song.get("album"));
        model.addAttribute("artists", song.get("artists"));
        model.addAttribute("musicImg", song.get("imageUrl"));
        model.addAttribute("previewUrl", song.get("previewUrl"));
        model.addAttribute("spotifyUrl", song.get("spotifyUrl"));

        Map<String, String> book = bookService.getForRandomBook();
        model.addAttribute("bookTitle", book.get("bookTitle"));
        model.addAttribute("authors", book.get("authors"));
        model.addAttribute("publisher", book.get("publisher"));
        model.addAttribute("description", book.get("description"));
        model.addAttribute("gBooksUrl", book.get("gBooksUrl"));
        model.addAttribute("bookImg", book.get("bookImg"));

        return "home";
    }
}
