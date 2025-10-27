package BlueMoon.bluemoon.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import BlueMoon.bluemoon.daos.HoaDonDAO;
import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.entities.HoGiaDinh;
import BlueMoon.bluemoon.entities.HoaDon;
import BlueMoon.bluemoon.models.HoaDonStatsDTO;
import BlueMoon.bluemoon.utils.InvoiceStatus;
import jakarta.transaction.Transactional;

@Service
public class HoaDonService {

    @Autowired private HoaDonDAO hoaDonDAO;
    @Autowired private HoGiaDinhService hoGiaDinhService;
    @Autowired private ThanhVienHoService thanhVienHoService;

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
    /**
     * Lấy tất cả hóa đơn của một hộ gia đình.
     */
    public List<HoaDon> getAllHoaDonByHo(HoGiaDinh hoGiaDinh) {
        if (hoGiaDinh == null) {
            return Collections.emptyList();
        }
        // Dùng DAO có sẵn
        return hoaDonDAO.findByHoGiaDinh(hoGiaDinh);
    }
    
    /**
     * Lấy một hóa đơn theo ID và Hộ gia đình (đảm bảo bảo mật).
     */
    public Optional<HoaDon> getHoaDonByIdAndHo(Integer maHoaDon, HoGiaDinh hoGiaDinh) {
        // Cần thêm hàm này vào HoaDonDAO nếu chưa có
        // Tạm thời lấy tất cả hóa đơn của hộ và lọc (Kém hiệu quả, nên dùng DAO)
        return hoaDonDAO.findByHoGiaDinh(hoGiaDinh).stream()
            .filter(hd -> hd.getMaHoaDon().equals(maHoaDon))
            .findFirst();
    }

