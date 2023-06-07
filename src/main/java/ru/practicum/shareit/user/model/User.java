package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder(toBuilder = true)
public class User {
    long id;
    @NotBlank
    String name;
    @NotNull
    @Email
    String email;
}
