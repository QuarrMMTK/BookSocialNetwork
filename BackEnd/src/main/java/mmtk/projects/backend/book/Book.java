package mmtk.projects.backend.book;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mmtk.projects.backend.common.BaseEntity;
import mmtk.projects.backend.feedback.FeedBack;
import mmtk.projects.backend.history.BookTransactionHistory;
import mmtk.projects.backend.user.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Book extends BaseEntity {

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<FeedBack> feedBacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    @Transient
    public double getRate(){
        if(feedBacks == null || feedBacks.isEmpty()){
            return 0.0;
        }
        var rate = this.feedBacks.stream().mapToDouble(FeedBack::getNote).average().orElse(0.0);
        return Math.round(rate * 10.0) / 10.0;
    }

}
