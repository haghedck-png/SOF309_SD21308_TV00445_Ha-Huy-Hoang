package com.poly.sof309.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import servlet.LoginServlet;
import util.PasswordUtil;

/** Test cho nhóm Đăng nhập & Phân quyền (Module 1 trong TEST_CASES.md). */
class AuthControllerTest {

    private static final String CTX = "/chungcu";

    // ---------- Parameterized: điều hướng theo vai trò / bộ phận ----------
    @ParameterizedTest(name = "[{index}] vaiTro={0}, boPhan={1} -> {2}")
    @DisplayName("calculateRedirectUrl: điều hướng đúng theo vai trò và bộ phận")
    @CsvSource({
        "CD,  ,         /cudan/thong-bao",
        "cd,  ,         /cudan/thong-bao",
        "BQL, ,         /banquanly/binh-chon",
        "NV,  LeTan,    /letan/su-co",
        "NV,  LT,       /letan/su-co",
        "NV,  KyThuat,  /kythuat/cong-viec",
        "NV,  NVKT,     /kythuat/cong-viec",
        "NV,  KeToan,   /ketoan/chi-so",
        "NV,  KT,       /ketoan/chi-so",
        "NV,  BaoVe,    /baove/dashboard",
        "NV,  BV,       /baove/dashboard",
        "NV,  BanQuanLy,/banquanly/binh-chon",
        "NV,  MAIN,     /banquanly/binh-chon",
        "NV,  XYZ,      /dang-nhap",
        "NV,  ,         /dang-nhap",
        "ABC, ,         /index.jsp"
    })
    void redirectTheoVaiTro(String vaiTro, String boPhan, String duongDan) {
        assertEquals(CTX + duongDan, LoginServlet.calculateRedirectUrl(CTX, vaiTro, boPhan));
    }

    @Test
    @DisplayName("calculateRedirectUrl: vaiTro null -> về index.jsp, không ném lỗi")
    void redirectVaiTroNull() {
        assertEquals(CTX + "/index.jsp", LoginServlet.calculateRedirectUrl(CTX, null, null));
    }

    // ---------- Parameterized: TC_03 bỏ trống / chỉ có khoảng trắng ----------
    @ParameterizedTest(name = "[{index}] user=''{0}'', pass=''{1}'' bị từ chối")
    @DisplayName("TC_03: doPost từ chối khi bỏ trống tên đăng nhập hoặc mật khẩu")
    @CsvSource({
        "'',      ''",
        "admin,   ''",
        "'',      123456",
        "'   ',   123456",
        "admin,   '   '",
        "admin,   ",
        ",        123456"
    })
    void loginThieuThongTin(String user, String pass) throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        PrintWriter writer = new PrintWriter(body);

        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("tenDangNhap")).thenReturn(user);
        when(req.getParameter("matKhau")).thenReturn(pass);
        when(resp.getWriter()).thenReturn(writer);

        new LoginServlet().service(req, resp);
        writer.flush();

        String json = body.toString();
        assertTrue(json.contains("\"success\":false"), "Phải trả về success=false: " + json);
        assertTrue(json.contains("Vui lòng nhập đầy đủ"), "Phải báo thiếu thông tin: " + json);
    }

    // ---------- PasswordUtil (BCrypt) ----------
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("PasswordUtil: hash rồi verify đúng/sai mật khẩu")
    void hashVaVerify() {
        String hash = PasswordUtil.hash("Pass@1234");
        assertNotNull(hash);
        assertNotEquals("Pass@1234", hash, "Không được lưu mật khẩu thô");
        assertTrue(PasswordUtil.verify("Pass@1234", hash));
        assertFalse(PasswordUtil.verify("saimatkhau", hash));
    }

    @ParameterizedTest(name = "[{index}] verify(''{0}'', ''{1}'') = false")
    @DisplayName("PasswordUtil.verify: dữ liệu không hợp lệ luôn trả về false")
    @CsvSource({
        "123456,   123456",               // hash không phải BCrypt (mật khẩu thô trong DB)
        "123456,   ''",                   // hash rỗng
        "123456,   ",                     // hash null
        ",         $2a$12$abcdefghijklmnopqrstuuvwxyz", // mật khẩu null
        "123456,   $2a$12$khongdungdinhdang"             // đúng tiền tố nhưng sai cấu trúc
    })
    void verifyKhongHopLe(String plain, String hashed) {
        assertFalse(PasswordUtil.verify(plain, hashed));
    }

    @Test
    @DisplayName("PasswordUtil.hash: mật khẩu null hoặc rỗng trả về null")
    void hashRong() {
        assertNull(PasswordUtil.hash(null));
        assertNull(PasswordUtil.hash(""));
    }
}
