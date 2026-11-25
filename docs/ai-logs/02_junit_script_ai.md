# Nhật ký AI: Sinh Unit Test JUnit 5 cho Controller (Y3)

## 1. Ngữ cảnh & Mục tiêu
- Công cụ AI sử dụng: Claude (Anthropic).
- Cần làm gì: viết Unit Test cho nhóm Controller của dự án. Dự án dùng mô hình Servlet/JSP nên Servlet đóng vai trò Controller. Ba class test cần tạo trong `src/test/java/com/poly/sof309/controllers/`:
  - `AuthControllerTest`: chức năng Đăng nhập & Phân quyền (Module 1).
  - `CanHoControllerTest`: chức năng cập nhật căn hộ (Module 2, TC_07).
  - `ValidationControllerTest`: kiểm tra ngoại lệ nghiệp vụ (TC_07, TC_09, TC_10).
- Kỹ thuật yêu cầu: `@ParameterizedTest` + `@CsvSource` kiểm tra biên form; `assertThrows()` kiểm tra ngoại lệ nghiệp vụ; `@Timeout` kiểm tra không treo vòng lặp.
- Mục tiêu nghiệm thu: `mvn test -Dtest=*ControllerTest` báo BUILD SUCCESS, 100% pass.

## 2. Câu lệnh đã gửi cho AI (Prompt)

**Prompt lần 1:**
> "Tôi có class `AuthController` với hàm đăng ký nhận username (5-20 ký tự) và password (tối thiểu 8 ký tự). Hãy viết JUnit 5 test dùng @ParameterizedTest và @CsvSource kiểm tra các giá trị biên, và một test dùng assertThrows khi tài khoản đã tồn tại."

**Prompt lần 2:**
> "Tạo ValidationControllerTest, CanHoControllerTest tiếp theo các bản test này"

## 3. Kết quả do AI sinh ra (Raw Output)

Prompt lần 1 sinh ra `AuthControllerTest`, prompt lần 2 sinh ra `ValidationControllerTest` và `CanHoControllerTest`. Dưới đây là bản gốc, chưa chỉnh sửa.

### 3.1. AuthControllerTest (bản gốc)

```java
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * GIẢ ĐỊNH về AuthController (hãy chỉnh lại cho khớp code thật của bạn):
 *  - AuthController có constructor không tham số.
 *  - void register(String username, String password)
 *  - Dữ liệu không hợp lệ  -> ném IllegalArgumentException
 *  - Tài khoản đã tồn tại  -> ném UserAlreadyExistsException
 */
class AuthControllerTest {

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(); // mỗi test một instance mới -> không dính state
    }

    // ---------- Giá trị biên của username (5-20 ký tự) ----------

    @ParameterizedTest(name = "username=''{0}'' ({1} ký tự) hợp lệ")
    @CsvSource({
            "abcde,                 5",   // biên dưới
            "abcdef,                6",   // ngay trên biên dưới
            "abcdefghijklmnopqrs,   19",  // ngay dưới biên trên
            "abcdefghijklmnopqrst,  20"   // biên trên
    })
    @DisplayName("Username ở trong khoảng 5-20 ký tự -> đăng ký thành công")
    void register_validUsernameBoundaries(String username, int length) {
        assertEquals(length, username.length()); // kiểm tra lại dữ liệu test
        assertDoesNotThrow(() -> controller.register(username, "password123"));
    }

    @ParameterizedTest(name = "username=''{0}'' ({1} ký tự) không hợp lệ")
    @CsvSource({
            "abcd,                  4",   // ngay dưới biên dưới
            "a,                     1",
            "abcdefghijklmnopqrstu, 21"   // ngay trên biên trên
    })
    @DisplayName("Username ngoài khoảng 5-20 ký tự -> ném IllegalArgumentException")
    void register_invalidUsernameBoundaries(String username, int length) {
        assertEquals(length, username.length());
        assertThrows(IllegalArgumentException.class,
                () -> controller.register(username, "password123"));
    }

    // ---------- Giá trị biên của password (tối thiểu 8 ký tự) ----------

    @ParameterizedTest(name = "password=''{0}'' ({1} ký tự) hợp lệ")
    @CsvSource({
            "12345678,   8",   // biên dưới
            "123456789,  9"    // ngay trên biên dưới
    })
    @DisplayName("Password >= 8 ký tự -> đăng ký thành công")
    void register_validPasswordBoundaries(String password, int length) {
        assertEquals(length, password.length());
        assertDoesNotThrow(() -> controller.register("validUser", password));
    }

    @ParameterizedTest(name = "password=''{0}'' ({1} ký tự) không hợp lệ")
    @CsvSource({
            "1234567,  7",   // ngay dưới biên
            "123,      3"
    })
    @DisplayName("Password < 8 ký tự -> ném IllegalArgumentException")
    void register_invalidPasswordBoundaries(String password, int length) {
        assertEquals(length, password.length());
        assertThrows(IllegalArgumentException.class,
                () -> controller.register("validUser", password));
    }

    // ---------- Tài khoản đã tồn tại ----------

    @Test
    @DisplayName("Đăng ký trùng username -> ném UserAlreadyExistsException")
    void register_duplicateUsername_throwsException() {
        controller.register("existingUser", "password123"); // lần đầu thành công

        UserAlreadyExistsException ex = assertThrows(
                UserAlreadyExistsException.class,
                () -> controller.register("existingUser", "anotherPass456"));

        assertNotNull(ex.getMessage()); // tuỳ chọn: kiểm tra thêm message nếu bạn có quy ước
    }
}
```

