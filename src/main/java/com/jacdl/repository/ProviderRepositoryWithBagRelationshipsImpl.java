package com.jacdl.repository;

import com.jacdl.domain.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ProviderRepositoryWithBagRelationshipsImpl implements ProviderRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PROVIDERS_PARAMETER = "providers";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Provider> fetchBagRelationships(Optional<Provider> provider) {
        return provider.map(this::fetchClients).map(this::fetchDeliveries);
    }

    @Override
    public Page<Provider> fetchBagRelationships(Page<Provider> providers) {
        return new PageImpl<>(fetchBagRelationships(providers.getContent()), providers.getPageable(), providers.getTotalElements());
    }

    @Override
    public List<Provider> fetchBagRelationships(List<Provider> providers) {
        return Optional.of(providers).map(this::fetchClients).map(this::fetchDeliveries).orElse(Collections.emptyList());
    }

    Provider fetchClients(Provider result) {
        return entityManager
            .createQuery("select provider from Provider provider left join fetch provider.clients where provider.id = :id", Provider.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Provider> fetchClients(List<Provider> providers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, providers.size()).forEach(index -> order.put(providers.get(index).getId(), index));
        List<Provider> result = entityManager
            .createQuery(
                "select provider from Provider provider left join fetch provider.clients where provider in :providers",
                Provider.class
            )
            .setParameter(PROVIDERS_PARAMETER, providers)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Provider fetchDeliveries(Provider result) {
        return entityManager
            .createQuery(
                "select provider from Provider provider left join fetch provider.deliveries where provider.id = :id",
                Provider.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Provider> fetchDeliveries(List<Provider> providers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, providers.size()).forEach(index -> order.put(providers.get(index).getId(), index));
        List<Provider> result = entityManager
            .createQuery(
                "select provider from Provider provider left join fetch provider.deliveries where provider in :providers",
                Provider.class
            )
            .setParameter(PROVIDERS_PARAMETER, providers)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
