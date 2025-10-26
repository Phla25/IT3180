# Hướng Dẫn Tích Hợp Nút Xuất Báo Cáo Vào Frontend

## 📋 Tổng Quan

Tài liệu này hướng dẫn chi tiết cách tích hợp các nút xuất báo cáo (Excel & PDF) vào giao diện HTML/Thymeleaf theo từng vai trò người dùng.

---

## ✅ Các Trang Đã Được Tích Hợp

| Trang HTML | Vai Trò | Tính Năng Export |
|------------|---------|------------------|
| `apartment-list-admin.html` | Admin | Căn hộ (Excel & PDF) |
| `apartment-list-officer.html` | Officer | Căn hộ + Cư dân (Excel & PDF) |
| `fees-admin.html` | Admin | Hóa đơn (Excel & PDF) |
| `fees-accountant.html` | Accountant | Hóa đơn (Excel & PDF) |
| `fees-resident.html` | Resident | Hóa đơn của hộ (Excel & PDF) |
| `household-list.html` | Admin | Hộ gia đình (Excel & PDF) |
| `residents.html` | Admin | Cư dân (Excel & PDF) |

---

## 🎨 Thiết Kế UI/UX

### Màu Sắc Chuẩn

```css
/* Nút Excel - Màu xanh lá */
.btn-export-excel {
    background-color: #10B981;
    color: white;
}

/* Nút PDF - Màu đỏ */
.btn-export-pdf {
    background-color: #EF4444;
    color: white;
}

/* Hover effect */
.btn-export:hover {
    opacity: 0.9;
    transform: translateY(-1px);
    transition: all 0.2s;
}
```

### Icons Sử Dụng

- **Excel**: `<i class="fas fa-file-excel"></i>`
- **PDF**: `<i class="fas fa-file-pdf"></i>`
- **Cư dân**: `<i class="fas fa-users"></i>`

---

## 📝 Cách Tích Hợp Từng Loại

### 1. Trang Danh Sách (List Pages) - Admin/Officer

**Vị trí**: Ngay sau nút "Thêm Mới" hoặc trong toolbar

**Code mẫu:**
```html
<div class="toolbar">
    <h2>Danh Sách...</h2>
    <div style="display: flex; gap: 10px;">
        <a th:href="@{/admin/....-add}" class="btn-add">
            <i class="fas fa-plus"></i> Thêm Mới
        </a>
        
        <!-- Export Buttons -->
        <a th:href="@{/admin/export/apartments}" class="btn-export btn-export-excel" title="Xuất Excel">
            <i class="fas fa-file-excel"></i> Excel
        </a>
        <a th:href="@{/admin/export/apartments/pdf}" class="btn-export btn-export-pdf" title="Xuất PDF">
            <i class="fas fa-file-pdf"></i> PDF
        </a>
    </div>
</div>
```

**CSS cần thiết:**
```html
<style>
    .btn-export {
        padding: 10px 15px;
        border-radius: 8px;
        text-decoration: none;
        font-weight: 600;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        border: none;
        cursor: pointer;
    }
    
    .btn-export-excel {
        background-color: #10B981;
        color: white;
    }
    
    .btn-export-pdf {
        background-color: #EF4444;
        color: white;
    }
    
    .btn-export:hover {
        opacity: 0.9;
        transform: translateY(-1px);
        transition: all 0.2s;
    }
</style>
```

---

### 2. Trang với Header Gradient (Resident Pages)

**Vị trí**: Bên phải header, sử dụng flexbox

**Code mẫu:**
```html
<div style="background: linear-gradient(135deg, #10B981 0%, #059669 100%); 
            color: white; 
            padding: 24px; 
            border-radius: 12px; 
            margin-bottom: 24px; 
            display: flex; 
            justify-content: space-between; 
            align-items: center;">
    <div>
        <h2 style="font-size: 1.5rem; margin-bottom: 8px; margin-top: 0;">
            <i class="fas fa-file-invoice"></i> Tiêu Đề
        </h2>
        <p style="opacity: 0.9; margin: 0;">Mô tả...</p>
    </div>
    
    <!-- Export Buttons với style đặc biệt cho gradient background -->
    <div style="display: flex; gap: 10px;">
        <a th:href="@{/resident/export/invoices}" 
           class="btn-export btn-export-excel" 
           title="Xuất Excel">
            <i class="fas fa-file-excel"></i> Excel
        </a>
        <a th:href="@{/resident/export/invoices/pdf}" 
           class="btn-export btn-export-pdf" 
           title="Xuất PDF">
            <i class="fas fa-file-pdf"></i> PDF
        </a>
    </div>
</div>
```

