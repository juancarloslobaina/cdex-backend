package com.jacdl.service.dto;

import com.jacdl.domain.enumeration.MessageStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.jacdl.domain.Chat} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChatDTO implements Serializable {

    private Long id;

    @NotNull
    private String message;

    @NotNull
    private Instant createdAt;

    private MessageStatus status;

    @NotNull
    private UserDTO from;

    @NotNull
    private UserDTO to;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public UserDTO getFrom() {
        return from;
    }

    public void setFrom(UserDTO from) {
        this.from = from;
    }

    public UserDTO getTo() {
        return to;
    }

    public void setTo(UserDTO to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatDTO)) {
            return false;
        }

        ChatDTO chatDTO = (ChatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, chatDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChatDTO{" +
            "id=" + getId() +
            ", message='" + getMessage() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", from=" + getFrom() +
            ", to=" + getTo() +
            "}";
    }
}
