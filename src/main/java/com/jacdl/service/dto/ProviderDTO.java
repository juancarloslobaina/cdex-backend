package com.jacdl.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.jacdl.domain.Provider} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProviderDTO implements Serializable {

    private Long id;

    @NotNull
    private String referralCode;

    @NotNull
    private UserDTO user;

    private Set<ClientDTO> clients = new HashSet<>();

    private Set<DeliveryDTO> deliveries = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<ClientDTO> getClients() {
        return clients;
    }

    public void setClients(Set<ClientDTO> clients) {
        this.clients = clients;
    }

    public Set<DeliveryDTO> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(Set<DeliveryDTO> deliveries) {
        this.deliveries = deliveries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProviderDTO)) {
            return false;
        }

        ProviderDTO providerDTO = (ProviderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, providerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProviderDTO{" +
            "id=" + getId() +
            ", referralCode='" + getReferralCode() + "'" +
            ", user=" + getUser() +
            ", clients=" + getClients() +
            ", deliveries=" + getDeliveries() +
            "}";
    }
}
