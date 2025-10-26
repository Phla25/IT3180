package BlueMoon.bluemoon.services;

import BlueMoon.bluemoon.models.Apartment;
import BlueMoon.bluemoon.models.Resident;
import BlueMoon.bluemoon.repository.ApartmentRepository;
import BlueMoon.bluemoon.entities.TaiSanChungCu;
import BlueMoon.bluemoon.utils.AssetType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentService {

    @Autowired
    private ApartmentRepository apartmentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Apartment> getApartmentsByResident(Resident resident) {
        System.out.println("🏠 [ApartmentService] Tìm apartments cho resident: " + resident.getUsername());

        // Lấy username (chính là CCCD) của resident
        String cccd = resident.getUsername();

        // Tìm tất cả các hộ gia đình mà cư dân này là thành viên (và đang hoạt động)
        String jpql = "SELECT tvh.hoGiaDinh.maHo FROM ThanhVienHo tvh " +
                "WHERE tvh.doiTuong.cccd = :cccd AND tvh.ngayKetThuc IS NULL";

        List<String> maHoList = entityManager.createQuery(jpql, String.class)
                .setParameter("cccd", cccd)
                .getResultList();

        System.out.println("📋 [ApartmentService] Tìm thấy " + maHoList.size() + " hộ gia đình cho CCCD: " + cccd);

        if (maHoList.isEmpty()) {
            System.out.println("⚠️ [ApartmentService] Không tìm thấy hộ nào, thử tìm theo owner trong bảng Apartment");
            // Fallback: tìm theo owner trong bảng Apartment (cấu trúc mới)
            List<Apartment> apartments = apartmentRepository.findByOwner(resident);
            if (apartments.isEmpty()) {
                System.out.println("❌ [ApartmentService] Không tìm thấy apartment nào");
            }
            return apartments;
        }

        // Tìm tất cả căn hộ (TaiSanChungCu) thuộc các hộ này
        String jpqlApartments = "SELECT ts FROM TaiSanChungCu ts " +
                "WHERE ts.hoGiaDinh.maHo IN :maHoList " +
                "AND ts.loaiTaiSan = :loaiCanHo";

        List<TaiSanChungCu> taiSanList = entityManager.createQuery(jpqlApartments, TaiSanChungCu.class)
                .setParameter("maHoList", maHoList)
                .setParameter("loaiCanHo", AssetType.can_ho)
                .getResultList();

        System.out.println("✅ [ApartmentService] Tìm thấy " + taiSanList.size() + " căn hộ từ TaiSanChungCu");

        // Chuyển đổi TaiSanChungCu sang Apartment
        List<Apartment> apartments = taiSanList.stream()
                .map(ts -> convertToApartment(ts, resident))
                .collect(Collectors.toList());

        return apartments;
    }

    /**
     * Chuyển đổi TaiSanChungCu sang Apartment
     */
    private Apartment convertToApartment(TaiSanChungCu taiSan, Resident resident) {
        // Tìm xem đã có Apartment tương ứng trong DB chưa
        String tenCanHo = taiSan.getTenTaiSan();
        List<Apartment> existing = entityManager.createQuery(
                "SELECT a FROM Apartment a WHERE a.apartmentNumber = :number", Apartment.class)
                .setParameter("number", tenCanHo)
                .getResultList();

        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        // Nếu chưa có, tạo mới
        Apartment apartment = new Apartment();
        apartment.setApartmentNumber(tenCanHo);

        // Parse floor từ tên căn hộ (ví dụ: "101" -> tầng 1, "205" -> tầng 2)
        try {
            String firstDigit = tenCanHo.replaceAll("[^0-9]", "").substring(0, 1);
            apartment.setFloor(Integer.parseInt(firstDigit));
        } catch (Exception e) {
            apartment.setFloor(1); // Mặc định tầng 1
        }

        apartment.setArea(taiSan.getDienTich() != null ? taiSan.getDienTich().doubleValue() : 0.0);
        apartment.setNumberOfRooms(3); // Mặc định 3 phòng
        apartment.setOwner(resident);
        apartment.setStatus(taiSan.getTrangThai() != null ? taiSan.getTrangThai().name() : "hoat_dong");

        // Lưu vào DB để có ID
        apartment = apartmentRepository.save(apartment);

        System.out.println("💾 [ApartmentService] Đã tạo Apartment mới: " + apartment.getApartmentNumber() + " với ID: "
                + apartment.getId());

        return apartment;
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