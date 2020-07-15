package rental;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface CheckRepository extends PagingAndSortingRepository<Check, Long>{

    Check findByOrderId(Long id);
}