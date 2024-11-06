package mmtk.projects.backend.book;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mmtk.projects.backend.common.BaseEntity;
import mmtk.projects.backend.feedback.FeedBack;
import mmtk.projects.backend.history.BookTransactionHistory;
import mmtk.projects.backend.user.User;
import java.util.List;

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
