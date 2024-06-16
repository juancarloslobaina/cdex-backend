package com.jacdl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Delivery.
 */
@Entity
@Table(name = "delivery")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Delivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "cash_available", precision = 21, scale = 2, nullable = false)
    private BigDecimal cashAvailable;

    @Column(name = "location")
    private String location;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "deliveries")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "clients", "deliveries" }, allowSetters = true)
    private Set<Provider> providers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Delivery id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCashAvailable() {
        return this.cashAvailable;
    }

    public Delivery cashAvailable(BigDecimal cashAvailable) {
        this.setCashAvailable(cashAvailable);
        return this;
    }

    public void setCashAvailable(BigDecimal cashAvailable) {
        this.cashAvailable = cashAvailable;
    }

    public String getLocation() {
        return this.location;
    }

    public Delivery location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Delivery user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Provider> getProviders() {
        return this.providers;
    }

    public void setProviders(Set<Provider> providers) {
        if (this.providers != null) {
            this.providers.forEach(i -> i.removeDelivery(this));
        }
        if (providers != null) {
            providers.forEach(i -> i.addDelivery(this));
        }
        this.providers = providers;
    }

    public Delivery providers(Set<Provider> providers) {
        this.setProviders(providers);
        return this;
    }

    public Delivery addProvider(Provider provider) {
        this.providers.add(provider);
        provider.getDeliveries().add(this);
        return this;
    }

    public Delivery removeProvider(Provider provider) {
        this.providers.remove(provider);
        provider.getDeliveries().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Delivery)) {
            return false;
        }
        return getId() != null && getId().equals(((Delivery) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Delivery{" +
            "id=" + getId() +
            ", cashAvailable=" + getCashAvailable() +
            ", location='" + getLocation() + "'" +
            "}";
    }
}
