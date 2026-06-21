package bg.softuni.eventora.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordDto {
    @NotBlank
    private String token;

    @NotBlank(message = "Въведи нова парола.")
    @Size(min = 6, max = 60, message = "Паролата трябва да е поне 6 символа.")
    private String password;

    @NotBlank(message = "Повтори паролата.")
    private String confirmPassword;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
