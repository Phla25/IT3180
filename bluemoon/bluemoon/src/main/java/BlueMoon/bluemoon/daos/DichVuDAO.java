package BlueMoon.bluemoon.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

@Repository
public class DichVuDAO {
    @Autowired
    private EntityManager entityManager;
    
    /**
     * Đếm tông số dịch vụ trong hệ thống
     */
    public Long countAll() {
        String jpql = "SELECT COUNT(dv) FROM DichVu dv";
        Long count = entityManager.createQuery(jpql, Long.class).getSingleResult();
        return count != null ? count : 0;
    }
    
}
