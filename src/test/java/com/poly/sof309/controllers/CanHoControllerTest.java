package com.poly.sof309.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import servlet.BQLCanHoUpdateServlet;

/**
 * Test cho BQLCanHoUpdateServlet (Module 2 - Quản lý căn hộ, TC_07).
 * Chỉ test các nhánh validate chạy TRƯỚC khi gọi DAO nên không cần database.
 */
@Timeout(value = 5, unit = TimeUnit.SECONDS)
class CanHoControllerTest {

    private static final String CTX = "/chungcu";
    private static final String SO_DO = CTX + "/banquanly/so-do";

    private static String redirectCuaServlet(String id, String dienTich, String trangThai) throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getMethod()).thenReturn("POST");
        when(req.getContextPath()).thenReturn(CTX);
        when(req.getParameter("id")).thenReturn(id);
        when(req.getParameter("dienTich")).thenReturn(dienTich);
        when(req.getParameter("trangThai")).thenReturn(trangThai);

        new BQLCanHoUpdateServlet().service(req, resp);

        org.mockito.ArgumentCaptor<String> captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(resp).sendRedirect(captor.capture());
        return captor.getValue();
    }

    private static String loi(String thongBao) {
        return SO_DO + "?error=" + URLEncoder.encode(thongBao, StandardCharsets.UTF_8);
    }

    @ParameterizedTest(name = "[{index}] id=''{0}''")
    @DisplayName("Thiếu id căn hộ -> quay về sơ đồ, không có thông báo lỗi")
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void thieuId(String id) throws Exception {
        assertEquals(SO_DO, redirectCuaServlet(id, "60", "DangO"));
    }

    // TC_07: giá trị biên của diện tích
    @ParameterizedTest(name = "[{index}] dienTich=''{0}'' bị từ chối")
    @DisplayName("TC_07: diện tích bằng 0, âm hoặc để trống bị từ chối")
    @NullAndEmptySource
    @ValueSource(strings = { "0", "-10", "-0.01", "0.0", "   " })
    void dienTichKhongHopLe(String dienTich) throws Exception {
        assertEquals(loi("Diện tích căn hộ phải là số dương (> 0)."),
                redirectCuaServlet("1", dienTich, "DangO"));
    }

    @ParameterizedTest(name = "[{index}] trangThai=''{0}'' bị từ chối")
    @DisplayName("Trạng thái ngoài danh sách (DangO, TrongChoThue, BaoTri) bị từ chối")
    @NullAndEmptySource
    @ValueSource(strings = { "XYZ", "dango", "TRONGCHOTHUE", "  " })
    void trangThaiKhongHopLe(String trangThai) throws Exception {
        assertEquals(loi("Trạng thái căn hộ không hợp lệ."),
                redirectCuaServlet("1", "60", trangThai));
    }

    @ParameterizedTest(name = "[{index}] id=''{0}'', dienTich=''{1}''")
    @DisplayName("Dữ liệu không phải số -> báo sai định dạng, servlet không ném ngoại lệ")
    @CsvSource({ "abc, 60", "1, 12x", "1.5, 60", "1, '6,5'" })
    void saiDinhDang(String id, String dienTich) throws Exception {
        String[] ketQua = new String[1];
        assertDoesNotThrow(() -> ketQua[0] = redirectCuaServlet(id, dienTich, "DangO"));
        assertEquals(loi("Dữ liệu nhập vào không đúng định dạng."), ketQua[0]);
    }
}
