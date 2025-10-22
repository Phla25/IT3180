package BlueMoon.bluemoon.daos;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import BlueMoon.bluemoon.entities.HoGiaDinh;
import BlueMoon.bluemoon.entities.TaiSanChungCu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class TaiSanChungCuDAO {
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;
    
    // =======================================================
    // 1. CÁC PHƯƠNG THỨC CRUD CƠ BẢN (Sử dụng EntityManager)
    // =======================================================

    /**
     * Lưu (hoặc cập nhật) một Entity.
     * @param taiSan Entity cần lưu.
     * @return Entity đã được quản lý (managed).
     */
    @Transactional
    public TaiSanChungCu save(TaiSanChungCu taiSan) {
        return entityManager.merge(taiSan);
    }

    /**
     * Tìm Entity bằng Khóa chính (ma_tai_san).
     */
    public Optional<TaiSanChungCu> findByID(Integer ma_tai_san) {
        TaiSanChungCu taiSan = entityManager.find(TaiSanChungCu.class, ma_tai_san);
        return Optional.ofNullable(taiSan);
    }
    
    /**
     * Tìm tất cả các Entity.
     */
    public List<TaiSanChungCu> findAll() {
        // Sử dụng JPQL để truy vấn tất cả
        return entityManager.createQuery("SELECT ts FROM TaiSanChungCu ts", TaiSanChungCu.class).getResultList();
    }
    
    /**
     * Xóa Entity.
     */
    @Transactional
    public void delete(TaiSanChungCu taiSan) {
        // Cần đảm bảo Entity đang ở trạng thái Managed trước khi remove
        if (entityManager.contains(taiSan)) {
            entityManager.remove(taiSan);
        } else {
            entityManager.remove(entityManager.merge(taiSan));
        }
    }

    // =======================================================
    // 2. CÁC PHƯƠNG THỨC TRUY VẤN TÙY CHỈNH (Sử dụng JPQL)
    // =======================================================

    /**
     * Tìm kiếm tất cả căn hộ
     */
    public List<TaiSanChungCu> findAllApartments() {
        String jpql = "SELECT ts FROM TaiSanChungCu ts WHERE ts.loaiTaiSan = :loai";
        return entityManager.createQuery(jpql, TaiSanChungCu.class)
                            .setParameter("loai", BlueMoon.bluemoon.utils.AssetType.can_ho)
                            .getResultList();
    }

    /**
     * Tìm kiếm tất cả chỗ đỗ xe
     */
    public List<TaiSanChungCu> findAllParkingSpots() {
        String jpql = "SELECT ts FROM TaiSanChungCu ts WHERE ts.tenTaiSan =: ten AND ts.loaiTaiSan = :loai";
        return entityManager.createQuery(jpql, TaiSanChungCu.class)
                            .setParameter("ten", "cho_do_xe")
                            .setParameter("loai", BlueMoon.bluemoon.utils.AssetType.tien_ich)
                            .getResultList();
    }

    /**
     * Sửa thông tin căn hộ
     */
    public void updateApartmentInfo(Integer ma_tai_san, HoGiaDinh hoGiaDinh, String trang_thai, BigDecimal gia_tri) {
        TaiSanChungCu taiSan = entityManager.find(TaiSanChungCu.class, ma_tai_san);
        if (taiSan != null && taiSan.getLoaiTaiSan() == BlueMoon.bluemoon.utils.AssetType.can_ho) {
            taiSan.setHoGiaDinh(hoGiaDinh);
            taiSan.setTrangThai(BlueMoon.bluemoon.utils.AssetStatus.valueOf(trang_thai));
            taiSan.setGiaTri(gia_tri);
            entityManager.merge(taiSan);
        }
    }
    /**
     * Tìm kiếm căn hộ theo hộ gia đình
     */
    public List<TaiSanChungCu> findApartmentsByHousehold(HoGiaDinh hoGiaDinh) {
        String jpql = "SELECT ts FROM TaiSanChungCu ts WHERE ts.loaiTaiSan = :loai AND ts.hoGiaDinh = :hoGiaDinh";
        return entityManager.createQuery(jpql, TaiSanChungCu.class)
                            .setParameter("loai", BlueMoon.bluemoon.utils.AssetType.can_ho)
                            .setParameter("hoGiaDinh", hoGiaDinh)
                            .getResultList();
    }
    
    /**
     * Phân loại căn hộ theo trạng thái
     */
    public List<TaiSanChungCu> findApartmentsByStatus(BlueMoon.bluemoon.utils.AssetStatus status) {
        String jpql = "SELECT ts FROM TaiSanChungCu ts WHERE ts.loaiTaiSan = :loai AND ts.trangThai = :trangThai";
        return entityManager.createQuery(jpql, TaiSanChungCu.class)
                            .setParameter("loai", BlueMoon.bluemoon.utils.AssetType.can_ho)
                            .setParameter("trangThai", status)
                            .getResultList();
    }
    /**
     * Phân loại căn hộ theo dải diện tích
     */
    public List<TaiSanChungCu> findApartmentsByAreaRange(BigDecimal minDienTich, BigDecimal maxDienTich) {
        String jpql = "SELECT ts FROM TaiSanChungCu ts WHERE ts.loaiTaiSan = :loai AND ts.dienTich BETWEEN :minDienTich AND :maxDienTich";
        return entityManager.createQuery(jpql, TaiSanChungCu.class)
                            .setParameter("loai", BlueMoon.bluemoon.utils.AssetType.can_ho)
                            .setParameter("minDienTich", minDienTich)
                            .setParameter("maxDienTich", maxDienTich)
                            .getResultList();
    }
    /**
     * Phân loại căn hộ theo dải giá trị
     */
    public List<TaiSanChungCu> findApartmentsByValueRange(BigDecimal minGiaTri, BigDecimal maxGiaTri) {
        String jpql = "SELECT ts FROM TaiSanChungCu ts WHERE ts.loaiTaiSan = :loai AND ts.giaTri BETWEEN :minGiaTri AND :maxGiaTri";
        return entityManager.createQuery(jpql, TaiSanChungCu.class)
                            .setParameter("loai", BlueMoon.bluemoon.utils.AssetType.can_ho)
                            .setParameter("minGiaTri", minGiaTri)
                            .setParameter("maxGiaTri", maxGiaTri)
                            .getResultList();
    }
    // Giả định bạn đã sửa lại TaiSanChungCuDAO.java để bao gồm phương thức sau:

    /**
     * Tìm căn hộ chính (loaiTaiSan = CAN_HỌ) của một hộ gia đình.
     */
    public Optional<TaiSanChungCu> findApartmentByHo(String maHo) {
        String jpql = "SELECT ts FROM TaiSanChungCu ts WHERE ts.hoGiaDinh.maHo = :maHo AND ts.loaiTaiSan = :loaiCanHo";
        try {
            return Optional.of(entityManager.createQuery(jpql, TaiSanChungCu.class)
                                            .setParameter("maHo", maHo)
                                            .setParameter("loaiCanHo", BlueMoon.bluemoon.utils.AssetType.can_ho)
                                            .setMaxResults(1) // Lấy căn hộ đầu tiên (giả định 1 hộ sở hữu 1 căn chính)
                                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}

