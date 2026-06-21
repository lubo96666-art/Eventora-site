package bg.softuni.eventora.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordDto {
    @Email(message = "Въведи валиден имейл.")
    @NotBlank(message = "Въведи имейл.")
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
