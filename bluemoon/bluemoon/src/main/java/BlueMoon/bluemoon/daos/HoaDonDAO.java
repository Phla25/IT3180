package BlueMoon.bluemoon.daos;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import BlueMoon.bluemoon.entities.HoGiaDinh;
import BlueMoon.bluemoon.entities.HoaDon;
import BlueMoon.bluemoon.utils.InvoiceStatus;
import jakarta.persistence.EntityManager;

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
    public BigDecimal sumSoTienByTrangThai(InvoiceStatus trangThai) {
        String jpql = "SELECT SUM(hd.soTien) FROM HoaDon hd WHERE hd.trangThai = :trangThai";
        BigDecimal sum = entityManager.createQuery(jpql, BigDecimal.class)
                .setParameter("trangThai", trangThai)
                .getSingleResult();
        return sum != null ? sum : BigDecimal.ZERO;
    }
}
