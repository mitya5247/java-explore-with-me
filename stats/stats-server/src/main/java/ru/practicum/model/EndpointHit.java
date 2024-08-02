package ru.practicum.model;

import javax.persistence.*;

/**
 * Hello world!
 */
@Entity
@Table
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String app;
    String uri;
    String ip;
    String timestamp;
}
