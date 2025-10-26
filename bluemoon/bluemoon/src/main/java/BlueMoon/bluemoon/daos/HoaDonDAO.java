package BlueMoon.bluemoon.daos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import BlueMoon.bluemoon.entities.HoGiaDinh;
import BlueMoon.bluemoon.entities.HoaDon;
import BlueMoon.bluemoon.utils.InvoiceStatus;
import BlueMoon.bluemoon.utils.InvoiceType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

@Repository
public class HoaDonDAO {
    @Autowired
    private EntityManager entityManager;

    public Long countAll() {
        String jpql = "SELECT COUNT(hd) FROM HoaDon hd";
        Long count = entityManager.createQuery(jpql, Long.class).getSingleResult();
        return count != null ? count : 0;
    }
    public Long countByTrangThai(InvoiceStatus trangThai) {
        String jpql = "SELECT COUNT(hd) FROM HoaDon hd WHERE hd.trangThai = :trangThai";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("trangThai", trangThai)
                .getSingleResult();
        return count != null ? count : 0;
    }
    public List<HoaDon> findAll() {
        String jpql = "SELECT hd FROM HoaDon hd";
        return entityManager.createQuery(jpql, HoaDon.class).getResultList();
    }
    public List<HoaDon> findByTrangThai(InvoiceStatus trangThai) {
        String jpql = "SELECT hd FROM HoaDon hd WHERE hd.trangThai = :trangThai";
        return entityManager.createQuery(jpql, HoaDon.class)
                .setParameter("trangThai", trangThai)
                .getResultList();
    }
    public List<HoaDon> findByHoGiaDinh(HoGiaDinh HoGiaDinh) 
    {
        String jpql = "SELECT hd FROM HoaDon hd WHERE hd.hoGiaDinh = :hoGiaDinh";
        return entityManager.createQuery(jpql, HoaDon.class)
                .setParameter("hoGiaDinh", HoGiaDinh)
                .getResultList();
    }
    public List<HoaDon> findByHoGiaDinhMaHo(String maHo) 
    {
            // Tối ưu hóa: Lọc bằng ID (maHo)
            // JOIN FETCH hd.hoGiaDinh h để tránh N+1 query khi hiển thị
            String jpql = "SELECT hd FROM HoaDon hd JOIN FETCH hd.hoGiaDinh h WHERE h.maHo = :maHo ORDER BY hd.ngayTao DESC";
            return entityManager.createQuery(jpql, HoaDon.class)
                    .setParameter("maHo", maHo)
                    .getResultList();
    }
    public Optional<HoaDon> findById(Integer maHoaDon) {
        // Sử dụng Integer cho maHoaDon
        String jpql = "SELECT hd FROM HoaDon hd "
                    + "JOIN FETCH hd.hoGiaDinh h " 
                    // Tải người thực hiện giao dịch (cccd_thanh_vien)
                    + "LEFT JOIN FETCH hd.nguoiThucHienGiaoDich ntdg " 
                    // Tải người thanh toán/xác nhận (cccd_nguoi_thanh_toan)
                    + "LEFT JOIN FETCH hd.nguoiThanhToanHoacXacNhan nttx "
                    + "WHERE hd.maHoaDon = :maHoaDon";
        try {
             HoaDon hoaDon = entityManager.createQuery(jpql, HoaDon.class)
                .setParameter("maHoaDon", maHoaDon)
                .getSingleResult();
             return Optional.of(hoaDon);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    public BigDecimal sumSoTienByTrangThai(InvoiceStatus trangThai) {
        String jpql = "SELECT SUM(hd.soTien) FROM HoaDon hd WHERE hd.trangThai = :trangThai";
        BigDecimal sum = entityManager.createQuery(jpql, BigDecimal.class)
                .setParameter("trangThai", trangThai)
                .getSingleResult();
        return sum != null ? sum : BigDecimal.ZERO;
    }
    public List<HoaDon> findAllWithHoGiaDinh() {
        // Thêm FETCH JOIN để tránh vấn đề N+1 khi truy cập hoGiaDinh trong Thymeleaf
        String jpql = "SELECT hd FROM HoaDon hd JOIN FETCH hd.hoGiaDinh h";
        return entityManager.createQuery(jpql, HoaDon.class).getResultList();
    }
    
    // PHƯƠNG THỨC MỚI ĐỂ GIẢI QUYẾT LỖI BIÊN DỊCH
    public List<HoaDon> findByTrangThaiOrderByNgayTao(InvoiceStatus trangThai) {
        // Tạm thời sử dụng truy vấn đơn giản nhất
        String jpql = "SELECT hd FROM HoaDon hd JOIN FETCH hd.hoGiaDinh h WHERE hd.trangThai = :trangThai ORDER BY hd.ngayTao DESC";
        return entityManager.createQuery(jpql, HoaDon.class)
            .setParameter("trangThai", trangThai)
            .getResultList();
    }
    
    // PHƯƠNG THỨC NÀY ĐƯỢC SỬ DỤNG KHI CẦN HIỂN THỊ TÊN CHỦ HỘ
    @SuppressWarnings("unchecked")
    public List<HoaDon> findByTrangThaiWithChuHo(InvoiceStatus trangThai) {
        
        // JPQL sử dụng JOIN PHỨC HỢP để tải Chủ Hộ
        String jpql = "SELECT DISTINCT hd FROM HoaDon hd "
                + "JOIN FETCH hd.hoGiaDinh h "
                + "JOIN h.thanhVienHoList tvh " // Tên trường @OneToMany trong HoGiaDinh.java
                + "JOIN tvh.doiTuong dt " // Tên trường trong ThanhVienHo.java
                + "WHERE tvh.laChuHo = TRUE AND hd.trangThai = :trangThai "
                + "ORDER BY hd.ngayTao DESC";

        Query query = entityManager.createQuery(jpql, HoaDon.class)
            .setParameter("trangThai", trangThai);
        
        return query.getResultList();
    }
    public BigDecimal sumSoTienByLoaiAndTrangThai(InvoiceType loaiHoaDon, InvoiceStatus trangThai) {
        String jpql = "SELECT SUM(hd.soTien) FROM HoaDon hd WHERE hd.loaiHoaDon = :loaiHoaDon AND hd.trangThai = :trangThai";
        BigDecimal sum = entityManager.createQuery(jpql, BigDecimal.class)
                .setParameter("loaiHoaDon", loaiHoaDon)
                .setParameter("trangThai", trangThai)
                .getSingleResult();
        return sum != null ? sum : BigDecimal.ZERO;
    }

    public HoaDon save(HoaDon hoaDon) {
        return entityManager.merge(hoaDon);
    }

    public void delete(HoaDon hd) {
        entityManager.remove(hd);
    }
}