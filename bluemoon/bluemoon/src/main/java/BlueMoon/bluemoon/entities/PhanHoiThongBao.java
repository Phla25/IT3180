package BlueMoon.bluemoon.entities;

import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "phan_hoi_thong_bao") // Đổi tên bảng để phân biệt với phản hồi sự cố
@DynamicUpdate
public class PhanHoiThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phan_hoi")
    private Integer maPhanHoi;

    // SỬA ĐỔI: Thay thế BaoCaoSuCo bằng ThongBao
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thong_bao", nullable = false)
    @JsonBackReference
    private ThongBao thongBao; // <-- Dùng trực tiếp entity, không dùng Optional


    // SỬA ĐỔI: Đổi tên cột và biến cho rõ ràng hơn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cccd_nguoi_gui", nullable = false)
    private DoiTuong nguoiGui; // Người gửi phản hồi (Cư dân)

    @Column(name = "noi_dung_phan_hoi", columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Column(name = "thoi_gian_gui") // Đổi tên cột cho phù hợp
    private LocalDateTime thoiGianGui;

    // LOẠI BỎ THUỘC TÍNH: Bỏ trường ghi chú nếu nó không cần thiết cho phản hồi thông báo
    // @Column(name = "ghi_chu", columnDefinition = "TEXT")
    // private String ghiChu; 

    // -------------------- Constructors --------------------

    public PhanHoiThongBao() {
    }

    // SỬA ĐỔI: Cập nhật Constructor
 // Constructor
    public PhanHoiThongBao(ThongBao thongBao, DoiTuong nguoiGui, String noiDung) {
        this.thongBao = thongBao;
        this.nguoiGui = nguoiGui;
        this.noiDung = noiDung;
    }

    // Getter/Setter
    public ThongBao getThongBao() {
        return thongBao;
    }

    public void setThongBao(ThongBao thongBao) {
        this.thongBao = thongBao;
    }


    // -------------------- Getter & Setter --------------------

    // SỬA ĐỔI: Cập nhật Getter/Setter
    
    public void setNoiDung(String noiDung) {
    	this.noiDung = noiDung;
    }
    
    public String getNoiDung() {
    	return noiDung;
    }

    public DoiTuong getNguoiGui() {
        return nguoiGui;
    }

    public void setNguoiGui(DoiTuong nguoiGui) {
        this.nguoiGui = nguoiGui;
    }
    
    // ... (Giữ nguyên các Getter/Setter còn lại và @PrePersist) ...

    @PrePersist
    protected void onCreate() {
        if (this.thoiGianGui == null) {
            this.thoiGianGui = LocalDateTime.now();
        }
    }
    public LocalDateTime getThoiGianGui() {
        return thoiGianGui;
    }

}