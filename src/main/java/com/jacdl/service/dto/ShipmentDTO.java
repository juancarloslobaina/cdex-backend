package com.jacdl.service.dto;

import com.jacdl.domain.enumeration.ShipmentStatus;
import com.jacdl.domain.enumeration.ShiptmentType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.jacdl.domain.Shipment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Instant createdAt;

    @NotNull
    private ShipmentStatus status;

    @NotNull
    private ShiptmentType type;

    @NotNull
    private Boolean active;

    @Lob
    private byte[] screenshot;

    private String screenshotContentType;

    @NotNull
    private ClientDTO client;

    @NotNull
    private ProviderDTO provider;

    private DeliveryDTO delivery;

    @NotNull
    private BeneficiaryDTO beneficiary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public ShiptmentType getType() {
        return type;
    }

    public void setType(ShiptmentType type) {
        this.type = type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public byte[] getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(byte[] screenshot) {
        this.screenshot = screenshot;
    }

    public String getScreenshotContentType() {
        return screenshotContentType;
    }

    public void setScreenshotContentType(String screenshotContentType) {
        this.screenshotContentType = screenshotContentType;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public ProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(ProviderDTO provider) {
        this.provider = provider;
    }

    public DeliveryDTO getDelivery() {
        return delivery;
    }

    public void setDelivery(DeliveryDTO delivery) {
        this.delivery = delivery;
    }

    public BeneficiaryDTO getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(BeneficiaryDTO beneficiary) {
        this.beneficiary = beneficiary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShipmentDTO)) {
            return false;
        }

        ShipmentDTO shipmentDTO = (ShipmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shipmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", amount=" + getAmount() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", type='" + getType() + "'" +
            ", active='" + getActive() + "'" +
            ", screenshot='" + getScreenshot() + "'" +
            ", client=" + getClient() +
            ", provider=" + getProvider() +
            ", delivery=" + getDelivery() +
            ", beneficiary=" + getBeneficiary() +
            "}";
    }
}
