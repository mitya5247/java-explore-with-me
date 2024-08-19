package ru.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Data
@Valid
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "app")
    String app;
    @Column(name = "uri")
    String uri;
    @Column(name = "ip")
    String ip;
    @PastOrPresent
    @Column(name = "moment")
    LocalDateTime timestamp;
}
