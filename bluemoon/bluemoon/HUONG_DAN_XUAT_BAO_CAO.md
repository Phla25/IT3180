# Hướng Dẫn Sử Dụng Tính Năng Xuất Báo Cáo

## Tổng Quan

Hệ thống BlueMoon đã được tích hợp tính năng xuất báo cáo thông tin ra file Excel (.xlsx) cho các vai trò khác nhau: **Admin**, **Kế toán (Accountant)**, và **Cư dân (Resident)**.

## Các Tính Năng Đã Được Triển Khai

### 1. Dependency
- **Apache POI 5.2.5**: Thư viện để tạo và xuất file Excel

### 2. DTO Models (Data Transfer Objects)
Các DTO được tạo để chuẩn bị dữ liệu trước khi xuất:

- **ApartmentReportDTO**: Thông tin căn hộ và tài sản
- **InvoiceReportDTO**: Thông tin hóa đơn
- **HouseholdReportDTO**: Thông tin hộ gia đình

### 3. Services

#### ExportService
Service chịu trách nhiệm xuất dữ liệu ra file Excel với các phương thức:
- `exportApartmentsToExcel()`: Xuất danh sách căn hộ/tài sản
- `exportInvoicesToExcel()`: Xuất danh sách hóa đơn
- `exportHouseholdsToExcel()`: Xuất danh sách hộ gia đình

#### ReportService
Service truy xuất và tổng hợp dữ liệu từ database theo vai trò:

**Cho Admin:**
- `getApartmentReportForAdmin()`: Lấy tất cả căn hộ
- `getAssetReportForAdmin()`: Lấy tất cả tài sản (có thể lọc theo loại)
- `getInvoiceReportForAdmin()`: Lấy tất cả hóa đơn
- `getHouseholdReportForAdmin()`: Lấy tất cả hộ gia đình

**Cho Kế toán:**
- `getInvoiceReportForAccountant()`: Lấy tất cả hóa đơn

**Cho Cư dân:**
- `getApartmentReportForResident(cccd)`: Lấy căn hộ của hộ mình
- `getInvoiceReportForResident(cccd)`: Lấy hóa đơn của hộ mình
- `getHouseholdReportForResident(cccd)`: Lấy thông tin hộ gia đình của mình

## API Endpoints

### Admin Endpoints

#### 1. Xuất danh sách căn hộ
```
GET /admin/export/apartments
```
- **Mô tả**: Xuất tất cả căn hộ trong hệ thống
- **Response**: File Excel (.xlsx)
- **Tên file**: `BaoCao_CanHo_[timestamp].xlsx`

#### 2. Xuất danh sách tài sản
```
GET /admin/export/assets?assetType=[loại_tài_sản]
```
- **Mô tả**: Xuất tất cả tài sản (có thể lọc theo loại)
- **Params**: 
  - `assetType` (optional): can_ho, tien_ich, etc.
- **Response**: File Excel (.xlsx)
- **Tên file**: `BaoCao_TaiSan_[timestamp].xlsx`

#### 3. Xuất danh sách hóa đơn
```
GET /admin/export/invoices
```
- **Mô tả**: Xuất tất cả hóa đơn trong hệ thống
- **Response**: File Excel (.xlsx)
- **Tên file**: `BaoCao_HoaDon_[timestamp].xlsx`

#### 4. Xuất danh sách hộ gia đình
```
GET /admin/export/households
```
- **Mô tả**: Xuất tất cả hộ gia đình trong hệ thống
- **Response**: File Excel (.xlsx)
- **Tên file**: `BaoCao_HoGiaDinh_[timestamp].xlsx`

### Accountant Endpoints

#### 1. Xuất danh sách hóa đơn
```
GET /accountant/export/invoices
```
- **Mô tả**: Xuất tất cả hóa đơn (view kế toán)
- **Response**: File Excel (.xlsx)
- **Tên file**: `BaoCao_HoaDon_KeToan_[timestamp].xlsx`

### Resident Endpoints

#### 1. Xuất danh sách căn hộ của hộ
```
GET /resident/export/apartments
```
- **Mô tả**: Xuất căn hộ thuộc hộ gia đình của cư dân
- **Authentication**: Required (JWT/Session)
- **Response**: File Excel (.xlsx)
- **Tên file**: `BaoCao_CanHo_CuDan_[timestamp].xlsx`

#### 2. Xuất danh sách hóa đơn của hộ
```
GET /resident/export/invoices
```
- **Mô tả**: Xuất hóa đơn thuộc hộ gia đình của cư dân
- **Authentication**: Required (JWT/Session)
- **Response**: File Excel (.xlsx)
- **Tên file**: `BaoCao_HoaDon_CuDan_[timestamp].xlsx`

#### 3. Xuất thông tin hộ gia đình
```
GET /resident/export/household
```
- **Mô tả**: Xuất thông tin hộ gia đình của cư dân
- **Authentication**: Required (JWT/Session)
- **Response**: File Excel (.xlsx)
- **Tên file**: `BaoCao_HoGiaDinh_CuDan_[timestamp].xlsx`

## Cấu Trúc File Excel

