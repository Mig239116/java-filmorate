package ru.yandex.practicum.filmorate.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.io.IOException;

public class FilmSerializer extends StdSerializer<Film> {

    public FilmSerializer() {
        super(Film.class);
    }

    @Override
    public void serialize(Film film, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeNumberField("id", film.getId());
        gen.writeStringField("name", film.getName());
        gen.writeStringField("description", film.getDescription());
        gen.writeStringField("releaseDate", film.getReleaseDate().toString());

        gen.writeNumberField("duration", film.getDuration().toMinutes());

        if (film.getMpa() != null) {
            gen.writeObjectFieldStart("mpa");
            gen.writeNumberField("id", film.getMpa().getId());
            if (film.getMpa().getName() != null) {
                gen.writeStringField("name", film.getMpa().getName());
            }
            gen.writeEndObject();
        }


        gen.writeArrayFieldStart("genres");
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                gen.writeStartObject();
                gen.writeNumberField("id", genre.getId());
                if (genre.getName() != null) {
                    gen.writeStringField("name", genre.getName());
                }
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
