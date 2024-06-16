package com.jacdl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jacdl.domain.enumeration.ShipmentStatus;
import com.jacdl.domain.enumeration.ShiptmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Shipment.
 */
@Entity
@Table(name = "shipment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Shipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false)
    private String reference;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ShiptmentType type;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Lob
    @Column(name = "screenshot", nullable = false)
    private byte[] screenshot;

    @NotNull
    @Column(name = "screenshot_content_type", nullable = false)
    private String screenshotContentType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "providers" }, allowSetters = true)
    private Client client;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "clients", "deliveries" }, allowSetters = true)
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "providers" }, allowSetters = true)
    private Delivery delivery;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Beneficiary beneficiary;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Shipment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Shipment reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Shipment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Shipment createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ShipmentStatus getStatus() {
        return this.status;
    }

    public Shipment status(ShipmentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public ShiptmentType getType() {
        return this.type;
    }

    public Shipment type(ShiptmentType type) {
        this.setType(type);
        return this;
    }

    public void setType(ShiptmentType type) {
        this.type = type;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Shipment active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public byte[] getScreenshot() {
        return this.screenshot;
    }

    public Shipment screenshot(byte[] screenshot) {
        this.setScreenshot(screenshot);
        return this;
    }

    public void setScreenshot(byte[] screenshot) {
        this.screenshot = screenshot;
    }

    public String getScreenshotContentType() {
        return this.screenshotContentType;
    }

    public Shipment screenshotContentType(String screenshotContentType) {
        this.screenshotContentType = screenshotContentType;
        return this;
    }

    public void setScreenshotContentType(String screenshotContentType) {
        this.screenshotContentType = screenshotContentType;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Shipment client(Client client) {
        this.setClient(client);
        return this;
    }

    public Provider getProvider() {
        return this.provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Shipment provider(Provider provider) {
        this.setProvider(provider);
        return this;
    }

    public Delivery getDelivery() {
        return this.delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Shipment delivery(Delivery delivery) {
        this.setDelivery(delivery);
        return this;
    }

    public Beneficiary getBeneficiary() {
        return this.beneficiary;
    }

    public void setBeneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public Shipment beneficiary(Beneficiary beneficiary) {
        this.setBeneficiary(beneficiary);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shipment)) {
            return false;
        }
        return getId() != null && getId().equals(((Shipment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shipment{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", amount=" + getAmount() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", type='" + getType() + "'" +
            ", active='" + getActive() + "'" +
            ", screenshot='" + getScreenshot() + "'" +
            ", screenshotContentType='" + getScreenshotContentType() + "'" +
            "}";
    }
}
