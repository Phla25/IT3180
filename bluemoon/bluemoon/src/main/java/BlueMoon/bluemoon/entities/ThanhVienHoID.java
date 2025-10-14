package BlueMoon.bluemoon.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Khóa tổng hợp cho bảng thanh_vien_ho:
 * Gồm cccd (mã định danh cá nhân) + ngày bắt đầu ở hộ.
 * @param <ThanhVienHoId>
 */
@SuppressWarnings("serial")
@Embeddable
public class ThanhVienHoID<ThanhVienHoId> implements Serializable {

    @Column(name = "cccd", length = 12, nullable = false)
    private String cccd;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    public ThanhVienHoID() {};

    public ThanhVienHoID(String cccd, LocalDate ngayBatDau) {
        this.cccd = cccd;
        this.ngayBatDau = ngayBatDau;
    }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    @SuppressWarnings("rawtypes")
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThanhVienHoID)) return false;
        ThanhVienHoID that = (ThanhVienHoID) o;
        return Objects.equals(cccd, that.cccd) &&
               Objects.equals(ngayBatDau, that.ngayBatDau);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cccd, ngayBatDau);
    }
}
