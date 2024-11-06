package mmtk.projects.backend.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Author : Name
 * Created At : 03/11/2024, Nov ,14, 40
 * Project Name : BookSocialNetwork
 **/
@Repository
public interface FeedbackRepository extends JpaRepository<FeedBack, Integer> {
    @Query(
            """
            SELECT feedback
            FROM FeedBack feedback
            WHERE feedback.book.id = :bookId
"""
    )
    Page<FeedBack> findAllByBookId(Integer bookId, Pageable pageable);
}
