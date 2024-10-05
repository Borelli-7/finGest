package dev.kaly7.finGest.entities;

import java.time.Instant;

public class NotificationJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long notifID;
    private String message;
    private Instant createdDate;
}
