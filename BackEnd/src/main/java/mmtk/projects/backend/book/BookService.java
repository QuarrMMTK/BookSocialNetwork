package mmtk.projects.backend.book;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mmtk.projects.backend.common.PageResponse;
import mmtk.projects.backend.exception.OperationNotPermittedException;
import mmtk.projects.backend.file.FileStorageService;
import mmtk.projects.backend.history.BookTransactionHistory;
import mmtk.projects.backend.history.BookTransitionHistoryRepository;
import mmtk.projects.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static mmtk.projects.backend.book.BookSpecification.withOwnerId;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final BookTransitionHistoryRepository transitionHistoryRepository;
    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;
    private final BookMapper bookMapper;

    public Integer save(BookRequest bookRequest, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(user);

        return bookRepository.save(book).getId();
    }

    public BookResponse findById(int bookId) {
        return bookRepository.findById(bookId).map(bookMapper::toBookResponse).orElseThrow(()-> new EntityNotFoundException("The Book with this id:: " + " is not found"));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transitionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transitionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("The Book with this id:: " + bookId + " is not found"));
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update the other book shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("The Book with this id:: " + bookId + " is not found"));
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update the other book archive able status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("The Book with this id:: " + bookId + " is not found"));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot borrow the book");
        }
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowed = transitionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if(isAlreadyBorrowed){
            throw new OperationNotPermittedException("You cannot borrow the already borrowed book");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory
                .builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transitionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("The Book with this id:: " + bookId));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot borrow the book");
        }
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        BookTransactionHistory  bookTransactionHistory = transitionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
        bookTransactionHistory.setReturned(true);
        return transitionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBook(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("The Book with this id:: " + bookId));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot borrow the book");
        }
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        BookTransactionHistory  bookTransactionHistory = transitionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return."));
        bookTransactionHistory.setReturnApproved(true);
        return transitionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("The Book with this id:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file, user.getId());

        if (bookCover != null) {
            book.setBookCover(bookCover);
            bookRepository.save(book);
        } else {
            log.error("Failed to upload the book cover picture for book ID {}", bookId);
            throw new RuntimeException("Failed to upload book cover picture.");
        }
    }
}
