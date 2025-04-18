package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "SECONDS")
    private Duration duration;

    private Set<Long> likes = new HashSet<>();

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
