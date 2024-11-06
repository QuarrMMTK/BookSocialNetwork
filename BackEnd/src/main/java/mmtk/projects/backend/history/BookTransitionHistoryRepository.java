package mmtk.projects.backend.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Author : Min Myat Thu Kha
 * Created At : 03/11/2024, Nov ,14, 24
 * Project Name : BookSocialNetwork
 **/
@Repository
public interface BookTransitionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query(
            """
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id = :userId
            """
    )
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query(
            """
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id = :userId
            """
    )
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query(
            """
            SELECT
            (count(*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransitionHistory
            WHERE bookTransitionHistory.user.id = :userId
            AND bookTransitionHistory.book.id = :bookId
            AND bookTransitionHistory.returnApproved = false
            """
    )
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

    @Query(
            """
            SELECT transition
            FROM BookTransactionHistory transition
            WHERE transition.user.id = :userId
            AND transition.book.id = :bookId
            AND transition.returned = false
            AND transition.returnApproved = false
            """
    )
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

    @Query(
            """
            SELECT transition
            FROM BookTransactionHistory transition
            WHERE transition.book.owner.id = :userId
            AND transition.book.id = :bookId
            AND transition.returned = true
            AND transition.returnApproved = false
            """
    )
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer userId);
}
