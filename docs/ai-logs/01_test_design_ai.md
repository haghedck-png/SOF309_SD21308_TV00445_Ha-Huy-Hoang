

## Nhật kí AI Y2

## 1. Ngữ cảnh & Mục tiêu
- Công cụ: gemini.
- Cần làm: sinh `docs/TEST_PLAN.md` và `docs/TEST_CASES.md` cho Hệ thống Quản lý Chung cư (Servlet/JSP, SQL Server), dựa trên `docs/SPECIFICATIONS.md`.
- Kỹ thuật yêu cầu: Phân vùng tương đương và Phân tích giá trị biên.
- Phạm vi: đăng nhập và phân quyền, căn hộ, cư dân, hóa đơn.

## 2. Câu lệnh đã gửi cho AI (Prompt)
Bạn là QA Lead. Dưới đây là đặc tả nghiệp vụ dự án Web của tôi: Quản lý chung cư. Hãy áp dụng kỹ thuật Phân vùng tương đương và Phân tích giá trị biên để thiết kế Test Plan và Bộ Test Case chi tiết cho Controller và UI.

## 3. Kết quả do AI sinh ra (Raw Output)

### 1.1. Mục tiêu & Phạm vi kiểm thử
* **Mục tiêu:** Xác minh toàn bộ quy tắc nghiệp vụ (`BR01`–`BR10`) hoạt động chính xác giữa giao diện JSP/UI và tầng Servlet/Controller xử lý request, đảm bảo không rò rỉ lỗi ngoại lệ ra người dùng cuối và dữ liệu trong SQL Server luôn nhất quán.
* **Phạm vi kiểm thử (In-Scope):**
  * **UI Layer (JSP / Form / Thông báo):** Form đăng nhập, màn hình danh sách/thêm/sửa/xóa căn hộ, form cư dân & gán chủ hộ, form lập hóa đơn điện/nước/phí quản lý, hiển thị lỗi validate và thông báo kết quả xử lý.
  * **Controller Layer (Servlet / Filter / Handler):** Tiếp nhận HTTP Request (`GET`, `POST`), đọc tham số từ `HttpServletRequest`, kiểm tra session & phân quyền `Admin` / `User (Resident)`, gọi Service/JPA, bắt lỗi ràng buộc dữ liệu/nghiệp vụ và `forward` hoặc `redirect` đúng trang.
* **Ngoài phạm vi (Out-of-Scope):** Kiểm thử chịu tải trên Apache Tomcat, kiểm thử bảo mật chuyên sâu, tối ưu chỉ mục/execution plan trên SQL Server.

### 1.2. Chiến lược áp dụng kỹ thuật thiết kế test
* **Phân vùng tương đương (Equivalence Partitioning - EP):** Phân chia các đầu vào (tên đăng nhập/email, mật khẩu, mã căn hộ, diện tích, giá thuê/phí, CCCD, SĐT, chỉ số điện/nước, kỳ tháng/năm) thành các lớp **Hợp lệ (Valid EP)** và **Không hợp lệ (Invalid EP)** tương ứng với từng `BR`.
* **Phân tích giá trị biên (Boundary Value Analysis - BVA):** Kiểm thử sát các điểm biên bắt buộc trong đặc tả như độ dài chuỗi (`CCCD = 12 số`, `SĐT = 10 số`, `Mã căn hộ`), giá trị dương tối thiểu (`> 0`), và so sánh biên của chỉ số điện/nước (`ChiSoMoi` so với `ChiSoCu`) để bắt lỗi điều kiện so sánh (`>` thay vì `>=` ở `BR09` hoặc off-by-one độ dài chuỗi ở `BR07`).

### 1.3. Tiêu chí Vào / Ra (Entry & Exit Criteria)
* **Entry Criteria:** Ứng dụng deploy thành công trên Apache Tomcat, kết nối SQL Server ổn định, có sẵn dữ liệu test (tài khoản `Admin` active/inactive, tài khoản `Resident`, căn hộ trống/đã có người ở/có hợp đồng hiệu lực, hóa đơn mẫu).
* **Exit Criteria:** 100% test case ánh xạ trực tiếp `BR01`–`BR10` được chạy và đạt `Pass`; không còn lỗi `Blocker`/`Critical` ảnh hưởng luồng đăng nhập, phân quyền, dữ liệu tài chính/căn hộ.

