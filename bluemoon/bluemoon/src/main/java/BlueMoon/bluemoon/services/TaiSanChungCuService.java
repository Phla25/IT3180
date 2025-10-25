package BlueMoon.bluemoon.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import BlueMoon.bluemoon.daos.HoGiaDinhDAO;
import BlueMoon.bluemoon.daos.TaiSanChungCuDAO;
import BlueMoon.bluemoon.entities.HoGiaDinh;
import BlueMoon.bluemoon.entities.TaiSanChungCu;
import BlueMoon.bluemoon.utils.AssetType;

@Service
public class TaiSanChungCuService {

    // Inject DAO để tương tác với cơ sở dữ liệu
    @Autowired private TaiSanChungCuDAO taiSanChungCuDAO;
    @Autowired private HoGiaDinhDAO hoGiaDinhDAO; // Cần để tìm HoGiaDinh khi liên kết

    // =======================================================
    // 1. CHỨC NĂNG READ (Đọc)
    // =======================================================
    
    /**
     * Lấy danh sách tất cả các Căn hộ (loaiTaiSan = can_ho).
     * @return List<TaiSanChungCu> là danh sách các căn hộ.
     */
    public List<TaiSanChungCu> getAllApartments() {
        return taiSanChungCuDAO.findAllApartments();
    }

    /**
     * Lấy một Tài Sản Chung Cư (Căn hộ) theo Mã Tài Sản.
     */
    public Optional<TaiSanChungCu> getApartmentById(Integer maTaiSan) {
        return taiSanChungCuDAO.findByID(maTaiSan);
    }
    /**
     * Phân loại căn hộ theo Trạng thái.
     * @param status Trạng thái cần lọc.
     * @return List<TaiSanChungCu>.
     */
    public List<TaiSanChungCu> getApartmentsByStatus(BlueMoon.bluemoon.utils.AssetStatus status) {
        return taiSanChungCuDAO.findApartmentsByStatus(status);
    }
    
    /**
     * Phân loại căn hộ theo dải Diện tích.
     * @param minDienTich Diện tích tối thiểu.
     * @param maxDienTich Diện tích tối đa.
     * @return List<TaiSanChungCu>.
     */
    public List<TaiSanChungCu> getApartmentsByAreaRange(java.math.BigDecimal minDienTich, java.math.BigDecimal maxDienTich) {
        return taiSanChungCuDAO.findApartmentsByAreaRange(minDienTich, maxDienTich);
    }

    /**
     * Phân loại căn hộ theo dải Giá trị.
     * @param minGiaTri Giá trị tối thiểu.
     * @param maxGiaTri Giá trị tối đa.
     * @return List<TaiSanChungCu>.
     */
    public List<TaiSanChungCu> getApartmentsByValueRange(java.math.BigDecimal minGiaTri, java.math.BigDecimal maxGiaTri) {
        return taiSanChungCuDAO.findApartmentsByValueRange(minGiaTri, maxGiaTri);
    }

    /**
     * Lấy danh sách căn hộ có thể lọc theo nhiều tiêu chí (Sử dụng cho Controller).
     * @param status Trạng thái (Optional).
     * @param minArea Diện tích nhỏ nhất (Optional).
     * @param maxArea Diện tích lớn nhất (Optional).
     * @return List<TaiSanChungCu> kết quả.
     */
    public List<TaiSanChungCu> findApartmentsByFilters(BlueMoon.bluemoon.utils.AssetStatus status, java.math.BigDecimal minArea, java.math.BigDecimal maxArea, java.math.BigDecimal minValue, java.math.BigDecimal maxValue) {
        // Đây là cách đơn giản để xử lý lọc: nếu không có tham số nào, trả về tất cả.
        // Đối với logic lọc phức tạp hơn (kết hợp nhiều trường), cần viết JPQL động trong DAO.
        
        // Vì DAO hiện tại chỉ có các hàm lọc đơn, ta sẽ cần thêm logic kết hợp trong DAO hoặc Service, 
        // nhưng tạm thời, ta sẽ chỉ hỗ trợ lọc đơn giản (hoặc kết hợp nếu cả min/max đều có).
        
        if (minArea != null && maxArea != null) {
            return taiSanChungCuDAO.findApartmentsByAreaRange(minArea, maxArea);
        }
        if (minValue != null && maxValue != null) {
            return taiSanChungCuDAO.findApartmentsByValueRange(minValue, maxValue);
        }
        if (status != null) {
            return taiSanChungCuDAO.findApartmentsByStatus(status);
        }
        
        // Nếu không có bộ lọc nào được áp dụng, trả về tất cả căn hộ
        return taiSanChungCuDAO.findAllApartments();
    }
    
