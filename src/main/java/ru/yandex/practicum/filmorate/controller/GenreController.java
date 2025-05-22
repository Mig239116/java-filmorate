package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Validated
public class GenreController {
    final GenreService genreService;

    @Autowired
    public GenreController(@Qualifier("genreDbService") GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    @GetMapping("/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Genre getById(@Valid @PathVariable long genreId) {
        return genreService.getById(genreId);
    }
}
