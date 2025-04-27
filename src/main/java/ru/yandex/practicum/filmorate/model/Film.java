package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.serializers.FilmDeserializer;
import ru.yandex.practicum.filmorate.serializers.FilmSerializer;
import ru.yandex.practicum.filmorate.validators.MinDuration;
import ru.yandex.practicum.filmorate.validators.NotBefore;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = FilmDeserializer.class)
@JsonSerialize(using = FilmSerializer.class)
public class Film {
    private Long id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;


    @NotNull
    @NotBefore(value = "1895-12-28")
    private LocalDate releaseDate;

    @MinDuration(1)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Duration duration;

    private Mpa mpa;


    private Set<Long> likes = new HashSet<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Genre> genres = new HashSet<>();

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void deleteLike(Long userId) {
        likes.remove(userId);
    }

    public Integer getLikesCount() {
        return likes.size();
    }
}
