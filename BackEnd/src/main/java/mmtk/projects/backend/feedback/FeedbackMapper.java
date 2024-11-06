package mmtk.projects.backend.feedback;

import mmtk.projects.backend.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Author : Min Myat Thu Kha
 * Created At : 03/11/2024, Nov ,14, 36
 * Project Name : BookSocialNetwork
 **/
@Service
public class FeedbackMapper {

    public FeedBack toFeedback(FeedbackRequest request) {
        return FeedBack.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .archived(false) //Not Required and has no impact :: just to satisfy lombok
                        .shareable(false) //Not Required and has no impact :: just to satisfy lombok
                        .build())
                .build();
    }

    public FeedBackResponse toFeedbackResponse(FeedBack f, Integer id) {
        return FeedBackResponse.builder()
                .note(f.getNote())
                .comment(f.getComment())
                .ownFeedback(Objects.equals(f.getCreateBy(), id))
                .build();
    }
}
