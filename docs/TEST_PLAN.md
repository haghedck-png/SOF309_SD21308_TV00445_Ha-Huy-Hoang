# TEST PLAN — HỆ THỐNG QUẢN LÝ CHUNG CƯ

## 1. Mục tiêu & Phạm vi kiểm thử
* **Mục tiêu:** Xác minh toàn bộ quy tắc nghiệp vụ (`BR01`–`BR10`) hoạt động chính xác giữa giao diện JSP/UI và tầng Servlet/Controller xử lý request, đảm bảo không rò rỉ lỗi ngoại lệ ra người dùng cuối và dữ liệu trong SQL Server luôn nhất quán.
* **Phạm vi kiểm thử (In-Scope):**
  * **UI Layer (JSP / Form / Thông báo):** Form đăng nhập (`/login`), màn hình danh sách/thêm/sửa/xóa căn hộ (`/admin/apartments`), form cư dân & gán chủ hộ (`/admin/residents`), form lập hóa đơn điện/nước/phí quản lý (`/admin/invoices`), hiển thị lỗi validate và thông báo kết quả xử lý.
  * **Controller Layer (Servlet / Filter / Handler):** Tiếp nhận HTTP Request (`GET`, `POST`), đọc tham số từ `HttpServletRequest`, kiểm tra session & phân quyền `Admin` / `User (Resident)`, gọi Service/JPA, bắt lỗi ràng buộc dữ liệu/nghiệp vụ và `forward` hoặc `redirect` đúng trang.
* **Ngoài phạm vi (Out-of-Scope):** Kiểm thử chịu tải trên Apache Tomcat, kiểm thử bảo mật chuyên sâu, tối ưu chỉ mục/execution plan trên SQL Server.

## 2. Chiến lược áp dụng kỹ thuật thiết kế test
* **Phân vùng tương đương (Equivalence Partitioning - EP):** Phân chia các đầu vào (tên đăng nhập/email, mật khẩu, mã căn hộ, diện tích, giá thuê/phí, CCCD, SĐT, chỉ số điện/nước, kỳ tháng/năm) thành các lớp **Hợp lệ (Valid EP)** và **Không hợp lệ (Invalid EP)** tương ứng với từng `BR`.
* **Phân tích giá trị biên (Boundary Value Analysis - BVA):** Kiểm thử sát các điểm biên bắt buộc trong đặc tả như độ dài chuỗi (`CCCD = 12 số`, `SĐT = 10 số`, `Mã căn hộ`), giá trị dương tối thiểu (`> 0`), và so sánh biên của chỉ số điện/nước (`ChiSoMoi` so với `ChiSoCu`) để bắt lỗi điều kiện so sánh (`>` thay vì `>=` ở `BR09` hoặc off-by-one độ dài chuỗi ở `BR07`).

## 3. Phân tích EP & BVA theo Quy tắc nghiệp vụ (`BR01`–`BR10`)

| BR / Trường dữ liệu | Phân vùng tương đương hợp lệ (Valid EP) | Phân vùng tương đương không hợp lệ (Invalid EP) | Giá trị biên trọng yếu (BVA) |
| :--- | :--- | :--- | :--- |
| **BR01 — Tài khoản & Mật khẩu** | Username/Email và Password có nội dung hợp lệ | Để trống (`""`), chỉ chứa khoảng trắng (`"   "`), `null` | Độ dài chuỗi `0`, `1`, `2` ký tự (khi kiểm tra rỗng/tối thiểu) |
| **BR04 — Mã căn hộ** | Mã chưa tồn tại trong hệ thống, định dạng hợp lệ (VD: `A101`, `B205`) | Mã đã tồn tại trên hệ thống, rỗng, chứa ký tự không hợp lệ/quá dài | Độ dài sát biên `2`, `3`, `4` và `9`, `10`, `11` ký tự |
| **BR05 — Diện tích & Giá thuê/Phí cơ bản** | Số thực/số dương `> 0` | Bằng `0`, số âm (`< 0`), chuỗi ký tự, để trống | `0`, `0.01`, `1.0` (diện tích); `0`, `1`, `1000` (giá thuê/phí) |
| **BR07 — Số CCCD** | Chuỗi đúng `12` chữ số, chưa trùng với cư dân đang hoạt động | `< 12` số, `> 12` số, chứa chữ/ký tự đặc biệt, trùng CCCD cư dân đang hoạt động | Độ dài `11`, `12`, `13` ký tự số |
| **BR07 — Số điện thoại** | Chuỗi đúng `10` chữ số, chưa trùng với cư dân đang hoạt động | `< 10` số, `> 10` số, chứa chữ/ký tự đặc biệt, trùng SĐT cư dân đang hoạt động | Độ dài `9`, `10`, `11` ký tự số |
| **BR09 — Chỉ số điện/nước** | Số nguyên `>= ChiSoCu` | `< ChiSoCu`, số âm, rỗng, không phải số nguyên | `ChiSoCu - 1`, `ChiSoCu`, `ChiSoCu + 1` |
| **BR10 — Kỳ hóa đơn định kỳ** | Chưa có hóa đơn định kỳ cho căn hộ trong `(Thang, Nam)` đó | Đã tồn tại hóa đơn định kỳ cho cùng căn hộ trong `(Thang, Nam)` | Tháng `1`, `12` và các tháng liền kề cùng căn hộ trong cùng năm |

## 4. Tiêu chí Vào / Ra (Entry & Exit Criteria)
* **Entry Criteria:** Ứng dụng deploy thành công trên Apache Tomcat, kết nối SQL Server ổn định, có sẵn dữ liệu test (tài khoản `Admin` active/inactive, tài khoản `Resident`, căn hộ trống/đã có người ở/có hợp đồng hiệu lực, hóa đơn mẫu).
* **Exit Criteria:** 100% test case ánh xạ trực tiếp `BR01`–`BR10` được chạy và đạt `Pass`; không còn lỗi `Blocker`/`Critical` ảnh hưởng luồng đăng nhập, phân quyền, dữ liệu tài chính/căn hộ.
