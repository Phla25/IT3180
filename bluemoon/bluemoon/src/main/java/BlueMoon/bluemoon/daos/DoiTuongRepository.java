package BlueMoon.bluemoon.daos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.utils.ResidentStatus;
import BlueMoon.bluemoon.utils.UserRole;

@Repository
public interface DoiTuongRepository extends JpaRepository<DoiTuong, String> {

    Optional<DoiTuong> findByEmail(String email);
    
    Optional<DoiTuong> findBySoDienThoai(String soDienThoai);

    // Tìm kiếm Người dùng (có tài khoản)
    List<DoiTuong> findByVaiTroNot(UserRole vaiTro); 
    
    // Tìm kiếm Dân cư (la_cu_dan = TRUE)
    List<DoiTuong> findByLaCuDanTrue();
    
    // Tìm kiếm Dân cư đang ở chung cư
    List<DoiTuong> findByLaCuDanTrueAndTrangThaiDanCu(ResidentStatus trangThaiDanCu);

    // Kiểm tra tồn tại
    boolean existsByEmail(String email);
}