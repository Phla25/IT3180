package BlueMoon.bluemoon.daos;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.utils.ResidentStatus;
import BlueMoon.bluemoon.utils.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

// Sử dụng @Repository để cho Spring quản lý DAO này
@Repository
public class DoiTuongDAO {

    // Tiêm EntityManager để truy vấn thủ công
    @PersistenceContext
    private EntityManager entityManager;

    // =======================================================
    // 1. CÁC PHƯƠNG THỨC CRUD CƠ BẢN (Sử dụng EntityManager)
    // =======================================================

    /**
     * Lưu (hoặc cập nhật) một Entity.
     * @param doiTuong Entity cần lưu.
     * @return Entity đã được quản lý (managed).
     */
    @Transactional
    public DoiTuong save(DoiTuong doiTuong) {
        return entityManager.merge(doiTuong);
    }

    /**
     * Tìm Entity bằng Khóa chính (CCCD).
     */
    public Optional<DoiTuong> findByCccd(String cccd) {
        DoiTuong doiTuong = entityManager.find(DoiTuong.class, cccd);
        return Optional.ofNullable(doiTuong);
    }
    
    /**
     * Tìm tất cả các Entity.
     */
    public List<DoiTuong> findAll() {
        // Sử dụng JPQL để truy vấn tất cả
        return entityManager.createQuery("SELECT d FROM DoiTuong d", DoiTuong.class).getResultList();
    }
    
    /**
     * Xóa Entity.
     */
    @Transactional
    public void delete(DoiTuong doiTuong) {
        // Cần đảm bảo Entity đang ở trạng thái Managed trước khi remove
        if (entityManager.contains(doiTuong)) {
            entityManager.remove(doiTuong);
        } else {
            entityManager.remove(entityManager.merge(doiTuong));
        }
    }

    // =======================================================
    // 2. CÁC PHƯƠNG THỨC TRUY VẤN TÙY CHỈNH (Sử dụng JPQL)
    // =======================================================

    /**
     * Tìm Entity bằng Email.
     */
    public Optional<DoiTuong> findByEmail(String email) {
        String jpql = "SELECT d FROM DoiTuong d WHERE d.email = :email";
        try {
            return Optional.of(
                entityManager.createQuery(jpql, DoiTuong.class)
                    .setParameter("email", email)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Tìm tất cả người dùng với vai trò cụ thể.
     */
    public List<DoiTuong> findByVaiTro(UserRole vaiTro) {
        String jpql = "SELECT d FROM DoiTuong d WHERE d.vaiTro = :vaiTro";
        return entityManager.createQuery(jpql, DoiTuong.class)
            .setParameter("vaiTro", vaiTro)
            .getResultList();
    }

    /**
     * Tìm Dân cư đang ở chung cư.
     */
    public List<DoiTuong> findResidentsInComplex(ResidentStatus trangThaiDanCu) {
        String jpql = "SELECT d FROM DoiTuong d WHERE d.laCuDan = true AND d.trangThaiDanCu = :trangThai";
        return entityManager.createQuery(jpql, DoiTuong.class)
            .setParameter("trangThai", trangThaiDanCu)
            .getResultList();
    }
    /**
     * Tìm kiếm tài khoản cư dân theo CCCD
     */
    public Optional<DoiTuong> timNguoiDungThuongTheoCCCD(String cccd){
        String jpql = "SELECT d FROM DoiTuong d WHERE d.cccd = :cccd AND d.vaiTro = :vaiTro";
        try {
            DoiTuong doiTuong = entityManager.createQuery(jpql, DoiTuong.class)
                    .setParameter("cccd", cccd)
                    .setParameter("vaiTro", UserRole.NGUOI_DUNG_THUONG)
                    .getSingleResult();
            return Optional.of(doiTuong);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    /** 
     * Tìm cư dân theo CCCD 
    */
    public Optional<DoiTuong> findResidentByCccd(String cccd) {
        String jpql = "SELECT d FROM DoiTuong d WHERE d.cccd = :cccd AND d.laCuDan = true";
        try {
            DoiTuong doiTuong = entityManager.createQuery(jpql, DoiTuong.class)
                    .setParameter("cccd", cccd)
                    .getSingleResult();
            return Optional.of(doiTuong);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Tìm người dùng bằng reset token
     */
    public Optional<DoiTuong> findByResetToken(String token) {
        String jpql = "SELECT d FROM DoiTuong d WHERE d.resetToken = :token";
        try {
            DoiTuong doiTuong = entityManager.createQuery(jpql, DoiTuong.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return Optional.of(doiTuong);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    /**
     * Tìm kiếm cư dân theo cccd, họ tên, tuổi, địa chỉ, giới tính, sđt, chủ hộ
     */
    public List<DoiTuong> searchResidents(String keyword) {
        String jpql = "SELECT d FROM DoiTuong d WHERE d.laCuDan = true AND (" +
                      "LOWER(d.cccd) LIKE :kw OR " +
                      "LOWER(d.hoTen) LIKE :kw OR " +
                      "CAST(d.tuoi AS string) LIKE :kw OR " +
                      "LOWER(d.diaChi) LIKE :kw OR " +
                      "LOWER(d.gioiTinh) LIKE :kw OR " +
                      "LOWER(d.sdt) LIKE :kw OR " +
                      "LOWER(d.chuHo) LIKE :kw" +
                      ")";
        String kwParam = "%" + keyword.toLowerCase() + "%";
        return entityManager.createQuery(jpql, DoiTuong.class)
                            .setParameter("kw", kwParam)
                            .getResultList();
    }
}