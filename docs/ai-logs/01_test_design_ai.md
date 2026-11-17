# AI LOG 01 - Thiết kế ca kiểm thử với AI (Y3)

> Bản nháp. Sửa lại cho đúng cách em đã làm thật trước khi nộp.

## 1. Công cụ
- Claude (Anthropic)

## 2. Prompt đã dùng
1. Dán hướng dẫn Tuần 2 của thầy + ảnh pom.xml, nhờ AI hướng dẫn cấu hình JUnit 5 + Mockito.
2. Gửi source web (DuAnQLCC) và repo, nhờ AI chọn các hàm test được mà không cần database.

## 3. Kết quả AI đề xuất
| File test | Đối tượng test | Kỹ thuật |
| :--- | :--- | :--- |
| AuthControllerTest | LoginServlet.calculateRedirectUrl, LoginServlet.doPost (bỏ trống), PasswordUtil | @ParameterizedTest + @CsvSource, Mockito, @Timeout |
| CanHoControllerTest | BQLCanHoUpdateServlet (validate diện tích, trạng thái, định dạng) | @ParameterizedTest, Mockito |
| ValidationControllerTest | util.ValidationUtil (TC_07, TC_09, TC_10) | assertThrows, @ParameterizedTest, @Timeout |

## 4. Em đã kiểm tra / chỉnh sửa
- đã đổi pom sang JUnit 5 và thêm Mockito
- đã gộp dependency của web cũ
- đã chạy mvn test -Dtest=*ControllerTest và 76 test pass
## 5. Kết quả
- `mvn test -Dtest=*ControllerTest`: Tests run: 76, Failures: 0, Errors: 0, Skipped: 0
