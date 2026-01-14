package com.dudev.orderservice.model;

import com.dudev.orderservice.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = false, updatable = false)
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Status status;

    @CreatedDate
    private LocalDateTime createdAt;
}
