package BlueMoon.bluemoon.utils; // Hoặc package utilities của bạn

import org.springframework.stereotype.Component;

@Component("residentUtils")
public class ResidentUtils {

    /**
     * Tính toán mã màu Hex (6 ký tự) từ một chuỗi (CCCD) để tạo màu avatar ngẫu nhiên.
     * @param inputString Chuỗi đầu vào (CCCD).
     * @return Chuỗi mã màu Hex (ví dụ: "A3B5C7").
     */
    public String calculateColorHash(String inputString) {
        if (inputString == null || inputString.trim().isEmpty()) {
            return "6C757D"; // Màu xám mặc định nếu null
        }
        
        // 1. Lấy Hash Code (có thể là số âm)
        int hashCode = inputString.hashCode();
        
        // 2. Chuyển sang giá trị dương 6 chữ số (0xFFFFFF)
        // và giới hạn dải màu
        int positiveHash = Math.abs(hashCode) & 0xFFFFFF;
        
        // 3. Format thành chuỗi Hex 6 ký tự (với padding 0 nếu cần)
        // Đây là cách an toàn và gọn gàng hơn so với formatInteger trong Thymeleaf
        return String.format("%06X", positiveHash);
    }
}