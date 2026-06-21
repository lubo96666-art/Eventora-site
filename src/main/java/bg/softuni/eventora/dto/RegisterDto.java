package bg.softuni.eventora.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterDto {
    @NotBlank(message = "Въведи потребителско име.")
    @Size(min = 3, max = 30, message = "Името трябва да е между 3 и 30 символа.")
    private String username;

    @Email(message = "Въведи валиден имейл.")
    @NotBlank(message = "Въведи имейл.")
    private String email;

    @NotBlank(message = "Въведи парола.")
    @Size(min = 6, max = 60, message = "Паролата трябва да е поне 6 символа.")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
