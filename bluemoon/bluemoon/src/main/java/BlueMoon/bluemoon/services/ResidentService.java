package BlueMoon.bluemoon.services;

import BlueMoon.bluemoon.models.Resident;
import BlueMoon.bluemoon.repository.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ResidentService {

    @Autowired
    private ResidentRepository residentRepository;

    public Resident findByUsername(String username) {
        System.out.println("🔍 [ResidentService] Tìm resident với username: " + username);

        Optional<Resident> result = residentRepository.findByUsername(username);

        if (result.isEmpty()) {
            System.out.println("❌ [ResidentService] KHÔNG tìm thấy resident với username: " + username);

            try {
                // Tạo và SAVE resident mới vào database
                Resident newResident = new Resident();
                newResident.setFullName("Cư Dân Mới - " + username);
                newResident.setUsername(username);
                newResident.setPhoneNumber("0123456789");
                newResident.setEmail(username + "@bluemoon.com");
                newResident.setPassword("temp_password");

                // SAVE vào database
                Resident savedResident = residentRepository.save(newResident);
                System.out.println("✅ [ResidentService] Đã tạo resident mới với ID: " + savedResident.getId());
                return savedResident;

            } catch (Exception e) {
                System.err.println(
                        "⚠️ [ResidentService] Lỗi khi tạo resident mới (có thể đã tồn tại): " + e.getMessage());

                // Thử tìm lại một lần nữa (có thể đã được tạo bởi thread khác)
                Optional<Resident> retry = residentRepository.findByUsername(username);
                if (retry.isPresent()) {
                    System.out.println(
                            "✅ [ResidentService] Tìm thấy resident sau khi retry: " + retry.get().getFullName());
                    return retry.get();
                }

                // Nếu vẫn không tìm thấy, throw exception
                throw new RuntimeException("Không thể tạo hoặc tìm thấy resident với username: " + username, e);
            }
        }

        System.out.println("✅ [ResidentService] Tìm thấy resident: " + result.get().getFullName());
        return result.get();
    }

    public Resident save(Resident resident) {
        return residentRepository.save(resident);
    }

    public Resident findById(Long id) {
        System.out.println("🔍 [ResidentService] Tìm resident với ID: " + id);

        return residentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cư dân với ID: " + id));
    }
}