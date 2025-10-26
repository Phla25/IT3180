package BlueMoon.bluemoon.daos;

import BlueMoon.bluemoon.entities.PhanHoiThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhanHoiThongBaoDAO extends JpaRepository<PhanHoiThongBao, Integer> {

    /**
     * Lấy tất cả phản hồi của một thông báo, sắp xếp theo thời gian gửi tăng dần.
     * @param maThongBao Mã thông báo cần lấy phản hồi.
     * @return Danh sách phản hồi.
     */
    List<PhanHoiThongBao> findByThongBaoMaThongBaoOrderByThoiGianGuiAsc(Integer maThongBao);
}