package BlueMoon.bluemoon.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

@Repository
public class HoGiaDinhDAO {
    @Autowired
    private EntityManager entityManager;

    public Long countAll() {
        String jpql = "SELECT COUNT(hgd) FROM HoGiaDinh hgd";
        Long count = entityManager.createQuery(jpql, Long.class).getSingleResult();
        return count != null ? count : 0;
    }
    
    /**
     * Đếm tổng số hộ gia đình đang hoạt động
     */
    public Long countActiveHouseholds() {
        String jpql = "SELECT COUNT(hgd) FROM HoGiaDinh hgd WHERE hgd.dangHoatDong = true";
        Long count = entityManager.createQuery(jpql, Long.class).getSingleResult();
        return count != null ? count : 0;
    }
    /**
     * Đếm tổng số thành viên trong 1 hộ gia đình
     */
    public Long countMembersInHousehold(String maHo) {
        String jpql = "SELECT COUNT(tvh) FROM ThanhVienHo tvh WHERE tvh.hoGiaDinh.maHo = :maHo ";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("maHo", maHo)
                .getSingleResult();
        return count != null ? count : 0;
    }
}
