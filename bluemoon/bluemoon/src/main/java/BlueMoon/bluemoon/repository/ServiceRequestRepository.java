package BlueMoon.bluemoon.repository;

import BlueMoon.bluemoon.models.ServiceRequest;
import BlueMoon.bluemoon.models.Resident;
import BlueMoon.bluemoon.models.ServiceRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByResidentOrderByCreatedAtDesc(Resident resident);

    List<ServiceRequest> findAllByOrderByCreatedAtDesc();

    List<ServiceRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    long countByStatus(RequestStatus status);

    List<ServiceRequest> findByServiceTypeOrderByCreatedAtDesc(String serviceType);
}