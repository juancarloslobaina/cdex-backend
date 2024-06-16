package com.jacdl.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.jacdl.domain.Beneficiary} entity. This class is used
 * in {@link com.jacdl.web.rest.BeneficiaryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /beneficiaries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BeneficiaryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter alias;

    private StringFilter phone;

    private StringFilter city;

    private StringFilter address;

    private StringFilter referenceAddress;

    private LongFilter userId;

    private Boolean distinct;

    public BeneficiaryCriteria() {}

    public BeneficiaryCriteria(BeneficiaryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.alias = other.optionalAlias().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.city = other.optionalCity().map(StringFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.referenceAddress = other.optionalReferenceAddress().map(StringFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BeneficiaryCriteria copy() {
        return new BeneficiaryCriteria(this);
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

    public StringFilter getAlias() {
        return alias;
    }

    public Optional<StringFilter> optionalAlias() {
        return Optional.ofNullable(alias);
    }

    public StringFilter alias() {
        if (alias == null) {
            setAlias(new StringFilter());
        }
        return alias;
    }

    public void setAlias(StringFilter alias) {
        this.alias = alias;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getCity() {
        return city;
    }

    public Optional<StringFilter> optionalCity() {
        return Optional.ofNullable(city);
    }

    public StringFilter city() {
        if (city == null) {
            setCity(new StringFilter());
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getReferenceAddress() {
        return referenceAddress;
    }

    public Optional<StringFilter> optionalReferenceAddress() {
        return Optional.ofNullable(referenceAddress);
    }

    public StringFilter referenceAddress() {
        if (referenceAddress == null) {
            setReferenceAddress(new StringFilter());
        }
        return referenceAddress;
    }

    public void setReferenceAddress(StringFilter referenceAddress) {
        this.referenceAddress = referenceAddress;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final BeneficiaryCriteria that = (BeneficiaryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(alias, that.alias) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(city, that.city) &&
            Objects.equals(address, that.address) &&
            Objects.equals(referenceAddress, that.referenceAddress) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alias, phone, city, address, referenceAddress, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BeneficiaryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAlias().map(f -> "alias=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalCity().map(f -> "city=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalReferenceAddress().map(f -> "referenceAddress=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
