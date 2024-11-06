package mmtk.projects.backend.feedback;

import jakarta.validation.constraints.*;
import lombok.NonNull;

/**
 * Author : Min Myat Thu Kha
 * Created At : 03/11/2024, Nov ,14, 27
 * Project Name : BookSocialNetwork
 **/

public record FeedbackRequest(
        @Positive(message = "200")
        @Min(message = "201", value = 0)
        @Max(message = "202", value = 5)
        Double note,

        @NotNull(message = "203")
        @NotBlank(message = "203")
        @NotEmpty(message = "203")
        String comment,

        @NotNull(message = "204")
        Integer bookId
) {
}