**CSS cho gradient background:**
```css
.btn-export {
    padding: 10px 15px;
    border-radius: 8px;
    text-decoration: none;
    font-weight: 600;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border: 2px solid white; /* Border trắng nổi bật trên gradient */
    cursor: pointer;
}

.btn-export-excel {
    background-color: white;
    color: #10B981; /* Đảo màu để nổi bật */
}

.btn-export-pdf {
    background-color: white;
    color: #EF4444; /* Đảo màu để nổi bật */
}
```

---

### 3. Trang Đa Chức Năng (Officer - Multiple Exports)

**Vị trí**: Trong cùng một toolbar, nhóm các nút liên quan

**Code mẫu:**
```html
<div style="display: flex; justify-content: flex-end; gap: 10px;">
    <button type="submit" class="btn-search">
        <i class="fas fa-search"></i> Tìm Kiếm
    </button>
    
    <!-- Nhóm Export Căn Hộ -->
    <a th:href="@{/officer/export/apartments}" 
       class="btn-export btn-export-excel" 
       title="Xuất Căn Hộ - Excel">
        <i class="fas fa-file-excel"></i> Excel
    </a>
    <a th:href="@{/officer/export/apartments/pdf}" 
       class="btn-export btn-export-pdf" 
       title="Xuất Căn Hộ - PDF">
        <i class="fas fa-file-pdf"></i> PDF
    </a>
    
    <!-- Separator -->
    <div style="width: 1px; background: #D1D5DB; margin: 0 5px;"></div>
    
    <!-- Nhóm Export Cư Dân -->
    <a th:href="@{/officer/export/residents}" 
       class="btn-export btn-export-excel" 
       title="Xuất Cư Dân - Excel">
        <i class="fas fa-users"></i> Cư Dân (Excel)
    </a>
    <a th:href="@{/officer/export/residents/pdf}" 
       class="btn-export btn-export-pdf" 
       title="Xuất Cư Dân - PDF">
        <i class="fas fa-users"></i> Cư Dân (PDF)
    </a>
</div>
```

---

## 🔗 Mapping Endpoints Theo Vai Trò

### Admin
```html
<!-- Căn hộ -->
<a th:href="@{/admin/export/apartments}">Excel</a>
<a th:href="@{/admin/export/apartments/pdf}">PDF</a>

<!-- Tài sản (có filter) -->
<a th:href="@{/admin/export/assets(assetType='can_ho')}">Excel</a>
<a th:href="@{/admin/export/assets/pdf}">PDF</a>

<!-- Hóa đơn -->
<a th:href="@{/admin/export/invoices}">Excel</a>
<a th:href="@{/admin/export/invoices/pdf}">PDF</a>

<!-- Hộ gia đình -->
<a th:href="@{/admin/export/households}">Excel</a>
<a th:href="@{/admin/export/households/pdf}">PDF</a>

<!-- Cư dân -->
<a th:href="@{/admin/export/residents}">Excel</a>
<a th:href="@{/admin/export/residents/pdf}">PDF</a>
```

### Officer (CQCN)
```html
<!-- Căn hộ -->
<a th:href="@{/officer/export/apartments}">Excel</a>
<a th:href="@{/officer/export/apartments/pdf}">PDF</a>

<!-- Cư dân -->
<a th:href="@{/officer/export/residents}">Excel</a>
<a th:href="@{/officer/export/residents/pdf}">PDF</a>
```

### Accountant (Kế Toán)
```html
<!-- Hóa đơn -->
<a th:href="@{/accountant/export/invoices}">Excel</a>
<a th:href="@{/accountant/export/invoices/pdf}">PDF</a>
```

### Resident (Cư Dân)
```html
<!-- Căn hộ của hộ -->
<a th:href="@{/resident/export/apartments}">Excel</a>
<a th:href="@{/resident/export/apartments/pdf}">PDF</a>

<!-- Hóa đơn của hộ -->
<a th:href="@{/resident/export/invoices}">Excel</a>
<a th:href="@{/resident/export/invoices/pdf}">PDF</a>

<!-- Thông tin hộ gia đình -->
<a th:href="@{/resident/export/household}">Excel</a>
<a th:href="@{/resident/export/household/pdf}">PDF</a>
```

---

## 💡 Thực Hành Tốt Nhất (Best Practices)

### 1. Responsive Design
```html
<!-- Desktop: Hiện đầy đủ text -->
<a href="..." class="btn-export btn-export-excel d-none d-md-inline-flex">
    <i class="fas fa-file-excel"></i> Excel
</a>

<!-- Mobile: Chỉ hiện icon -->
<a href="..." class="btn-export btn-export-excel d-md-none">
    <i class="fas fa-file-excel"></i>
</a>
```

