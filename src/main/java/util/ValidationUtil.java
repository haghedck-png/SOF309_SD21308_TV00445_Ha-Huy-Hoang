package util;

/**
 * Các hàm kiểm tra nghiệp vụ dùng chung (diện tích, chỉ số điện nước, CCCD...).
 * Vi phạm nghiệp vụ -> ném IllegalArgumentException với thông báo tiếng Việt.
 * Khớp với các ca TC_07, TC_09, TC_10 trong TEST_CASES.md.
 */
public final class ValidationUtil {

    private ValidationUtil() {}

    public static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " không được để trống");
        }
    }

    /** TC_07: diện tích / giá phải lớn hơn 0. */
    public static double requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " phải lớn hơn 0");
        }
        return value;
    }

    /** TC_10: chỉ số mới không được nhỏ hơn chỉ số cũ (bằng nhau vẫn hợp lệ). */
    public static void requireChiSoHopLe(double chiSoCu, double chiSoMoi) {
        if (chiSoMoi < chiSoCu) {
            throw new IllegalArgumentException("Chỉ số mới không được nhỏ hơn chỉ số cũ");
        }
    }

    /** TC_09: CCCD gồm đúng 12 chữ số. */
    public static void requireCccd(String cccd) {
        if (cccd == null || !cccd.matches("\\d{12}")) {
            throw new IllegalArgumentException("CCCD không hợp lệ (phải gồm đúng 12 chữ số)");
        }
    }
}
