package BlueMoon.bluemoon.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

@Repository
public class DangKyDichVuDAO {
    @Autowired
    private EntityManager entityManager;

    /**
     * Đếm tổng số dịch vụ đăng ký bởi cư dân trong hệ thống
     */
    public Long countAll() {
        String jpql = "SELECT COUNT(dkdv) FROM DangKyDichVu dkdv";
        Long count = entityManager.createQuery(jpql, Long.class).getSingleResult();
        return count != null ? count : 0;
    }
    /** 
     * Đếm Tổng số dịch vụ đăng ký bởi cư dân cụ thể
     */
    public Long countByCccd(String cccd) {
        String jpql = "SELECT COUNT(dkdv) FROM DangKyDichVu dkdv WHERE dkdv.doiTuong.cccd = :cccd";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("cccd", cccd)
                .getSingleResult();
        return count != null ? count : 0;
    }
}
