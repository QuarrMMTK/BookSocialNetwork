package mmtk.projects.backend.feedback;

import lombok.*;

/**
 * Author : Name
 * Created At : 03/11/2024, Nov ,14, 45
 * Project Name : BookSocialNetwork
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedBackResponse {

    private Double note;
    private String comment;
    private boolean ownFeedback;

}