    /**
     * CẬP NHẬT: Mock chức năng thanh toán của Cư Dân.
     * Lưu thông tin người thực hiện thanh toán vào trường MỚI.
     */
    @Transactional 
    public void markAsPaidByResident(Integer maHoaDon, DoiTuong nguoiThanhToan) {
        // ... (Tìm HoaDon) ...
        HoaDon hd = hoaDonDAO.findAllWithHoGiaDinh().stream()
            .filter(h -> h.getMaHoaDon().equals(maHoaDon))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn."));
        
        if (hd.getTrangThai() == InvoiceStatus.da_thanh_toan) {
            throw new IllegalArgumentException("Hóa đơn #" + maHoaDon + " đã được thanh toán.");
        }
        if (hd.getTrangThai() == InvoiceStatus.cho_xac_nhan) {
            throw new IllegalArgumentException("Hóa đơn #" + maHoaDon + " đang chờ Kế toán xác nhận. Không thể thanh toán lại.");
        }
        
        hd.setTrangThai(InvoiceStatus.cho_xac_nhan);
        // ✨ LƯU NGƯỜI THANH TOÁN VÀO TRƯỜNG MỚI
        hd.setNguoiThanhToan(nguoiThanhToan); 
        // Giữ nguyên nguoiThucHienGiaoDich (người tạo ra hóa đơn)
    }
    /**
     * CẬP NHẬT: Lưu hoặc Cập nhật Hóa đơn (Chức năng CRUD) bởi Admin/Kế toán.
     * * Quy tắc logic mới cho nguoiDangKyDichVu:
     * 1. TẠO MỚI (bởi Admin/Kế toán): nguoiDangKyDichVu = Chủ hộ.
     * 2. CẬP NHẬT: Giữ lại nguoiDangKyDichVu cũ (có thể là Chủ hộ hoặc người đăng ký dịch vụ).
     * 3. HỆ THỐNG TỰ SINH (Đăng ký DV): logic này nằm trong DangKyDichVuService, không bị ảnh hưởng.
     * * @param hoaDon Đối tượng HoaDon được bind từ form.
     * @param maHo Mã Hộ gia đình được chọn từ form.
     * @param nguoiThucHien Đối tượng Admin/Kế toán đang thực hiện thao tác.
     * @return HoaDon đã được lưu.
     */
    @Transactional
    public HoaDon saveOrUpdateHoaDon(HoaDon hoaDon, String maHo, DoiTuong nguoiThucHien) {
    
        final boolean isNewInvoice = hoaDon.getMaHoaDon() == null;
    
        // 1. Tải lại/Thiết lập HoGiaDinh từ maHo
        HoGiaDinh hoGiaDinh = hoGiaDinhService.getHouseholdById(maHo)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hộ gia đình với Mã Hộ: " + maHo));
        hoaDon.setHoGiaDinh(hoGiaDinh);

        // 2. Xử lý Logic Cập nhật (Nếu không phải tạo mới)
        if (!isNewInvoice) {
            HoaDon hdOriginal = hoaDonDAO.findAllWithHoGiaDinh().stream()
                .filter(h -> h.getMaHoaDon().equals(hoaDon.getMaHoaDon()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hóa đơn cần cập nhật."));
            
            // [Logic kiểm tra trạng thái thanh toán giữ nguyên]
            if (hdOriginal.getTrangThai() == InvoiceStatus.da_thanh_toan && hoaDon.getTrangThai() != InvoiceStatus.da_thanh_toan) {
                 throw new IllegalArgumentException("Không thể chuyển Hóa đơn đã thanh toán về trạng thái chưa thanh toán.");
            }
        
            // Giữ lại người tạo/người gốc hóa đơn ban đầu (Quan trọng để giữ người đăng ký dịch vụ)
            if (hoaDon.getNguoiDangKyDichVu() == null) {
                hoaDon.setNguoiDangKyDichVu(hdOriginal.getNguoiDangKyDichVu());
            }
        
            // Giữ lại người thanh toán/xác nhận ban đầu
            if (hoaDon.getNguoiThanhToan() == null) {
                hoaDon.setNguoiThanhToan(hdOriginal.getNguoiThanhToan());
            }
        }
    
        // 3. Thiết lập Người Tạo/Người Gốc & Trạng Thái Ban Đầu (Quan trọng cho Create)
        if (isNewInvoice) {
            
        // ✨ QUY TẮC MỚI: TÌM CHỦ HỘ VÀ SET LÀM NGƯỜI ĐĂNG KÝ DỊCH VỤ/NGƯỜI GỐC
            Optional<DoiTuong> chuHoOpt = thanhVienHoService.getChuHoByMaHo(maHo);
            DoiTuong chuHo = chuHoOpt
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Chủ hộ cho Hộ gia đình này. Không thể tạo hóa đơn."));
            
            // Set người gốc hóa đơn là Chủ hộ
            hoaDon.setNguoiDangKyDichVu(chuHo); 
        
            // Thiết lập ngày tạo và trạng thái mặc định (giữ nguyên)
            if (hoaDon.getNgayTao() == null) {
                hoaDon.setNgayTao(LocalDateTime.now());
            }
            if (hoaDon.getTrangThai() == null) {
                hoaDon.setTrangThai(InvoiceStatus.chua_thanh_toan);
            }
        }
    
        // 4. Logic Xử lý Trạng Thái "ĐÃ THANH TOÁN" (Áp dụng cho cả Create/Update)
        // [Logic này giữ nguyên]
        if (hoaDon.getTrangThai() == InvoiceStatus.da_thanh_toan && hoaDon.getNgayThanhToan() == null) {
            hoaDon.setNgayThanhToan(LocalDateTime.now());
        
            if (hoaDon.getNguoiThanhToan() == null) {
                 hoaDon.setNguoiThanhToan(nguoiThucHien); // Admin/Kế toán là người xác nhận
            }
        }   
    
        // 5. Lưu Hóa đơn
        return hoaDonDAO.save(hoaDon);
    }
    /**
     * Lấy danh sách hóa đơn đang ở trạng thái chờ xác nhận.
     */
    public List<HoaDon> getHoaDonChoXacNhan(int limit) {
        List<HoaDon> list = hoaDonDAO.findByTrangThaiWithChuHo(InvoiceStatus.cho_xac_nhan);
        // Sắp xếp theo ngày tạo (mới nhất lên đầu)
        list.sort(Comparator.comparing(HoaDon::getNgayTao, Comparator.reverseOrder()));
        return list.size() > limit ? list.subList(0, limit) : list;
    }
    /**
     * Lấy tất cả hóa đơn cho Kế Toán/Admin.
     */
    public List<HoaDon> getAllHoaDon() {
        return hoaDonDAO.findAllWithHoGiaDinh(); // Dùng hàm có FETCH JOIN
    }
    
    /**
     * Hàm lấy Hóa đơn theo ID (cho Admin/Accountant)
     */
    public Optional<HoaDon> getHoaDonById(Integer maHoaDon) {
         return hoaDonDAO.findAllWithHoGiaDinh().stream()
            .filter(h -> h.getMaHoaDon().equals(maHoaDon))
            .findFirst();
    }
    /**
     * Xóa Hóa đơn.
     */
    @Transactional
    public void deleteHoaDon(Integer maHoaDon) {
        // Cần thêm hàm findById vào HoaDonDAO nếu chưa có
        HoaDon hd = hoaDonDAO.findAllWithHoGiaDinh().stream()
                             .filter(h -> h.getMaHoaDon().equals(maHoaDon))
                             .findFirst()
                             .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn cần xóa."));
        // Cần thêm hàm delete vào HoaDonDAO
        hoaDonDAO.delete(hd); 
    }

    /**
     * Kế Toán Xác nhận Thanh toán Hóa đơn.
     */
    @Transactional
    public void confirmPayment(Integer maHoaDon, DoiTuong nguoiXacNhan) {
        HoaDon hd = hoaDonDAO.findAllWithHoGiaDinh().stream()
            .filter(h -> h.getMaHoaDon().equals(maHoaDon))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn."));
        
        if (hd.getTrangThai() == InvoiceStatus.da_thanh_toan) {
            throw new IllegalArgumentException("Hóa đơn đã được thanh toán trước đó.");
        }
        
        // Chuyển trạng thái sang đã thanh toán
        hd.setTrangThai(InvoiceStatus.da_thanh_toan);
        hd.setNgayThanhToan(LocalDateTime.now());
    }
    
    /**
     * Lấy tất cả hóa đơn đã thanh toán (Lịch sử giao dịch).
     */
    public List<HoaDon> getAllPaidHoaDon() {
        return hoaDonDAO.findByTrangThai(InvoiceStatus.da_thanh_toan);
    }
    /**
     * Kế Toán Từ Chối Xác nhận Thanh toán Hóa đơn.
     * Chuyển trạng thái từ CHỜ XÁC NHẬN về CHƯA THANH TOÁN.
     */
    @Transactional
    public void rejectPayment(Integer maHoaDon, DoiTuong nguoiThucHien) {
        HoaDon hd = hoaDonDAO.findAllWithHoGiaDinh().stream()
            .filter(h -> h.getMaHoaDon().equals(maHoaDon))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn."));
        
        // Chỉ từ chối khi hóa đơn ở trạng thái CHỜ XÁC NHẬN
        if (hd.getTrangThai() != InvoiceStatus.cho_xac_nhan) {
            throw new IllegalArgumentException("Chỉ có thể từ chối hóa đơn đang ở trạng thái Chờ Xác Nhận. Trạng thái hiện tại: " + hd.getTrangThai().getDbValue());
        }
        
        // Chuyển trạng thái về chưa thanh toán
        hd.setTrangThai(InvoiceStatus.chua_thanh_toan);
        hd.setNgayThanhToan(null);
        hd.setNguoiThanhToan(null); // Xóa thông tin người đã thanh toán (cư dân)
        
        hoaDonDAO.save(hd);
    }
}