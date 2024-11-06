package mmtk.projects.backend.token;

import jakarta.persistence.*;
import lombok.*;
import mmtk.projects.backend.user.User;

import java.time.LocalDateTime;

/**
 * Author : Min Myat Thu Kha
 * Created At : 03/11/2024, Nov ,14, 24
 * Project Name : BookSocialNetwork
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime validatedAt;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
