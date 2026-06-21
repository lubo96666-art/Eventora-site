package bg.softuni.eventora.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerifyCodeDto {
    @NotBlank(message = "Имейлът е задължителен.")
    @Email(message = "Въведи валиден имейл.")
    private String email;

    @NotBlank(message = "Кодът е задължителен.")
    @Pattern(regexp = "\\d{6}", message = "Кодът трябва да е точно 6 цифри.")
    private String code;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