    // =======================================================
    // 2. CHỨC NĂNG CREATE (Tạo)
    // =======================================================

    /**
     * Thêm một Căn hộ mới.
     * @param canHoMoi Entity TaiSanChungCu chứa thông tin căn hộ (TenTaiSan, DienTich, GiaTri, TrangThai, ViTri).
     * @param maHo Mã Hộ gia đình liên kết (có thể là null hoặc chuỗi rỗng).
     * @return TaiSanChungCu đã được lưu.
     */
    @Transactional
    public TaiSanChungCu themCanHo(TaiSanChungCu canHoMoi, String maHo) {
        // 1. Kiểm tra và thiết lập Hộ gia đình liên kết
        if (maHo != null && !maHo.trim().isEmpty()) {
            HoGiaDinh hgd = hoGiaDinhDAO.findById(maHo)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hộ gia đình với Mã Hộ: " + maHo));
            
            // Logic nghiệp vụ: Cần đảm bảo hộ này chưa sở hữu căn hộ chính nào khác nếu cần. 
            // Ở đây, ta chỉ kiểm tra Hộ có tồn tại hay không.
            canHoMoi.setHoGiaDinh(hgd);
        } else {
            canHoMoi.setHoGiaDinh(null);
        }

        // 2. Thiết lập loại tài sản mặc định là CĂN_HỘ
        canHoMoi.setLoaiTaiSan(AssetType.can_ho); 
        
        // 3. Lưu
        return taiSanChungCuDAO.save(canHoMoi);
    }

    // =======================================================
    // 3. CHỨC NĂNG UPDATE (Cập nhật)
    // =======================================================
    
    /**
     * Cập nhật thông tin Căn hộ.
     * @param maTaiSan Mã căn hộ cần cập nhật.
     * @param canHoCapNhat Entity TaiSanChungCu chứa dữ liệu mới.
     * @param maHo Mã Hộ gia đình mới liên kết (có thể là null để gỡ liên kết).
     * @return TaiSanChungCu đã được cập nhật.
     */
    @Transactional
    public TaiSanChungCu capNhatCanHo(Integer maTaiSan, TaiSanChungCu canHoCapNhat, String maHo) {
        TaiSanChungCu canHoHienTai = taiSanChungCuDAO.findByID(maTaiSan)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Căn hộ với Mã Tài Sản: " + maTaiSan));
        
        // Đảm bảo không thay đổi loại tài sản
        if (canHoHienTai.getLoaiTaiSan() != AssetType.can_ho) {
             throw new IllegalStateException("Loại tài sản không phải là Căn hộ. Không thể cập nhật qua chức năng này.");
        }

        // 1. Cập nhật thông tin cơ bản
        canHoHienTai.setTenTaiSan(canHoCapNhat.getTenTaiSan());
        canHoHienTai.setTrangThai(canHoCapNhat.getTrangThai());
        canHoHienTai.setDienTich(canHoCapNhat.getDienTich());
        canHoHienTai.setGiaTri(canHoCapNhat.getGiaTri());
        canHoHienTai.setViTri(canHoCapNhat.getViTri());
        
        // 2. Cập nhật Hộ gia đình liên kết
        if (maHo != null && !maHo.trim().isEmpty()) {
            HoGiaDinh hgd = hoGiaDinhDAO.findById(maHo)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hộ gia đình với Mã Hộ: " + maHo));
            canHoHienTai.setHoGiaDinh(hgd);
        } else {
             canHoHienTai.setHoGiaDinh(null); // Gỡ liên kết
        }

        return taiSanChungCuDAO.save(canHoHienTai);
    }

    // =======================================================
    // 4. CHỨC NĂNG DELETE (Xóa)
    // =======================================================
    
    /**
     * Xóa vật lý một Căn hộ khỏi hệ thống.
     * @param maTaiSan Mã căn hộ cần xóa.
     */
    @Transactional
    public void xoaCanHo(Integer maTaiSan) {
        TaiSanChungCu canHo = taiSanChungCuDAO.findByID(maTaiSan)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Căn hộ với Mã Tài Sản: " + maTaiSan));
        
        // Thêm logic kiểm tra ràng buộc nghiệp vụ trước khi xóa (ví dụ: không được xóa nếu có hóa đơn hoặc sự cố đang mở liên quan)
        // ... (Logic kiểm tra) ...
        
        taiSanChungCuDAO.delete(canHo);
    }
}