### 2. Loading State
```html
<a href="..." class="btn-export btn-export-excel" onclick="showLoadingToast()">
    <i class="fas fa-file-excel"></i> Excel
</a>

<script>
function showLoadingToast() {
    // Hiển thị thông báo loading
    Swal.fire({
        title: 'Đang xuất file...',
        text: 'Vui lòng đợi trong giây lát',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });
    
    // Tự động đóng sau 3 giây
    setTimeout(() => {
        Swal.close();
    }, 3000);
}
</script>
```

### 3. Tooltip cho UX tốt hơn
```html
<a href="..." 
   class="btn-export btn-export-excel" 
   title="Xuất danh sách căn hộ ra file Excel"
   data-bs-toggle="tooltip">
    <i class="fas fa-file-excel"></i> Excel
</a>
```

### 4. Dropdown Menu (Tùy chọn)
```html
<div class="btn-group">
    <button type="button" 
            class="btn btn-success dropdown-toggle" 
            data-bs-toggle="dropdown">
        <i class="fas fa-download"></i> Xuất Báo Cáo
    </button>
    <ul class="dropdown-menu">
        <li>
            <a class="dropdown-item" th:href="@{/admin/export/apartments}">
                <i class="fas fa-file-excel text-success"></i> Xuất Excel
            </a>
        </li>
        <li>
            <a class="dropdown-item" th:href="@{/admin/export/apartments/pdf}">
                <i class="fas fa-file-pdf text-danger"></i> Xuất PDF
            </a>
        </li>
    </ul>
</div>
```

---

## 🐛 Xử Lý Lỗi

### JavaScript Error Handling
```javascript
// Thêm vào các link export
document.querySelectorAll('.btn-export').forEach(btn => {
    btn.addEventListener('click', function(e) {
        const url = this.href;
        const fileType = this.classList.contains('btn-export-excel') ? 'Excel' : 'PDF';
        
        // Hiển thị loading
        Swal.fire({
            title: `Đang xuất file ${fileType}...`,
            allowOutsideClick: false,
            didOpen: () => Swal.showLoading()
        });
        
        // Fetch để check lỗi
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Lỗi khi xuất file');
                }
                Swal.close();
                // Browser sẽ tự download file
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi!',
                    text: 'Không thể xuất file. Vui lòng thử lại.'
                });
            });
    });
});
```

---

## 📱 Mobile Optimization

### CSS cho Mobile
```css
@media (max-width: 768px) {
    .btn-export {
        padding: 8px 12px;
        font-size: 14px;
    }
    
    .btn-export span.btn-text {
        display: none; /* Ẩn text, chỉ hiện icon */
    }
    
    .toolbar {
        flex-direction: column;
        gap: 10px;
    }
}
```

### HTML cho Mobile
```html
<a th:href="@{/admin/export/apartments}" class="btn-export btn-export-excel">
    <i class="fas fa-file-excel"></i> 
    <span class="btn-text">Excel</span> <!-- Sẽ ẩn trên mobile -->
</a>
```

---

## 🎯 Checklist Tích Hợp

Khi thêm nút export vào trang mới, hãy kiểm tra:

- [ ] CSS đã được thêm vào `<style>` section
- [ ] Endpoints mapping đúng theo role
- [ ] Icon Font Awesome đã load
- [ ] Hover effect hoạt động
- [ ] Title/tooltip hiển thị đúng
- [ ] Responsive trên mobile
- [ ] Thymeleaf syntax đúng (`th:href`)
- [ ] Gap/spacing phù hợp với design
- [ ] Màu sắc nhất quán với hệ thống

---

## 📚 Tài Liệu Liên Quan

- **Backend API**: `CAP_NHAT_TINH_NANG_MO_RONG.md`
- **Hướng dẫn chung**: `HUONG_DAN_XUAT_BAO_CAO.md`
- **Báo cáo triển khai**: `BAO_CAO_TRIEN_KHAI.md`

---

## 🔧 Troubleshooting

### Vấn đề: Nút không hiển thị
**Giải pháp**: Kiểm tra CSS đã được thêm vào `<style>` hoặc file CSS external

### Vấn đề: Link không hoạt động
**Giải pháp**: Kiểm tra Thymeleaf syntax và endpoint mapping

### Vấn đề: Style bị lỗi
**Giải pháp**: Kiểm tra conflict với CSS hiện tại, sử dụng class name cụ thể hơn

### Vấn đề: Mobile không responsive
**Giải pháp**: Thêm media queries và test trên các kích thước màn hình khác nhau

---

**Cập nhật**: ${new Date().toLocaleDateString('vi-VN')}  
**Tác giả**: BlueMoon Development Team

