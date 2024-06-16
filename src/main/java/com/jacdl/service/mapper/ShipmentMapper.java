package com.jacdl.service.mapper;

import com.jacdl.domain.Beneficiary;
import com.jacdl.domain.Client;
import com.jacdl.domain.Delivery;
import com.jacdl.domain.Provider;
import com.jacdl.domain.Shipment;
import com.jacdl.service.dto.BeneficiaryDTO;
import com.jacdl.service.dto.ClientDTO;
import com.jacdl.service.dto.DeliveryDTO;
import com.jacdl.service.dto.ProviderDTO;
import com.jacdl.service.dto.ShipmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shipment} and its DTO {@link ShipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentMapper extends EntityMapper<ShipmentDTO, Shipment> {
    @Mapping(target = "client", source = "client", qualifiedByName = "clientId")
    @Mapping(target = "provider", source = "provider", qualifiedByName = "providerId")
    @Mapping(target = "delivery", source = "delivery", qualifiedByName = "deliveryId")
    @Mapping(target = "beneficiary", source = "beneficiary", qualifiedByName = "beneficiaryId")
    ShipmentDTO toDto(Shipment s);

    @Named("clientId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientDTO toDtoClientId(Client client);

    @Named("providerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProviderDTO toDtoProviderId(Provider provider);

    @Named("deliveryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DeliveryDTO toDtoDeliveryId(Delivery delivery);

    @Named("beneficiaryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BeneficiaryDTO toDtoBeneficiaryId(Beneficiary beneficiary);
}