### Báo Cáo Căn Hộ
Các cột bao gồm:
- Mã Tài Sản
- Tên Tài Sản
- Loại
- Trạng Thái
- Diện Tích (m²)
- Vị Trí
- Giá Trị (VNĐ)
- Ngày Thêm
- Mã Hộ
- Tên Hộ
- Chủ Hộ
- Số Điện Thoại
- Trạng Thái Hộ

### Báo Cáo Hóa Đơn
Các cột bao gồm:
- Mã Hóa Đơn
- Mã Hộ
- Tên Hộ
- Chủ Hộ
- Loại Hóa Đơn
- Số Tiền (VNĐ)
- Trạng Thái
- Ngày Tạo
- Hạn Thanh Toán
- Ngày Thanh Toán
- Người Thanh Toán
- Ghi Chú

### Báo Cáo Hộ Gia Đình
Các cột bao gồm:
- Mã Hộ
- Tên Hộ
- Chủ Hộ
- CCCD Chủ Hộ
- Số Điện Thoại
- Email
- Số Thành Viên
- Số Căn Hộ
- Trạng Thái
- Ngày Thành Lập
- Ghi Chú

## Cách Sử Dụng

### Từ Giao Diện Web (Thymeleaf)

Thêm button xuất báo cáo vào các trang HTML tương ứng:

#### Ví dụ cho Admin - Xuất danh sách căn hộ:
```html
<a href="/admin/export/apartments" class="btn btn-success">
    <i class="fas fa-file-excel"></i> Xuất Excel
</a>
```

#### Ví dụ cho Kế toán - Xuất danh sách hóa đơn:
```html
<a href="/accountant/export/invoices" class="btn btn-success">
    <i class="fas fa-download"></i> Tải Báo Cáo Hóa Đơn
</a>
```

#### Ví dụ cho Cư dân - Xuất hóa đơn của hộ:
```html
<a href="/resident/export/invoices" class="btn btn-primary">
    <i class="fas fa-file-download"></i> Xuất Hóa Đơn
</a>
```

### Từ JavaScript/jQuery

```javascript
// Xuất báo cáo với loading indicator
function exportReport(url, filename) {
    // Hiển thị loading
    showLoading();
    
    // Download file
    window.location.href = url;
    
    // Ẩn loading sau 2 giây
    setTimeout(hideLoading, 2000);
}

// Sử dụng
$('#exportBtn').click(function() {
    exportReport('/admin/export/apartments', 'Danh_Sach_Can_Ho.xlsx');
});
```

## Phân Quyền và Bảo Mật

### Phân quyền theo Role
- **ADMIN**: Có thể xuất tất cả báo cáo về căn hộ, tài sản, hóa đơn, hộ gia đình
- **ACCOUNTANT**: Có thể xuất báo cáo hóa đơn (tất cả)
- **RESIDENT**: Chỉ có thể xuất báo cáo liên quan đến hộ gia đình của mình

### Xác thực
Tất cả endpoints đều yêu cầu authentication qua Spring Security. Hệ thống sẽ tự động lấy thông tin người dùng hiện tại từ `Authentication` object.

## Xử Lý Lỗi

Các trường hợp lỗi có thể xảy ra:

1. **401 Unauthorized**: Người dùng chưa đăng nhập
2. **500 Internal Server Error**: Lỗi khi tạo file Excel hoặc truy vấn database

```java
// Trong controller, các lỗi được xử lý như sau:
try {
    // Logic xuất báo cáo
    return ResponseEntity.ok()
            .headers(headers)
            .body(excelData);
} catch (Exception e) {
    return ResponseEntity.internalServerError().build();
}
```

## Tối Ưu Hóa Performance

### Lazy Loading
- Sử dụng JOIN FETCH trong các query để tránh N+1 query problem
- Chỉ load các trường cần thiết cho báo cáo

### Caching (Tương lai)
Có thể implement caching cho các báo cáo thường xuyên được truy cập:
```java
@Cacheable("reports")
public List<ApartmentReportDTO> getApartmentReportForAdmin() {
    // ...
}
```

## Mở Rộng Tương Lai

### 1. Xuất PDF
Có thể thêm thư viện iText để xuất PDF:
```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
</dependency>
```

### 2. Xuất CSV
Tạo phương thức xuất CSV đơn giản hơn cho dữ liệu lớn.

### 3. Lọc và Tùy Chỉnh
Thêm các tham số filter:
```
GET /admin/export/invoices?status=chua_thanh_toan&fromDate=2024-01-01&toDate=2024-12-31
```

### 4. Scheduled Reports
Tự động tạo và gửi email báo cáo định kỳ.

## Troubleshooting

### Lỗi: "Cannot download file"
- Kiểm tra quyền truy cập của user
- Kiểm tra database connection
- Xem log server để tìm lỗi chi tiết

### Lỗi: "File bị hỏng"
- Đảm bảo Apache POI dependency đã được thêm đúng
- Kiểm tra encoding của dữ liệu (UTF-8)

### Lỗi: "Dữ liệu không đầy đủ"
- Kiểm tra các relationship trong Entity (@OneToMany, @ManyToOne)
- Đảm bảo EAGER/LAZY loading được cấu hình đúng

## Liên Hệ & Hỗ Trợ

Nếu có vấn đề hoặc cần hỗ trợ, vui lòng liên hệ team phát triển hoặc tạo issue trên repository.

---

**Version**: 1.0  
**Last Updated**: 2024  
**Author**: BlueMoon Development Team

