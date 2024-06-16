package com.jacdl.repository;

import com.jacdl.domain.Provider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ProviderRepositoryWithBagRelationships {
    Optional<Provider> fetchBagRelationships(Optional<Provider> provider);

    List<Provider> fetchBagRelationships(List<Provider> providers);

    Page<Provider> fetchBagRelationships(Page<Provider> providers);
}
