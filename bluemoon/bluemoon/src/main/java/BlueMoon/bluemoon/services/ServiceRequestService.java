package BlueMoon.bluemoon.services;

import BlueMoon.bluemoon.models.ServiceRequest;
import BlueMoon.bluemoon.models.Resident;
import BlueMoon.bluemoon.models.ServiceRequest.RequestStatus;
import BlueMoon.bluemoon.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Transactional
    public ServiceRequest createRequest(ServiceRequest request) {
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);
        return serviceRequestRepository.save(request);
    }

    public List<ServiceRequest> getRequestsByResident(Resident resident) {
        return serviceRequestRepository.findByResidentOrderByCreatedAtDesc(resident);
    }

    public List<ServiceRequest> getAllRequests() {
        return serviceRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public ServiceRequest getRequestById(Long id) {
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu với ID: " + id));
    }

    @Transactional
    public ServiceRequest updateStatus(Long id, RequestStatus status, String note) {
        ServiceRequest request = getRequestById(id);
        request.setStatus(status);
        request.setAdminNote(note);
        request.setUpdatedAt(LocalDateTime.now());
        return serviceRequestRepository.save(request);
    }

    @Transactional
    public ServiceRequest assignOfficer(Long id, String officerName) {
        ServiceRequest request = getRequestById(id);
        request.setAssignedOfficer(officerName);
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setUpdatedAt(LocalDateTime.now());
        return serviceRequestRepository.save(request);
    }

    public List<ServiceRequest> getRequestsByStatus(RequestStatus status) {
        return serviceRequestRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public long countPendingRequests() {
        return serviceRequestRepository.countByStatus(RequestStatus.PENDING);
    }

    @Transactional
    public void deleteRequest(Long id) {
        serviceRequestRepository.deleteById(id);
    }
}