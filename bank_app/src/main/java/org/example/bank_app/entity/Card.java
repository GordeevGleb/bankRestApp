package org.example.bank_app.entity;



import jakarta.persistence.*;
import lombok.*;
import org.example.bank_app.entity.enums.CardStatus;

import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Card.withUser",
                attributeNodes = @NamedAttributeNode("user")
        )
})
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @Column(name = "balance", nullable = false)
    private Double balance;
}
