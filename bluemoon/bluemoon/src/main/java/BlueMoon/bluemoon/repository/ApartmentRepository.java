package BlueMoon.bluemoon.repository;

import BlueMoon.bluemoon.models.Apartment;
import BlueMoon.bluemoon.models.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findByOwner(Resident owner);
}