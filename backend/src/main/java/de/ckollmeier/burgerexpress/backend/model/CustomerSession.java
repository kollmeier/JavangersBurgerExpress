package de.ckollmeier.burgerexpress.backend.model;

import lombok.Builder;
import lombok.With;

import java.io.Serializable;
import java.time.Instant;

@Builder
@With
public record CustomerSession(Instant createdAt, Instant expiresAt) implements Serializable {
}
