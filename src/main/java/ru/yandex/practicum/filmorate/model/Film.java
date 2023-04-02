package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private int id;
    public Map<Integer, Boolean> likes = new HashMap<>();
    public int likesCount = 0;
    @NotEmpty
    private String name;
    @Size(max = 200, message = "Длинна описания должна быть не больше 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;

    public void likesCounter() {
        int intLikes = 0;
        for (Boolean aBoolean : likes.values()) {
            if (aBoolean) {
                intLikes += 1;
            }
        }
        likesCount = intLikes;
    }

}
