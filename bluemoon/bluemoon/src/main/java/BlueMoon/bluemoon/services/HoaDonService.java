package BlueMoon.bluemoon.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Lấy thông tin thống kê hóa đơn chính cho dashboard cư dân.
     */
    public HoaDonStatsDTO getHoaDonStats(HoGiaDinh hoGiaDinh) {
        if (hoGiaDinh == null) {
            return new HoaDonStatsDTO();
        }
        
        // Cần cập nhật DAO để lọc theo Hộ nếu cần, tạm thời vẫn dùng DAO cũ
        BigDecimal tongChuaThanhToan = hoaDonDAO.sumSoTienByTrangThai(InvoiceStatus.chua_thanh_toan); 
        Long soLuongChuaThanhToan = hoaDonDAO.countByTrangThai(InvoiceStatus.chua_thanh_toan); 
        
        HoaDonStatsDTO stats = new HoaDonStatsDTO();
        stats.tongChuaThanhToan = tongChuaThanhToan;
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
    
    /**
     * Lấy danh sách hóa đơn cần Kế toán xử lý/xác nhận.
     */
    public List<HoaDon> getHoaDonCanXacNhan(InvoiceStatus trangThai, int limit) {
        // Gọi phương thức DAO có JOIN phức hợp và sắp xếp
        List<HoaDon> list = hoaDonDAO.findByTrangThaiWithChuHo(trangThai);
        
        // Giới hạn số lượng ở tầng Service
        return list.size() > limit ? list.subList(0, limit) : list;
    }
        
    /**
     * Tính toán các số liệu tài chính quan trọng cho Dashboard Kế Toán.
     */
    public HoaDonStatsDTO getAccountantStats() {   
        // 1. TÍNH TỔNG THU (Total Revenue)
        BigDecimal tongThuThangNay = hoaDonDAO.sumSoTienByTrangThai(InvoiceStatus.da_thanh_toan);
        
        // 2. DỮ LIỆU CÒN LẠI (Chưa Thu, Quá Hạn)
        BigDecimal tongChuaThu = hoaDonDAO.sumSoTienByTrangThai(InvoiceStatus.chua_thanh_toan);
        Long soHoaDonChuaThu = hoaDonDAO.countByTrangThai(InvoiceStatus.chua_thanh_toan);
        
        // Quá Hạn
        List<HoaDon> allPendingInvoices = hoaDonDAO.findByTrangThai(InvoiceStatus.chua_thanh_toan);
        LocalDate today = LocalDate.now();
        List<HoaDon> overdueInvoices = allPendingInvoices.stream()
            .filter(hd -> hd.getHanThanhToan() != null && hd.getHanThanhToan().isBefore(today))
            .collect(Collectors.toList());
            
        BigDecimal tongQuaHan = overdueInvoices.stream()
            .map(HoaDon::getSoTien)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        Long soHoaDonQuaHan = (long) overdueInvoices.size();
        
        // 3. Đóng gói kết quả
        HoaDonStatsDTO stats = new HoaDonStatsDTO();
        stats.tongThuThangNay = tongThuThangNay;
        stats.tongChuaThanhToan = tongChuaThu; 
        stats.tongHoaDonChuaThanhToan = soHoaDonChuaThu.intValue(); 
        
        stats.tongQuaHan = tongQuaHan;
        stats.soHoaDonQuaHan = soHoaDonQuaHan.intValue();
        stats.phanTramTangTruong = 15.0; // Vẫn mock 15% 
        
        return stats;
    }
}