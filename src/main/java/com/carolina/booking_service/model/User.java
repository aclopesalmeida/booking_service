package com.carolina.booking_service.model;

import com.carolina.booking_service.validation.Create;
import com.carolina.booking_service.validation.Update;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email", name = "unique_user_email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(groups = Create.class)
    private String firstName;

    @Column(nullable = false)
    @NotEmpty(groups = Create.class)
    private String lastName;

    @NotEmpty(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    @Column(nullable = false)
    private String email;

    @NotEmpty(groups = Create.class)
    @Size(min = 8, message = "Your password must be at least 8 characters long")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public User() {}

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * Constructor handy for unit testing
     */
    public User(Long id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Booking> getBookings() {
        if (this.bookings != null) {
            List<Booking> copy = new ArrayList<>();
            copy.addAll(this.bookings);
            return copy;
        }
        return this.bookings;
    }

    public void setBookings(List<Booking> bookings) {
        if (bookings != null) {
            List<Booking> copy = new ArrayList<>(bookings);
            this.bookings = copy;
        }
        this.bookings = bookings;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        String result = "User{";
        if (firstName != null) {
            result += firstName + " ,";
        }
        if (lastName != null) {
            result += lastName + " ,";
        }
        if (email != null) {
            result += email + " ,";
        }
        if (password != null) {
            result += password + " ,";
        }
        return result;
    }
}