---

## 2. Phân tích EP & BVA theo Quy tắc nghiệp vụ (`BR01`–`BR10`)

| BR / Trường dữ liệu | Phân vùng tương đương hợp lệ (Valid EP) | Phân vùng tương đương không hợp lệ (Invalid EP) | Giá trị biên trọng yếu (BVA) |
| :--- | :--- | :--- | :--- |
| **BR01 — Tài khoản & Mật khẩu** | Username/Email và Password có nội dung hợp lệ | Để trống (`""`), chỉ chứa khoảng trắng (`"   "`), `null` | Độ dài chuỗi `0`, `1`, `2` ký tự (khi kiểm tra rỗng/tối thiểu) |
| **BR04 — Mã căn hộ** | Mã chưa tồn tại trong hệ thống, định dạng hợp lệ (VD: `A101`, `B205`) | Mã đã tồn tại trên hệ thống, rỗng, chứa ký tự không hợp lệ/quá dài | Độ dài sát biên theo cấu hình DB/UI (VD: `2`, `3`, `4` và `max-1`, `max`, `max+1`) |
| **BR05 — Diện tích & Giá thuê/Phí cơ bản** | Số thực/số dương `> 0` | Bằng `0`, số âm (`< 0`), chuỗi ký tự, để trống | `0`, `0.01` (hoặc `0.1` tùy kiểu số), `1.0`, giá trị trần nghiệp vụ và `max + 0.01` |
| **BR07 — Số CCCD** | Chuỗi đúng `12` chữ số, chưa trùng với cư dân đang hoạt động | `< 12` số, `> 12` số, chứa chữ/ký tự đặc biệt, trùng CCCD cư dân đang hoạt động | Độ dài `11`, `12`, `13` ký tự số |
| **BR07 — Số điện thoại** | Chuỗi đúng `10` chữ số, chưa trùng với cư dân đang hoạt động | `< 10` số, `> 10` số, chứa chữ/ký tự đặc biệt, trùng SĐT cư dân đang hoạt động | Độ dài `9`, `10`, `11` ký tự số |
| **BR09 — Chỉ số điện/nước** | Số nguyên `>= ChiSoCu` | `< ChiSoCu`, số âm, rỗng, không phải số nguyên | `ChiSoCu - 1`, `ChiSoCu`, `ChiSoCu + 1` |
| **BR10 — Kỳ hóa đơn định kỳ** | Chưa có hóa đơn định kỳ cho căn hộ trong `(Thang, Nam)` đó | Đã tồn tại hóa đơn định kỳ cho cùng căn hộ trong `(Thang, Nam)` | Tháng `1`, `12` và các tháng liền kề cùng căn hộ trong cùng năm |

---

## 3. Bộ Test Case chi tiết cho UI (JSP / Giao diện người dùng)

