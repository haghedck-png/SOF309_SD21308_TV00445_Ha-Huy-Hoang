# BỘ TEST CASE CHI TIẾT — HỆ THỐNG QUẢN LÝ CHUNG CƯ

## 1. Bộ Test Case cho UI (JSP / Giao diện người dùng)

| ID | Chức năng / BR | Kỹ thuật | Điều kiện tiên quyết | Thao tác & Dữ liệu kiểm thử | Kết quả mong đợi |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **UI-01** | Đăng nhập (`/login` — `BR01`, `BR03`) | EP | Đang ở trang Login JSP | Bỏ trống Username hoặc Password rồi bấm Đăng nhập | Giữ nguyên trang Login, hiển thị thông báo lỗi bắt buộc nhập rõ ràng ngay trên form |
| **UI-02** | Đăng nhập (`/login` — `BR02`) | EP | Có sẵn 1 tài khoản `Admin` và 1 tài khoản `Resident` active | Đăng nhập lần lượt bằng tài khoản `Admin`, sau đó đăng xuất và đăng nhập bằng `Resident` | `Admin` vào giao diện `/admin/dashboard`; `Resident` vào giao diện `/resident/home` |
| **UI-03** | Đăng nhập tài khoản khóa (`/login` — `BR03`) | EP | Tài khoản có `Status = Inactive` | Nhập đúng thông tin đăng nhập của tài khoản `Inactive` và bấm Đăng nhập | Không vào được hệ thống, hiển thị thông báo tài khoản bị khóa/không hoạt động |
| **UI-04** | Thêm/Sửa căn hộ — Diện tích & Giá (`/admin/apartments` — `BR05`) | EP + BVA | Đang đăng nhập quyền `Admin`, mở form Căn hộ | Thử nhập diện tích/giá: `0`, `-5`, `0.01`, `100` (và nhập chữ `abc`) | Giá trị `> 0` (`0.01`, `100`) được chấp nhận; `0`, `-5`, `abc` hiển thị lỗi yêu cầu số dương hợp lệ trên UI |
| **UI-05** | Thêm/Sửa căn hộ — Trùng mã (`/admin/apartments` — `BR04`) | EP | Đã có căn hộ mã `A101` trên hệ thống | Tạo căn hộ mới với mã `A101` | Hiển thị thông báo lỗi mã căn hộ đã tồn tại, giữ lại các ô đã nhập khác trên form |
| **UI-06** | Cư dân — CCCD & SĐT (`/admin/residents` — `BR07`) | BVA | Đang ở form Thêm/Cập nhật cư dân | Nhập CCCD độ dài `11`, `12`, `13` số; nhập SĐT độ dài `9`, `10`, `11` số | CCCD `12` số và SĐT `10` số hợp lệ; độ dài `11`/`13` (CCCD) và `9`/`11` (SĐT) báo lỗi định dạng ngay trên form |
| **UI-07** | Gán chủ hộ cho căn hộ trống (`/admin/residents/assign-owner` — `BR08`) | EP | Căn hộ `B205` đang ở trạng thái Trống | Chọn cư dân làm chủ hộ cho `B205` và lưu | Sau khi lưu/tải lại danh sách, trạng thái căn hộ `B205` hiển thị `"Đã có người ở"` |
| **UI-08** | Lập hóa đơn điện/nước (`/admin/invoices` — `BR09`) | BVA | Căn hộ có `ChiSoCu = 150` | Nhập `ChiSoMoi` lần lượt là `149`, `150`, `151` trên form lập hóa đơn | `149` báo lỗi chỉ số mới phải `>=` chỉ số cũ; `150` và `151` tính đúng lượng tiêu thụ (`0` và `1`) |

---

## 2. Bộ Test Case cho Controller (Servlet / Action)

| ID | Servlet / Endpoint & BR | Kỹ thuật | Điều kiện tiên quyết | Request / Tham số gửi lên Controller | Kết quả mong đợi |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CTL-01** | `POST /login` (`BR01`, `BR03`) | EP | DB có user `Inactive` và user `Active` | Gửi `username=""`, `password="123"`, hoặc gửi user `Inactive` | Controller từ chối đăng nhập, set message lỗi vào request/session và `forward` về trang login, không tạo session hợp lệ |
| **CTL-02** | `POST /login` (`BR02`) | EP | DB có tài khoản `ADMIN` và `RESIDENT` | Gửi request đăng nhập hợp lệ với từng vai trò | Controller điều hướng (`sendRedirect`) đúng URL `/admin/dashboard` cho `ADMIN` và `/resident/home` cho `RESIDENT` |
| **CTL-03** | `POST /admin/apartments` (`BR04`, `BR05`) | EP + BVA | Session `ADMIN` hợp lệ; căn hộ `A101` đã tồn tại | Gửi `code="A101"` (trùng), hoặc `area=0`, `area=-10`, `baseFee=0`, hoặc biên hợp lệ `area=0.01`, `baseFee=1000` | Dữ liệu vi phạm `BR04`/`BR05` không lưu DB và trả về lỗi hợp lệ; dữ liệu hợp lệ lưu vào SQL Server thành công |
| **CTL-04** | `POST /admin/apartments/delete` (`BR06`) | EP | Session `ADMIN` hợp lệ; có căn hộ `"Đã có người ở"` hoặc có hợp đồng hiệu lực, và căn hộ trống không hợp đồng | Gửi ID căn hộ đang có người ở/hợp đồng hiệu lực vs ID căn hộ đủ điều kiện xóa | Căn hộ đang có người ở/hợp đồng hiệu lực bị chặn xóa và báo lỗi nghiệp vụ; căn hộ hợp lệ được xóa thành công |
| **CTL-05** | `POST /admin/residents` (`BR07`) | EP + BVA | Session `ADMIN` hợp lệ; có cư dân đang hoạt động mang CCCD `012345678901` và SĐT `0912345678` | Gửi CCCD `11`/`12`/`13` số, SĐT `9`/`10`/`11` số, hoặc trùng CCCD/SĐT đang hoạt động | Chặn các trường hợp sai độ dài hoặc trùng dữ liệu đang hoạt động; chỉ lưu khi thỏa `BR07` |
| **CTL-06** | `POST /admin/residents/assign-owner` (`BR08`) | EP | Session `ADMIN` hợp lệ; căn hộ trống tồn tại trong DB | Gửi `residentId` và `apartmentId` gán làm chủ hộ | Controller gọi service cập nhật chủ hộ và chuyển trạng thái căn hộ thành `"Đã có người ở"` trong DB |
| **CTL-07** | `POST /admin/invoices` (`BR09`, `BR10`) | EP + BVA | Session `ADMIN` hợp lệ; căn hộ có `ChiSoCu = 200` và đã có hóa đơn tháng `11/2026` | Thử `ChiSoMoi = 199`, `200`, `201`; thử tạo thêm hóa đơn định kỳ cho cùng căn hộ tháng `11/2026` | `ChiSoMoi = 199` bị từ chối (`BR09`); `200` và `201` hợp lệ; tạo hóa đơn định kỳ thứ 2 cùng tháng/năm bị chặn (`BR10`) |
| **CTL-08** | Phân quyền truy cập Controller (`BR02`) | EP | Đang đăng nhập bằng session `RESIDENT` | Gửi trực tiếp `GET`/`POST` tới các Servlet quản trị (VD: `/admin/apartments`, `/admin/invoices`) | Filter/Controller chặn truy cập trái phép, trả lỗi `403` hoặc chuyển hướng về trang thông báo/trang cá nhân `/resident/home` |
