package BlueMoon.bluemoon.services;

import BlueMoon.bluemoon.models.Apartment;
import BlueMoon.bluemoon.models.Resident;
import BlueMoon.bluemoon.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApartmentService {

    @Autowired
    private ApartmentRepository apartmentRepository;

    public List<Apartment> getApartmentsByResident(Resident resident) {
        System.out.println("🏠 [ApartmentService] Tìm apartments cho resident ID: " + resident.getId());

        List<Apartment> apartments = apartmentRepository.findByOwner(resident);

        if (apartments == null || apartments.isEmpty()) {
            System.out.println("❌ [ApartmentService] Không tìm thấy apartment theo owner");
            // Lấy tất cả apartments để test (vì có thể resident chưa được gán làm owner)
            apartments = apartmentRepository.findAll();

            if (!apartments.isEmpty()) {
                System.out.println(
                        "⚠️ [ApartmentService] Trả về tất cả " + apartments.size() + " apartments có trong DB");
            } else {
                System.out.println("⚠️ [ApartmentService] Database không có apartment nào!");
            }
        } else {
            System.out.println("✅ [ApartmentService] Tìm thấy " + apartments.size() + " apartments của resident");
        }

        return apartments;
    }

    public Apartment save(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    public Apartment findById(Long id) {
        System.out.println("🔍 [ApartmentService] Tìm apartment với ID: " + id);

        return apartmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy căn hộ với ID: " + id));
    }
}