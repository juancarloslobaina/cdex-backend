package com.jacdl.service.impl;

import com.jacdl.domain.Delivery;
import com.jacdl.repository.DeliveryRepository;
import com.jacdl.service.DeliveryService;
import com.jacdl.service.dto.DeliveryDTO;
import com.jacdl.service.mapper.DeliveryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.jacdl.domain.Delivery}.
 */
@Service
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);

    private final DeliveryRepository deliveryRepository;

    private final DeliveryMapper deliveryMapper;

    public DeliveryServiceImpl(DeliveryRepository deliveryRepository, DeliveryMapper deliveryMapper) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
    }

    @Override
    public DeliveryDTO save(DeliveryDTO deliveryDTO) {
        log.debug("Request to save Delivery : {}", deliveryDTO);
        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public DeliveryDTO update(DeliveryDTO deliveryDTO) {
        log.debug("Request to update Delivery : {}", deliveryDTO);
        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public Optional<DeliveryDTO> partialUpdate(DeliveryDTO deliveryDTO) {
        log.debug("Request to partially update Delivery : {}", deliveryDTO);

        return deliveryRepository
            .findById(deliveryDTO.getId())
            .map(existingDelivery -> {
                deliveryMapper.partialUpdate(existingDelivery, deliveryDTO);

                return existingDelivery;
            })
            .map(deliveryRepository::save)
            .map(deliveryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Deliveries");
        return deliveryRepository.findAll(pageable).map(deliveryMapper::toDto);
    }

    public Page<DeliveryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return deliveryRepository.findAllWithEagerRelationships(pageable).map(deliveryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeliveryDTO> findOne(Long id) {
        log.debug("Request to get Delivery : {}", id);
        return deliveryRepository.findOneWithEagerRelationships(id).map(deliveryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Delivery : {}", id);
        deliveryRepository.deleteById(id);
    }
}