### 3.2. CanHoControllerTest (bản gốc)

```java
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * GIẢ ĐỊNH (chỉnh lại cho khớp code thật):
 *  - CanHoController có constructor không tham số, lưu trữ in-memory.
 *  - CanHo themCanHo(String maCanHo, double dienTich, int soPhongNgu, double giaThue)
 *  - CanHo timCanHo(String maCanHo)
 *  - Quy tắc: dienTich > 0, soPhongNgu >= 1, giaThue >= 0, maCanHo không rỗng
 *  - Dữ liệu sai       -> IllegalArgumentException
 *  - Mã căn hộ trùng   -> CanHoAlreadyExistsException
 *  - Không tìm thấy    -> CanHoNotFoundException
 *  - CanHo có getter getMaCanHo(), getDienTich(), getSoPhongNgu(), getGiaThue()
 */
class CanHoControllerTest {

    private CanHoController controller;

    @BeforeEach
    void setUp() {
        controller = new CanHoController();
    }

    // ---------- Thêm căn hộ hợp lệ ----------

    @Test
    @DisplayName("Thêm căn hộ hợp lệ -> trả về căn hộ với đúng dữ liệu")
    void themCanHo_valid() {
        CanHo canHo = controller.themCanHo("A101", 65.5, 2, 12_000_000);

        assertEquals("A101", canHo.getMaCanHo());
        assertEquals(65.5, canHo.getDienTich());
        assertEquals(2, canHo.getSoPhongNgu());
        assertEquals(12_000_000, canHo.getGiaThue());
    }

    // ---------- Giá trị biên ----------

    @ParameterizedTest(name = "dienTich={0}, soPhongNgu={1}, giaThue={2} -> hợp lệ")
    @CsvSource({
            "0.01, 1, 0",          // biên dưới: diện tích nhỏ nhất, 1 phòng, giá 0
            "50.0, 1, 1",
            "100.0, 5, 50000000"
    })
    @DisplayName("Giá trị biên hợp lệ -> thêm thành công")
    void themCanHo_validBoundaries(double dienTich, int soPhongNgu, double giaThue) {
        assertDoesNotThrow(() -> controller.themCanHo("B201", dienTich, soPhongNgu, giaThue));
    }

    @ParameterizedTest(name = "dienTich={0}, soPhongNgu={1}, giaThue={2} -> không hợp lệ")
    @CsvSource({
            "0,     2, 1000000",   // diện tích = 0
            "-1,    2, 1000000",   // diện tích âm
            "50,    0, 1000000",   // 0 phòng ngủ
            "50,   -1, 1000000",   // số phòng âm
            "50,    2, -1"         // giá thuê âm
    })
    @DisplayName("Giá trị biên không hợp lệ -> ném IllegalArgumentException")
    void themCanHo_invalidBoundaries(double dienTich, int soPhongNgu, double giaThue) {
        assertThrows(IllegalArgumentException.class,
                () -> controller.themCanHo("C301", dienTich, soPhongNgu, giaThue));
    }

    @ParameterizedTest
    @CsvSource(value = {"''", "'   '"})
    @DisplayName("Mã căn hộ rỗng hoặc toàn khoảng trắng -> ném IllegalArgumentException")
    void themCanHo_blankMa(String ma) {
        assertThrows(IllegalArgumentException.class,
                () -> controller.themCanHo(ma, 50, 2, 1_000_000));
    }

    @Test
    @DisplayName("Mã căn hộ null -> ném IllegalArgumentException")
    void themCanHo_nullMa() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.themCanHo(null, 50, 2, 1_000_000));
    }

    // ---------- Trùng mã ----------

    @Test
    @DisplayName("Thêm căn hộ trùng mã -> ném CanHoAlreadyExistsException")
    void themCanHo_duplicate() {
        controller.themCanHo("A101", 65, 2, 12_000_000);

        assertThrows(CanHoAlreadyExistsException.class,
                () -> controller.themCanHo("A101", 80, 3, 15_000_000));
    }

    // ---------- Tìm căn hộ ----------

    @Test
    @DisplayName("Tìm căn hộ đã thêm -> trả về đúng căn hộ")
    void timCanHo_found() {
        controller.themCanHo("A101", 65, 2, 12_000_000);

        assertEquals("A101", controller.timCanHo("A101").getMaCanHo());
    }

    @Test
    @DisplayName("Tìm căn hộ không tồn tại -> ném CanHoNotFoundException")
    void timCanHo_notFound() {
        assertThrows(CanHoNotFoundException.class, () -> controller.timCanHo("ZZZ"));
    }
}
```

