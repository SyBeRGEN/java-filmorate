package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private int id;
    @NotEmpty
    private String name;
    @Size(max = 200, message = "Длинна описания должна быть не больше 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;

}
