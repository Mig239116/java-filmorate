package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank
    @Email
    @NotEmpty
    private String email;

    @NotBlank
    @NotEmpty
    @NotNull
    private String login;
    private String name;

    @PastOrPresent
    @NotNull
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public void addFriend(Long userId) {
        friends.add(userId);
    }

    public void deleteFriend(Long userId) {
        friends.remove(userId);
    }

}