### 3.3. ValidationControllerTest (bản gốc)

```java
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

/**
 * GIẢ ĐỊNH (chỉnh lại cho khớp code thật):
 *  - ValidationController có constructor không tham số.
 *  - boolean isValidUsername(String)  : 5-20 ký tự
 *  - boolean isValidPassword(String)  : tối thiểu 8 ký tự
 *  - Giá trị null/rỗng -> trả về false (không ném exception)
 */
class ValidationControllerTest {

    private ValidationController validator;

    @BeforeEach
    void setUp() {
        validator = new ValidationController();
    }

    // ---------- Username ----------

    @ParameterizedTest(name = "username=''{0}'' ({1} ký tự) -> {2}")
    @CsvSource({
            "abcd,                  4,  false",  // dưới biên dưới
            "abcde,                 5,  true",   // biên dưới
            "abcdef,                6,  true",
            "abcdefghijklmnopqrs,   19, true",
            "abcdefghijklmnopqrst,  20, true",   // biên trên
            "abcdefghijklmnopqrstu, 21, false"   // trên biên trên
    })
    @DisplayName("Username: kiểm tra giá trị biên 5-20 ký tự")
    void isValidUsername_boundaries(String username, int length, boolean expected) {
        assertEquals(length, username.length());
        assertEquals(expected, validator.isValidUsername(username));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Username null hoặc rỗng -> false")
    void isValidUsername_nullOrEmpty(String username) {
        assertFalse(validator.isValidUsername(username));
    }

    // ---------- Password ----------

    @ParameterizedTest(name = "password=''{0}'' ({1} ký tự) -> {2}")
    @CsvSource({
            "1234567,   7, false",  // dưới biên
            "12345678,  8, true",   // biên dưới
            "123456789, 9, true"
    })
    @DisplayName("Password: kiểm tra giá trị biên tối thiểu 8 ký tự")
    void isValidPassword_boundaries(String password, int length, boolean expected) {
        assertEquals(length, password.length());
        assertEquals(expected, validator.isValidPassword(password));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Password null hoặc rỗng -> false")
    void isValidPassword_nullOrEmpty(String password) {
        assertFalse(validator.isValidPassword(password));
    }

    @Test
    @DisplayName("Password rất dài vẫn hợp lệ (không có giới hạn trên)")
    void isValidPassword_veryLong() {
        assertTrue(validator.isValidPassword("a".repeat(200)));
    }
}
```

