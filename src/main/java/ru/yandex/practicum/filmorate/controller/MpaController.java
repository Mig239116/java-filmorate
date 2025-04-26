package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Validated
public class MpaController {
    final MpaService mpaService;

    @Autowired
    public MpaController(@Qualifier("mpaDbService") MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }

    @GetMapping("/{mpaId}")
    @ResponseStatus(HttpStatus.OK)
    public Mpa getById(@Valid @PathVariable long mpaId) {
        return mpaService.getById(mpaId);
    }
}
