package com.poly.sof309.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import util.ValidationUtil;

/** Kiểm tra ngoại lệ nghiệp vụ (assertThrows) cho TC_07, TC_09, TC_10. */
class ValidationControllerTest {

    @ParameterizedTest(name = "[{index}] dienTich={0} -> ném IllegalArgumentException")
    @DisplayName("TC_07: diện tích <= 0 ném ngoại lệ")
    @CsvSource({ "0", "-10", "-0.5" })
    void dienTichAmNemLoi(double dienTich) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.requirePositive(dienTich, "Diện tích"));
        assertEquals("Diện tích phải lớn hơn 0", ex.getMessage());
    }

    @ParameterizedTest(name = "[{index}] dienTich={0} hợp lệ")
    @DisplayName("TC_05: diện tích dương (kể cả giá trị biên rất nhỏ) hợp lệ")
    @CsvSource({ "75.5", "0.0001", "60" })
    void dienTichHopLe(double dienTich) {
        assertEquals(dienTich, ValidationUtil.requirePositive(dienTich, "Diện tích"));
    }

    @ParameterizedTest(name = "[{index}] cũ={0}, mới={1}")
    @DisplayName("TC_10: chỉ số mới nhỏ hơn chỉ số cũ ném ngoại lệ")
    @CsvSource({ "150, 140", "150, 149.99", "10, 0" })
    void chiSoGiamNemLoi(double cu, double moi) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.requireChiSoHopLe(cu, moi));
        assertTrue(ex.getMessage().contains("không được nhỏ hơn"));
    }

    @ParameterizedTest(name = "[{index}] cũ={0}, mới={1} hợp lệ")
    @DisplayName("TC_10: chỉ số bằng hoặc lớn hơn chỉ số cũ hợp lệ (giá trị biên)")
    @CsvSource({ "150, 150", "150, 151", "0, 0" })
    void chiSoHopLe(double cu, double moi) {
        assertDoesNotThrow(() -> ValidationUtil.requireChiSoHopLe(cu, moi));
    }

    @ParameterizedTest(name = "[{index}] CCCD=''{0}'' không hợp lệ")
    @DisplayName("TC_09: CCCD sai định dạng ném ngoại lệ")
    @NullAndEmptySource
    @ValueSource(strings = { "12345abc", "12345678901", "1234567890123", "  " })
    void cccdSaiNemLoi(String cccd) {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireCccd(cccd));
    }

    @Test
    @DisplayName("TC_09: CCCD đúng 12 chữ số hợp lệ")
    void cccdHopLe() {
        assertDoesNotThrow(() -> ValidationUtil.requireCccd("012345678901"));
    }

    @ParameterizedTest(name = "[{index}] ''{0}'' bị coi là trống")
    @DisplayName("requireNotBlank: null, rỗng, khoảng trắng đều ném ngoại lệ")
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void khongDuocTrong(String value) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.requireNotBlank(value, "Tên đăng nhập"));
        assertEquals("Tên đăng nhập không được để trống", ex.getMessage());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("Kiểm tra 100.000 lần không bị treo (Timeout 2 giây)")
    void khongBiTreo() {
        for (int i = 1; i <= 100_000; i++) {
            ValidationUtil.requirePositive(i, "Giá");
            ValidationUtil.requireChiSoHopLe(i, i + 1);
        }
    }
}
