package BlueMoon.bluemoon.daos;

import BlueMoon.bluemoon.entities.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoDAO extends JpaRepository<ThongBao, Integer> {
    List<ThongBao> findAllByOrderByThoiGianGuiDesc();
 // Dùng JPQL để JOIN FETCH nguoiGui (DoiTuong)
    @Query("SELECT t FROM ThongBao t JOIN FETCH t.nguoiGui ORDER BY t.thoiGianGui DESC")
    List<ThongBao> findAllWithNguoiGuiEagerly();
}