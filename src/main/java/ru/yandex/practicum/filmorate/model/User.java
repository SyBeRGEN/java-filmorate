package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    public Set<Integer> friends = new HashSet<>();
    private int id;
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    private String login;
    private String name = "";
    @PastOrPresent
    private LocalDate birthday;

}