| ID | Chức năng / BR | Kỹ thuật | Điều kiện tiên quyết | Thao tác & Dữ liệu kiểm thử | Kết quả mong đợi |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **UI-01** | Đăng nhập (`BR01`, `BR03`) | EP | Đang ở trang Login JSP | Bỏ trống Username hoặc Password rồi bấm Đăng nhập | Giữ nguyên trang Login, hiển thị thông báo lỗi bắt buộc nhập rõ ràng ngay trên form |
| **UI-02** | Đăng nhập (`BR02`) | EP | Có sẵn 1 tài khoản `Admin` và 1 tài khoản `Resident` active | Đăng nhập lần lượt bằng tài khoản `Admin`, sau đó đăng xuất và đăng nhập bằng `Resident` | `Admin` vào giao diện quản trị; `Resident` vào giao diện thông tin cá nhân |
| **UI-03** | Đăng nhập tài khoản khóa (`BR03`) | EP | Tài khoản có `Status = Inactive` | Nhập đúng thông tin đăng nhập của tài khoản `Inactive` và bấm Đăng nhập | Không vào được hệ thống, hiển thị thông báo tài khoản bị khóa/không hoạt động |
| **UI-04** | Thêm/Sửa căn hộ — Diện tích & Giá (`BR05`) | EP + BVA | Đang đăng nhập quyền `Admin`, mở form Căn hộ | Thử nhập diện tích/giá: `0`, `-5`, `0.01`, `100` (và nhập chữ `abc`) | Giá trị `> 0` (`0.01`, `100`) được chấp nhận; `0`, `-5`, `abc` hiển thị lỗi yêu cầu số dương hợp lệ trên UI |
| **UI-05** | Thêm/Sửa căn hộ — Trùng mã (`BR04`) | EP | Đã có căn hộ mã `A101` trên hệ thống | Tạo căn hộ mới với mã `A101` (hoặc `a101` nếu hệ thống phân biệt/chuẩn hóa) | Hiển thị thông báo lỗi mã căn hộ đã tồn tại, giữ lại các ô đã nhập khác trên form |
| **UI-06** | Cư dân — CCCD & SĐT (`BR07`) | BVA | Đang ở form Thêm/Cập nhật cư dân | Nhập CCCD độ dài `11`, `12`, `13` số; nhập SĐT độ dài `9`, `10`, `11` số | CCCD `12` số và SĐT `10` số hợp lệ; độ dài `11`/`13` (CCCD) và `9`/`11` (SĐT) báo lỗi định dạng ngay trên form |
| **UI-07** | Gán chủ hộ cho căn hộ trống (`BR08`) | EP | Căn hộ `B205` đang ở trạng thái Trống | Chọn cư dân làm chủ hộ cho `B205` và lưu | Sau khi lưu/tải lại danh sách, trạng thái căn hộ `B205` hiển thị "Đã có người ở" |
| **UI-08** | Lập hóa đơn điện/nước (`BR09`) | BVA | Căn hộ có `ChiSoCu = 150` | Nhập `ChiSoMoi` lần lượt là `149`, `150`, `151` trên form lập hóa đơn | `149` báo lỗi chỉ số mới phải `>=` chỉ số cũ; `150` và `151` tính đúng lượng tiêu thụ (`0` và `1`) |

---

## 4. Bộ Test Case chi tiết cho Controller (Servlet / Action)

