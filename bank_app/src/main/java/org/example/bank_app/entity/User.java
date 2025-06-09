package org.example.bank_app.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.bank_app.entity.enums.Roles;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "User.withRoles",
                attributeNodes = @NamedAttributeNode("roles")
        ),
        @NamedEntityGraph(
                name = "User.withCards",
                attributeNodes = @NamedAttributeNode("cards")
        ),
        @NamedEntityGraph(
                name = "User.withRolesAndCards",
                attributeNodes = {
                        @NamedAttributeNode("roles"),
                        @NamedAttributeNode("cards")
                }
        )
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String password;

    private String name;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Card> cards;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles;

    @Column(name = "is_blocked")
    private Boolean isBlocked;
}
