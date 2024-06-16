package com.jacdl.service.impl;

import com.jacdl.domain.Beneficiary;
import com.jacdl.repository.BeneficiaryRepository;
import com.jacdl.service.BeneficiaryService;
import com.jacdl.service.dto.BeneficiaryDTO;
import com.jacdl.service.mapper.BeneficiaryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.jacdl.domain.Beneficiary}.
 */
@Service
@Transactional
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final Logger log = LoggerFactory.getLogger(BeneficiaryServiceImpl.class);

    private final BeneficiaryRepository beneficiaryRepository;

    private final BeneficiaryMapper beneficiaryMapper;

    public BeneficiaryServiceImpl(BeneficiaryRepository beneficiaryRepository, BeneficiaryMapper beneficiaryMapper) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.beneficiaryMapper = beneficiaryMapper;
    }

    @Override
    public BeneficiaryDTO save(BeneficiaryDTO beneficiaryDTO) {
        log.debug("Request to save Beneficiary : {}", beneficiaryDTO);
        Beneficiary beneficiary = beneficiaryMapper.toEntity(beneficiaryDTO);
        beneficiary = beneficiaryRepository.save(beneficiary);
        return beneficiaryMapper.toDto(beneficiary);
    }

    @Override
    public BeneficiaryDTO update(BeneficiaryDTO beneficiaryDTO) {
        log.debug("Request to update Beneficiary : {}", beneficiaryDTO);
        Beneficiary beneficiary = beneficiaryMapper.toEntity(beneficiaryDTO);
        beneficiary = beneficiaryRepository.save(beneficiary);
        return beneficiaryMapper.toDto(beneficiary);
    }

    @Override
    public Optional<BeneficiaryDTO> partialUpdate(BeneficiaryDTO beneficiaryDTO) {
        log.debug("Request to partially update Beneficiary : {}", beneficiaryDTO);

        return beneficiaryRepository
            .findById(beneficiaryDTO.getId())
            .map(existingBeneficiary -> {
                beneficiaryMapper.partialUpdate(existingBeneficiary, beneficiaryDTO);

                return existingBeneficiary;
            })
            .map(beneficiaryRepository::save)
            .map(beneficiaryMapper::toDto);
    }

    public Page<BeneficiaryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return beneficiaryRepository.findAllWithEagerRelationships(pageable).map(beneficiaryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BeneficiaryDTO> findOne(Long id) {
        log.debug("Request to get Beneficiary : {}", id);
        return beneficiaryRepository.findOneWithEagerRelationships(id).map(beneficiaryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Beneficiary : {}", id);
        beneficiaryRepository.deleteById(id);
    }
}
