package org.example.persistence.dao;

import org.example.domain.model.ServicePrice;
import java.util.List;
import java.util.Optional;

public interface ServicePriceDao {
    ServicePrice create(ServicePrice sp);                 // zwraca z nadanym id
    Optional<ServicePrice> findById(long id);
    List<ServicePrice> findAll();
    void update(ServicePrice sp);                         // po id
    void delete(long id);                                 // NotFound to brak akcji
}
