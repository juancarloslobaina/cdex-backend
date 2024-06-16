package com.jacdl.service.criteria;

import com.jacdl.domain.enumeration.ShipmentStatus;
import com.jacdl.domain.enumeration.ShiptmentType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.jacdl.domain.Shipment} entity. This class is used
 * in {@link com.jacdl.web.rest.ShipmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /shipments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ShipmentStatus
     */
    public static class ShipmentStatusFilter extends Filter<ShipmentStatus> {

        public ShipmentStatusFilter() {}

        public ShipmentStatusFilter(ShipmentStatusFilter filter) {
            super(filter);
        }

        @Override
        public ShipmentStatusFilter copy() {
            return new ShipmentStatusFilter(this);
        }
    }

    /**
     * Class for filtering ShiptmentType
     */
    public static class ShiptmentTypeFilter extends Filter<ShiptmentType> {

        public ShiptmentTypeFilter() {}

        public ShiptmentTypeFilter(ShiptmentTypeFilter filter) {
            super(filter);
        }

        @Override
        public ShiptmentTypeFilter copy() {
            return new ShiptmentTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private BigDecimalFilter amount;

    private InstantFilter createdAt;

    private ShipmentStatusFilter status;

    private ShiptmentTypeFilter type;

    private BooleanFilter active;

    private LongFilter clientId;

    private LongFilter providerId;

    private LongFilter deliveryId;

    private LongFilter beneficiaryId;

    private Boolean distinct;

    public ShipmentCriteria() {}

    public ShipmentCriteria(ShipmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ShipmentStatusFilter::copy).orElse(null);
        this.type = other.optionalType().map(ShiptmentTypeFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.clientId = other.optionalClientId().map(LongFilter::copy).orElse(null);
        this.providerId = other.optionalProviderId().map(LongFilter::copy).orElse(null);
        this.deliveryId = other.optionalDeliveryId().map(LongFilter::copy).orElse(null);
        this.beneficiaryId = other.optionalBeneficiaryId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ShipmentCriteria copy() {
        return new ShipmentCriteria(this);
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

    public StringFilter getReference() {
        return reference;
    }

    public Optional<StringFilter> optionalReference() {
        return Optional.ofNullable(reference);
    }

    public StringFilter reference() {
        if (reference == null) {
            setReference(new StringFilter());
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public Optional<BigDecimalFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            setAmount(new BigDecimalFilter());
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
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

    public ShipmentStatusFilter getStatus() {
        return status;
    }

    public Optional<ShipmentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ShipmentStatusFilter status() {
        if (status == null) {
            setStatus(new ShipmentStatusFilter());
        }
        return status;
    }

    public void setStatus(ShipmentStatusFilter status) {
        this.status = status;
    }

    public ShiptmentTypeFilter getType() {
        return type;
    }

    public Optional<ShiptmentTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public ShiptmentTypeFilter type() {
        if (type == null) {
            setType(new ShiptmentTypeFilter());
        }
        return type;
    }

    public void setType(ShiptmentTypeFilter type) {
        this.type = type;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public Optional<LongFilter> optionalClientId() {
        return Optional.ofNullable(clientId);
    }

    public LongFilter clientId() {
        if (clientId == null) {
            setClientId(new LongFilter());
        }
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
    }

    public LongFilter getProviderId() {
        return providerId;
    }

    public Optional<LongFilter> optionalProviderId() {
        return Optional.ofNullable(providerId);
    }

    public LongFilter providerId() {
        if (providerId == null) {
            setProviderId(new LongFilter());
        }
        return providerId;
    }

    public void setProviderId(LongFilter providerId) {
        this.providerId = providerId;
    }

    public LongFilter getDeliveryId() {
        return deliveryId;
    }

    public Optional<LongFilter> optionalDeliveryId() {
        return Optional.ofNullable(deliveryId);
    }

    public LongFilter deliveryId() {
        if (deliveryId == null) {
            setDeliveryId(new LongFilter());
        }
        return deliveryId;
    }

    public void setDeliveryId(LongFilter deliveryId) {
        this.deliveryId = deliveryId;
    }

    public LongFilter getBeneficiaryId() {
        return beneficiaryId;
    }

    public Optional<LongFilter> optionalBeneficiaryId() {
        return Optional.ofNullable(beneficiaryId);
    }

    public LongFilter beneficiaryId() {
        if (beneficiaryId == null) {
            setBeneficiaryId(new LongFilter());
        }
        return beneficiaryId;
    }

    public void setBeneficiaryId(LongFilter beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
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
        final ShipmentCriteria that = (ShipmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(status, that.status) &&
            Objects.equals(type, that.type) &&
            Objects.equals(active, that.active) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(providerId, that.providerId) &&
            Objects.equals(deliveryId, that.deliveryId) &&
            Objects.equals(beneficiaryId, that.beneficiaryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            amount,
            createdAt,
            status,
            type,
            active,
            clientId,
            providerId,
            deliveryId,
            beneficiaryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalClientId().map(f -> "clientId=" + f + ", ").orElse("") +
            optionalProviderId().map(f -> "providerId=" + f + ", ").orElse("") +
            optionalDeliveryId().map(f -> "deliveryId=" + f + ", ").orElse("") +
            optionalBeneficiaryId().map(f -> "beneficiaryId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
