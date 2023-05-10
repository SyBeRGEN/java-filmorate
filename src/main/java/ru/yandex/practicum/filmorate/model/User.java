package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.utils.Edata;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Edata {
    private Integer id;
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    private String login;
    private String name = "";
    @PastOrPresent
    private LocalDate birthday;

}