## 4. Chỉnh sửa của Sinh viên & Lý do sửa

### 4.1. Những điểm AI làm sai hoặc thiếu

**Điểm chung cho cả 3 file:**
- AI không biết cấu trúc dự án nên tự giả định các class `AuthController`, `CanHoController`, `ValidationController` (chính AI ghi chú "GIẢ ĐỊNH, hãy chỉnh lại cho khớp code thật"). Dự án thật dùng Servlet (`LoginServlet`, `BQLCanHoUpdateServlet`) và lớp tiện ích (`PasswordUtil`, `ValidationUtil`), nên em đổi đối tượng test cho khớp.
- Quy tắc nghiệp vụ AI đoán (username 5-20 ký tự, password >= 8 ký tự, số phòng ngủ, giá thuê...) không phải quy tắc thật của hệ thống. Em thay bằng các quy tắc trong TEST_CASES.md (TC_03, TC_05, TC_07, TC_09, TC_10).
- Bản AI không có dòng `package`. Em thêm `package com.poly.sof309.controllers;`.
- Prompt chưa yêu cầu `@Timeout` nên AI không thêm. Em bổ sung để đạt Bước 2.4.
- Code thật dùng Servlet nên phải giả lập request/response. Em thêm Mockito và dùng `jakarta.servlet`.

**AuthControllerTest:**

| Điểm | Bản AI | Bản em sửa |
|---|---|---|
| Đối tượng test | `AuthController.register()` giả định, ném `UserAlreadyExistsException` | `LoginServlet.calculateRedirectUrl`, `LoginServlet.service/doPost` và `PasswordUtil` |
| Cách gọi | `new AuthController()` gọi trực tiếp | Mockito `mock(HttpServletRequest/HttpServletResponse)`, đọc JSON từ `PrintWriter` giả |
| Dữ liệu biên | Username 5-20, password >= 8 | Vai trò/bộ phận (CD, BQL, NV...), bỏ trống, chuỗi toàn khoảng trắng, `null` (TC_03), hash không phải BCrypt |
| `@Timeout` | Không có | `@Timeout(5s)` cho test BCrypt hash/verify |

**CanHoControllerTest:**

| Điểm | Bản AI | Bản em sửa |
|---|---|---|
| Đối tượng test | `CanHoController` giả định (`themCanHo`, `timCanHo`, lưu in-memory) | `BQLCanHoUpdateServlet` thật, chỉ test các nhánh validate chạy trước khi gọi DAO nên không cần database |
| Cách kiểm tra | Exception (`CanHoAlreadyExistsException`, `CanHoNotFoundException`) | Servlet không ném exception mà `sendRedirect` kèm `?error=...`, nên dùng `ArgumentCaptor` bắt URL redirect và so sánh |
| Quy tắc | `soPhongNgu >= 1`, `giaThue >= 0`, mã không rỗng | `id` bắt buộc, `dienTich > 0`, `trangThai` thuộc `DangO/TrongChoThue/BaoTri`, dữ liệu số sai định dạng |
| Phủ null/rỗng | `@CsvSource("''", "'   '")` và test null riêng | Gộp bằng `@NullAndEmptySource` + `@ValueSource` |
| Thêm mới | Không có | Ca sai định dạng: `abc`, `12x`, `1.5`, `6,5` |
| `@Timeout` | Không có | `@Timeout(5s)` cấp class |

