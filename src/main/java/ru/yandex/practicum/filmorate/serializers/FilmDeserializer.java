package ru.yandex.practicum.filmorate.serializers;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FilmDeserializer extends StdDeserializer<Film> {

    public FilmDeserializer() {
        super(Film.class);
    }

    @Override
    public Film deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Film film = new Film();

        if (node.has("id")) {
            film.setId(node.get("id").asLong());
        }
        film.setName(node.get("name").asText());
        film.setDescription(node.get("description").asText());
        film.setReleaseDate(LocalDate.parse(node.get("releaseDate").asText()));

        film.setDuration(Duration.ofMinutes(node.get("duration").asLong()));

        if (node.has("mpa")) {
            Mpa mpa = new Mpa();
            mpa.setId(node.get("mpa").get("id").asLong());
            film.setMpa(mpa);
        }

        if (node.has("genres")) {
            Set<Genre> genres = new HashSet<>();
            JsonNode genresNode = node.get("genres");
            if (genresNode.isArray()) {
                for (JsonNode genreNode : genresNode) {
                    Genre genre = new Genre();
                    if (genreNode.has("id")) {
                        genre.setId(genreNode.get("id").asLong());
                    } else {
                        genre.setId(genreNode.asLong());
                    }
                    genres.add(genre);
                }
            }
            film.setGenres(genres);
        }

        return film;
    }
}

