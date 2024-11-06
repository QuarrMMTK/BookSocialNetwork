package mmtk.projects.backend.feedback;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mmtk.projects.backend.book.Book;
import mmtk.projects.backend.common.BaseEntity;

/**
 * Author : Min Myat Thu Kha
 * Created At : 03/11/2024, Nov ,14, 24
 * Project Name : BookSocialNetwork
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class FeedBack extends BaseEntity {
    private Double note; // 1 - 5 stars
    private String comment;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
