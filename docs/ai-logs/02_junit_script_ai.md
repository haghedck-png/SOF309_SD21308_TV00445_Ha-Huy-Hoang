# Nhật ký AI 02: Sinh Unit Test JUnit 5 cho Controller (Y3)

> CÁC MỤC CÓ [ĐIỀN] LÀ PHẦN BẠN PHẢI TỰ DÁN BẰNG NỘI DUNG THẬT. Xóa dòng này khi nộp.

## 1. Ngữ cảnh & Mục tiêu
- Công cụ: Claude (Anthropic).
- Cần làm: viết Unit Test cho Controller (Servlet) bằng JUnit 5 và Mockito, chạy được mà không cần database, bám theo TC_03, TC_05, TC_07, TC_09, TC_10.
- Yêu cầu kỹ thuật: `@ParameterizedTest` + `@CsvSource`, `assertThrows`, `@Timeout`.

## 2. Câu lệnh đã gửi cho AI (Prompt)
1. [ĐIỀN] Dán hướng dẫn Tuần 2 của thầy và ảnh `pom.xml`, nhờ hướng dẫn cấu hình JUnit 5 và Mockito.
2. [ĐIỀN] Gửi source web, nhờ chọn các hàm test được mà không cần database.
3. [ĐIỀN: thêm các prompt khác nếu có]

## 3. Kết quả do AI sinh ra (Raw Output)
[ĐIỀN: dán code test gốc AI sinh ra]

AI đề xuất 3 file test:

| File test | Đối tượng test | Kỹ thuật |
| :--- | :--- | :--- |
| `AuthControllerTest` | `LoginServlet.calculateRedirectUrl`, `LoginServlet.doPost` (bỏ trống), `PasswordUtil` | `@ParameterizedTest` + `@CsvSource`, Mockito, `@Timeout` |
| `CanHoControllerTest` | `BQLCanHoUpdateServlet` (diện tích, trạng thái, định dạng) | `@ParameterizedTest`, `@NullAndEmptySource`, Mockito |
| `ValidationControllerTest` | `util.ValidationUtil` (TC_07, TC_09, TC_10) | `assertThrows`, `@ParameterizedTest`, `@Timeout` |

## 4. Chỉnh sửa của Sinh viên & Lý do sửa
- Đổi `pom.xml` sang JUnit 5 và thêm Mockito; gộp dependency của web cũ (Hibernate, JSTL, SQL Server, BCrypt).
- Tạo `util/ValidationUtil.java` trong `src/main` để có hàm validate thuần Java, test được TC_07, TC_09, TC_10 mà không cần database. [ĐIỀN: nói rõ lý do, và nếu đã gọi hàm này trong servlet thì ghi thêm.]
- Chỉ test các nhánh validate chạy trước khi gọi DAO, vì không có database khi chạy test.
- Giữ `SampleControllerTest` của thầy làm file mẫu, không tính vào số test của em.
- [ĐIỀN: những chỗ AI sinh sai và em đã sửa, ví dụ lỗi biên dịch, assert sai, thông báo lỗi không khớp code thật.]

## 5. Kết quả
- `mvn test -Dtest=*ControllerTest`: 76 test, 0 lỗi, 0 bỏ qua (gồm 2 test mẫu của `SampleControllerTest`; 74 test do em viết).
- Số test từng file theo báo cáo Surefire: `AuthControllerTest` 31, `CanHoControllerTest` 20, `ValidationControllerTest` 23.
