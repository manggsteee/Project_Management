package project.management.model;

import jakarta.persistence.*;
import lombok.*;
import project.management.project_enum.MemberRoleEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "project_member_role")
public class MemberRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRoleEnum role;
}
