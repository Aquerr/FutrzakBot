package io.github.aquerr.futrzakbot.storage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.time.OffsetDateTime;

@Entity
@Table(name = "mini_game_encounter")
@Data
public class MiniGameEncounter
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "game_name", nullable = false)
    private String gameName;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(name = "created_date", nullable = false)
    @TimeZoneStorage(TimeZoneStorageType.NATIVE)
    private OffsetDateTime createdDate;

    @Column(name = "expiration_date", nullable = false)
    @TimeZoneStorage(TimeZoneStorageType.NATIVE)
    private OffsetDateTime expirationDate;
}
