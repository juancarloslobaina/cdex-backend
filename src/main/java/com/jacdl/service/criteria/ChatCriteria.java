package com.jacdl.service.criteria;

import com.jacdl.domain.enumeration.MessageStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.jacdl.domain.Chat} entity. This class is used
 * in {@link com.jacdl.web.rest.ChatResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /chats?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChatCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MessageStatus
     */
    public static class MessageStatusFilter extends Filter<MessageStatus> {

        public MessageStatusFilter() {}

        public MessageStatusFilter(MessageStatusFilter filter) {
            super(filter);
        }

        @Override
        public MessageStatusFilter copy() {
            return new MessageStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter message;

    private InstantFilter createdAt;

    private MessageStatusFilter status;

    private LongFilter fromId;

    private LongFilter toId;

    private Boolean distinct;

    public ChatCriteria() {}

    public ChatCriteria(ChatCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.message = other.optionalMessage().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(MessageStatusFilter::copy).orElse(null);
        this.fromId = other.optionalFromId().map(LongFilter::copy).orElse(null);
        this.toId = other.optionalToId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ChatCriteria copy() {
        return new ChatCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getMessage() {
        return message;
    }

    public Optional<StringFilter> optionalMessage() {
        return Optional.ofNullable(message);
    }

    public StringFilter message() {
        if (message == null) {
            setMessage(new StringFilter());
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public MessageStatusFilter getStatus() {
        return status;
    }

    public Optional<MessageStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public MessageStatusFilter status() {
        if (status == null) {
            setStatus(new MessageStatusFilter());
        }
        return status;
    }

    public void setStatus(MessageStatusFilter status) {
        this.status = status;
    }

    public LongFilter getFromId() {
        return fromId;
    }

    public Optional<LongFilter> optionalFromId() {
        return Optional.ofNullable(fromId);
    }

    public LongFilter fromId() {
        if (fromId == null) {
            setFromId(new LongFilter());
        }
        return fromId;
    }

    public void setFromId(LongFilter fromId) {
        this.fromId = fromId;
    }

    public LongFilter getToId() {
        return toId;
    }

    public Optional<LongFilter> optionalToId() {
        return Optional.ofNullable(toId);
    }

    public LongFilter toId() {
        if (toId == null) {
            setToId(new LongFilter());
        }
        return toId;
    }

    public void setToId(LongFilter toId) {
        this.toId = toId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ChatCriteria that = (ChatCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(message, that.message) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(status, that.status) &&
            Objects.equals(fromId, that.fromId) &&
            Objects.equals(toId, that.toId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, createdAt, status, fromId, toId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChatCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMessage().map(f -> "message=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalFromId().map(f -> "fromId=" + f + ", ").orElse("") +
            optionalToId().map(f -> "toId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