**ValidationControllerTest:**

| Điểm | Bản AI | Bản em sửa |
|---|---|---|
| Đối tượng test | `ValidationController` giả định, trả về `boolean` | `util.ValidationUtil` thật, ném `IllegalArgumentException` |
| Cách kiểm tra | `assertEquals(expected, ...)` | `assertThrows` kiểm tra loại exception và message, `assertDoesNotThrow` cho ca hợp lệ |
| Quy tắc | Username 5-20, password >= 8 | Diện tích > 0 (TC_07), CCCD đúng 12 chữ số (TC_09), chỉ số mới >= chỉ số cũ (TC_10), `requireNotBlank` |
| `assertThrows` | Không có | Có, đúng yêu cầu Bước 2.3 |
| `@Timeout` | Không có | Vòng lặp 100.000 lần với `@Timeout(2s)` |

### 4.2. Code hoàn thiện sau khi sửa

Các file cuối cùng nằm tại `src/test/java/com/poly/sof309/controllers/`: `AuthControllerTest.java`, `CanHoControllerTest.java`, `ValidationControllerTest.java`. Đoạn tiêu biểu:

```java
// ValidationControllerTest: @ParameterizedTest + @CsvSource + assertThrows
@ParameterizedTest(name = "[{index}] dienTich={0} -> ném IllegalArgumentException")
@DisplayName("TC_07: diện tích <= 0 ném ngoại lệ")
@CsvSource({ "0", "-10", "-0.5" })
void dienTichAmNemLoi(double dienTich) {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtil.requirePositive(dienTich, "Diện tích"));
    assertEquals("Diện tích phải lớn hơn 0", ex.getMessage());
}

// ValidationControllerTest: @Timeout
@Test
@Timeout(value = 2, unit = TimeUnit.SECONDS)
@DisplayName("Kiểm tra 100.000 lần không bị treo (Timeout 2 giây)")
void khongBiTreo() {
    for (int i = 1; i <= 100_000; i++) {
        ValidationUtil.requirePositive(i, "Giá");
        ValidationUtil.requireChiSoHopLe(i, i + 1);
    }
}

// CanHoControllerTest: Mockito + ArgumentCaptor
private static String redirectCuaServlet(String id, String dienTich, String trangThai) throws Exception {
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse resp = mock(HttpServletResponse.class);
    when(req.getMethod()).thenReturn("POST");
    when(req.getContextPath()).thenReturn(CTX);
    when(req.getParameter("id")).thenReturn(id);
    when(req.getParameter("dienTich")).thenReturn(dienTich);
    when(req.getParameter("trangThai")).thenReturn(trangThai);

    new BQLCanHoUpdateServlet().service(req, resp);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(resp).sendRedirect(captor.capture());
    return captor.getValue();
}
```

### 4.3. Cấu hình và môi trường
- Đổi `pom.xml` sang JUnit 5 (`junit-jupiter`) và thêm Mockito.
- Gộp dependency của web cũ vào `pom.xml` mới (servlet API, BCrypt...).

### 4.4. Rút kinh nghiệm
- Prompt chưa cung cấp code thật (`LoginServlet`, `ValidationUtil`, `BQLCanHoUpdateServlet`) và chưa nêu yêu cầu `@Timeout`, Mockito nên AI phải giả định và phải sửa nhiều. Lần sau em sẽ dán code thật và liệt kê đủ yêu cầu kỹ thuật vào prompt.

## 5. Kết quả
- Lệnh: `mvn test -Dtest=*ControllerTest`
- Kết quả: `Tests run: 76, Failures: 0, Errors: 0, Skipped: 0` - BUILD SUCCESS
- Đã áp dụng: `@ParameterizedTest` + `@CsvSource`, `assertThrows()`, `@Timeout`.
