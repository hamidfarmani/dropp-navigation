package model.entity.persistent;

import model.enums.UserRole;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by kasra on 4/30/2017.
 */
@NamedQueries({
        @NamedQuery(name = "ticketSubject.All", query = "select t from ticketSubject t"),
        @NamedQuery(name = "ticketSubject.subject.exist", query = "select t.id from ticketSubject t where t.subject=:subject"),
        @NamedQuery(name = "ticketSubject.findby.id", query = "select t from ticketSubject t where t.id=:id")
})
@Entity(name = "ticketSubject")
@Table(name = "TICKET_SUBJECT")
public class TicketSubject implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "ticketSubjectGen", sequenceName = "ticketSubjectSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ticketSubjectGen")
    private Long id;

    @Basic
    @Column(name = "SUBJECT", columnDefinition = "NVARCHAR2(50)")
    private String subject;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "FK_TICKET_SUBJECT", referencedColumnName = "ID")
    private List<TicketSubject> ticketSubjects;

    @Basic
    @Column(name = "ROLE",columnDefinition = "CHAR")
    private UserRole role;

    public TicketSubject() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<TicketSubject> getTicketSubjects() {
        return ticketSubjects;
    }

    public void setTicketSubjects(List<TicketSubject> ticketSubjects) {
        this.ticketSubjects = ticketSubjects;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
