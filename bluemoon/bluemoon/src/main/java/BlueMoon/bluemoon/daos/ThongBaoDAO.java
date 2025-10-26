package BlueMoon.bluemoon.daos;

import BlueMoon.bluemoon.entities.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoDAO extends JpaRepository<ThongBao, Integer> {

    /**
     * Lấy tất cả thông báo, sắp xếp theo thời gian gửi giảm dần (mới nhất trước).
     * Phục vụ cho cả Admin (lịch sử gửi) và Cư dân (danh sách thông báo nhận).
     */
    List<ThongBao> findAllByOrderByThoiGianGuiDesc();
    
}