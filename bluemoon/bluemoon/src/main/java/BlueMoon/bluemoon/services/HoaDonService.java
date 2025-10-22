package BlueMoon.bluemoon.services;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import BlueMoon.bluemoon.daos.HoaDonDAO;
import BlueMoon.bluemoon.entities.HoGiaDinh;
import BlueMoon.bluemoon.entities.HoaDon;
import BlueMoon.bluemoon.models.HoaDonStatsDTO;
import BlueMoon.bluemoon.utils.InvoiceStatus;

@Service
public class HoaDonService {

    @Autowired private HoaDonDAO hoaDonDAO;
    // @Autowired private ThanhVienHoService thanhVienHoService; // Cần thiết để lấy HoGiaDinh

    /**
     * Lấy thông tin thống kê hóa đơn chính cho dashboard cư dân.
     * CẦN ĐỐI TƯỢNG HoGiaDinh HOẶC MÃ HỘ. Giả định Service đã nhận được HoGiaDinh.
     */
    public HoaDonStatsDTO getHoaDonStats(HoGiaDinh hoGiaDinh) {
        if (hoGiaDinh == null) {
            return new HoaDonStatsDTO();
        }
        
        // 1. Tính tổng tiền chưa thanh toán
        // Giả định: Hóa đơn DAO có thể tìm theo Hộ VÀ Trạng thái
        BigDecimal tongChuaThanhToan = hoaDonDAO.sumSoTienByTrangThai(InvoiceStatus.chua_thanh_toan); // Cần DAO lọc theo Hộ
        
        // 2. Đếm số lượng hóa đơn chưa thanh toán
        Long soLuongChuaThanhToan = hoaDonDAO.countByTrangThai(InvoiceStatus.chua_thanh_toan); // Cần DAO lọc theo Hộ
        
        HoaDonStatsDTO stats = new HoaDonStatsDTO();
        stats.soTienChuaThanhToan = tongChuaThanhToan;
        stats.tongHoaDonChuaThanhToan = soLuongChuaThanhToan.intValue();

        if (soLuongChuaThanhToan > 0) {
            stats.trangThai = soLuongChuaThanhToan + " chưa thanh toán";
        } else {
             stats.trangThai = "Tất cả đã thanh toán";
        }
        return stats;
    }
    
    /**
     * Lấy danh sách hóa đơn gần đây nhất của một hộ gia đình.
     */
    public List<HoaDon> getRecentHoaDon(HoGiaDinh hoGiaDinh, int limit) {
        if (hoGiaDinh == null) {
            return Collections.emptyList();
        }
        
        List<HoaDon> list = hoaDonDAO.findByHoGiaDinh(hoGiaDinh);
        
        // Sắp xếp theo ngày tạo (mới nhất lên đầu)
        list.sort(Comparator.comparing(HoaDon::getNgayTao, Comparator.reverseOrder()));
        
        return list.size() > limit ? list.subList(0, limit) : list;
    }
}