| ID | Servlet / Endpoint & BR | Kỹ thuật | Điều kiện tiên quyết | Request / Tham số gửi lên Controller | Kết quả mong đợi |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CTL-01** | `POST /login` (`BR01`, `BR03`) | EP | DB có user `Inactive` và user `Active` | Gửi `username=""`, `password="123"`, hoặc gửi user `Inactive` | Controller từ chối đăng nhập, set message lỗi vào request/session và `forward` về trang login, không tạo session hợp lệ |
| **CTL-02** | `POST /login` (`BR02`) | EP | DB có tài khoản `ADMIN` và `RESIDENT` | Gửi request đăng nhập hợp lệ với từng vai trò | Controller điều hướng (`sendRedirect`) đúng URL trang quản trị cho `ADMIN` và trang cá nhân cho `RESIDENT` |
| **CTL-03** | `POST /admin/apartments` (`BR04`, `BR05`) | EP + BVA | Session `ADMIN` hợp lệ; căn hộ `A101` đã tồn tại | Gửi `code="A101"` (trùng), hoặc `area=0`, `area=-10`, `baseFee=0`, hoặc biên hợp lệ `area=0.01`, `baseFee=1000` | Dữ liệu vi phạm `BR04`/`BR05` không lưu DB và trả về lỗi hợp lệ; dữ liệu hợp lệ lưu vào SQL Server thành công |
| **CTL-04** | `POST /admin/apartments/delete` (`BR06`) | EP | Session `ADMIN` hợp lệ; có căn hộ "Đã có người ở" hoặc có hợp đồng hiệu lực, và căn hộ trống không hợp đồng | Gửi ID căn hộ đang có người ở/hợp đồng hiệu lực vs ID căn hộ đủ điều kiện xóa | Căn hộ đang có người ở/hợp đồng hiệu lực bị chặn xóa và báo lỗi nghiệp vụ; căn hộ hợp lệ được xóa thành công |
| **CTL-05** | `POST /admin/residents` (`BR07`) | EP + BVA | Session `ADMIN` hợp lệ; có cư dân đang hoạt động mang CCCD `012345678901` và SĐT `0912345678` | Gửi CCCD `11`/`12`/`13` số, SĐT `9`/`10`/`11` số, hoặc trùng CCCD/SĐT đang hoạt động | Chặn các trường hợp sai độ dài hoặc trùng dữ liệu đang hoạt động; chỉ lưu khi thỏa `BR07` |
| **CTL-06** | `POST /admin/residents/assign-owner` (`BR08`) | EP | Session `ADMIN` hợp lệ; căn hộ trống tồn tại trong DB | Gửi `residentId` và `apartmentId` gán làm chủ hộ | Controller gọi service cập nhật chủ hộ và chuyển trạng thái căn hộ thành `"Đã có người ở"` trong DB |
| **CTL-07** | `POST /admin/invoices` (`BR09`, `BR10`) | EP + BVA | Session `ADMIN` hợp lệ; căn hộ có `ChiSoCu = 200` và đã có hóa đơn tháng `11/2026` | Thử `ChiSoMoi = 199`, `200`, `201`; thử tạo thêm hóa đơn định kỳ cho cùng căn hộ tháng `11/2026` | `ChiSoMoi = 199` bị từ chối (`BR09`); `200` và `201` hợp lệ; tạo hóa đơn định kỳ thứ 2 cùng tháng/năm bị chặn (`BR10`) |
| **CTL-08** | Phân quyền truy cập Controller (`BR02`) | EP | Đang đăng nhập bằng session `RESIDENT` | Gửi trực tiếp `GET`/`POST` tới các Servlet quản trị (VD: `/admin/apartments`, `/admin/invoices`) | Filter/Controller chặn truy cập trái phép, trả lỗi `403` hoặc chuyển hướng về trang thông báo/trang cá nhân của `RESIDENT` |

## 4. Chỉnh sửa của Sinh viên & Lý do sửa
- **Những điểm AI làm sai hoặc thiếu:**
  - Bản gốc dùng vai trò `MANAGER` và endpoint REST `/api/...`, trong khi dự án sử dụng kiến trúc **Java Servlet/JSP, JPA/Hibernate, SQL Server, Apache Tomcat** với hai vai trò **Ban Quản Lý (Admin)** và **Cư dân (Resident)**.
  - Một số ràng buộc và quy tắc nghiệp vụ chưa bám sát `SPECIFICATIONS.md` (chưa kiểm tra đủ `BR01`–`BR10`, như số CCCD 12 số, số điện thoại 10 số, không xóa căn hộ đang có người ở/có hợp đồng hiệu lực, chỉ số mới `>=` chỉ số cũ và tối đa 01 hóa đơn định kỳ/tháng/năm).
  - Chưa tách riêng kết quả thành hai file `docs/TEST_PLAN.md` và `docs/TEST_CASES.md` theo cấu trúc thư mục dự án.
- **Code / Tài liệu hoàn thiện sau khi bạn sửa:**
  - Cập nhật lại `docs/TEST_PLAN.md` và `docs/TEST_CASES.md` cho khớp các URL màn hình (`/login`, `/admin/dashboard`, `/admin/apartments`, `/admin/residents`, `/admin/invoices`, `/resident/home`) và toàn bộ quy tắc `BR01`–`BR10` trong `docs/SPECIFICATIONS.md`.

## 5. Kết quả
- `TEST_PLAN.md` và `TEST_CASES.md` đã lưu trong `docs/`.
