package bg.softuni.eventorareviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ReviewUpdateRequest(@NotNull UUID authorId, @Min(1) @Max(5) int rating,
                                  @NotBlank @Size(min = 5, max = 500) String comment) {
}
