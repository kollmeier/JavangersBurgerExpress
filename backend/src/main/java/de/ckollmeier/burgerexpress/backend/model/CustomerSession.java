package de.ckollmeier.burgerexpress.backend.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

import java.time.Instant;

@Builder
@With
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class CustomerSession {
    private final Instant createdAt;
    private final Instant expiresAt;
}
