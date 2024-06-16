package com.jacdl.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.jacdl.domain.Delivery} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal cashAvailable;

    private String location;

    @NotNull
    private UserDTO user;

    private Set<ProviderDTO> providers = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCashAvailable() {
        return cashAvailable;
    }

    public void setCashAvailable(BigDecimal cashAvailable) {
        this.cashAvailable = cashAvailable;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<ProviderDTO> getProviders() {
        return providers;
    }

    public void setProviders(Set<ProviderDTO> providers) {
        this.providers = providers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryDTO)) {
            return false;
        }

        DeliveryDTO deliveryDTO = (DeliveryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deliveryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryDTO{" +
            "id=" + getId() +
            ", cashAvailable=" + getCashAvailable() +
            ", location='" + getLocation() + "'" +
            ", user=" + getUser() +
            ", providers=" + getProviders() +
            "}";
    }
}
