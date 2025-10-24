package BlueMoon.bluemoon.models;

/**
 * Đối tượng truyền dữ liệu (DTO) chứa thông tin căn hộ chính
 * và thông tin chủ hộ/tình trạng để hiển thị trên Dashboard Cư Dân.
 */
public class HoGiaDinhDTO {
    
    // Mã căn hộ (Ví dụ: A-1201, lấy từ TaiSanChungCu.tenTaiSan hoặc viTri)
    private String maCanHo;  
    
    // Tên của Chủ hộ (Lấy từ DoiTuong qua ThanhVienHo)
    private String chuHo;    
    
    // Tình trạng căn hộ hoặc tình trạng cư trú (Ví dụ: "Đang cư trú", "N/A", "Đã bán")
    private String tinhTrang; 

    private Integer tongThanhVien;
    
    // ==== Constructors ====
    
    public HoGiaDinhDTO() {
        this.maCanHo = "N/A";
        this.chuHo = "N/A";
        this.tinhTrang = "Không xác định";
        this.tongThanhVien = 0;
    }

    public HoGiaDinhDTO(String maCanHo, String chuHo, String tinhTrang, Integer tongThanhVien) {
        this.maCanHo = maCanHo;
        this.chuHo = chuHo;
        this.tinhTrang = tinhTrang;
        this.tongThanhVien = tongThanhVien;
    }
    public HoGiaDinhDTO(String maCanHo, String chuHo, String tinhTrang) {
        this.maCanHo = maCanHo;
        this.chuHo = chuHo;
        this.tinhTrang = tinhTrang;
        this.tongThanhVien = 0;
    }

    // ==== Getters and Setters ====

    public String getMaCanHo() {
        return maCanHo;
    }

    public void setMaCanHo(String maCanHo) {
        this.maCanHo = maCanHo;
    }

    public String getChuHo() {
        return chuHo;
    }

    public void setChuHo(String chuHo) {
        this.chuHo = chuHo;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public Integer getTongThanhVien() {
        return tongThanhVien;
    }
    public void setTongThanhVien(Integer tongThanhVien) {
        this.tongThanhVien = tongThanhVien;
    }
}