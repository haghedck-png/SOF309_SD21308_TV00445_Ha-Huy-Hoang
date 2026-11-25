# ĐẶC TẢ HỆ THỐNG QUẢN LÝ CHUNG CƯ

## 1. Giới thiệu chung
* **Tên hệ thống:** Hệ thống Quản lý Chung cư (Apartment Management System)
* **Công nghệ sử dụng:** Java Servlet/JSP, JPA/Hibernate, SQL Server, Apache Tomcat.
* **Mục tiêu:** Số hóa quy trình quản lý căn hộ, cư dân, hợp đồng thuê/mua, hóa đơn dịch vụ (điện, nước, phí quản lý) và tiếp nhận phản ánh từ cư dân.

## 2. Tác nhân hệ thống (Actors)
* **Ban Quản Lý (BQL):** Quản trị hệ thống, sơ đồ căn hộ, tài khoản, nhân sự, thông báo, bình chọn, thống kê.
* **Nhân viên theo bộ phận:** Lễ tân, Kế toán, Kỹ thuật, Bảo vệ (mỗi bộ phận có màn hình riêng).
* **Cư dân:** Xem căn hộ, hóa đơn, đặt tiện ích, gửi phản ánh, bỏ phiếu.

## 3. Đặc tả chức năng & Quy tắc nghiệp vụ (Business Rules)

### 3.1. Quản lý Tài khoản & Đăng nhập
* **Mô tả:** Cho phép BQL, nhân viên và cư dân đăng nhập bằng Tên đăng nhập và Mật khẩu.
* **Quy tắc nghiệp vụ (BR):**
  * `BR01`: Tên đăng nhập và mật khẩu không được để trống.
  * `BR02`: Phân quyền tự động sau khi đăng nhập (BQL vào trang quản lý, cư dân vào trang thông báo, nhân viên vào trang theo bộ phận).
  * `BR03`: Tài khoản bị khóa (Status = Inactive) không được phép đăng nhập.

### 3.2. Quản lý Căn hộ
* **Mô tả:** Xem sơ đồ, cập nhật diện tích và trạng thái căn hộ.
* **Quy tắc nghiệp vụ (BR):**
  * `BR04`: Mã căn hộ (ví dụ: A101, B205) phải là duy nhất trên toàn hệ thống.
  * `BR05`: Diện tích căn hộ và giá thuê/phí dịch vụ cơ bản phải là số dương (`> 0`).
  * `BR06`: Không được xóa căn hộ đang có trạng thái "Đã có người ở" hoặc đang có hợp đồng hiệu lực.

### 3.3. Quản lý Cư dân & Hợp đồng
* **Mô tả:** Lưu trữ thông tin nhân khẩu (Họ tên, CCCD, SĐT, Email) và gắn cư dân vào căn hộ tương ứng.
* **Quy tắc nghiệp vụ (BR):**
  * `BR07`: Số CCCD (12 số) và Số điện thoại (10 số) phải đúng định dạng và không trùng lặp với cư dân khác đang hoạt động.
  * `BR08`: Khi gán cư dân làm chủ hộ của một căn hộ trống, trạng thái căn hộ tự động chuyển thành "Đã có người ở".

### 3.4. Quản lý Hóa đơn & Dịch vụ
* **Mô tả:** Tính tiền điện, nước, phí quản lý hàng tháng theo chỉ số cũ/mới và cập nhật trạng thái thanh toán.
* **Quy tắc nghiệp vụ (BR):**
  * `BR09`: Chỉ số điện/nước tháng mới phải lớn hơn hoặc bằng chỉ số tháng cũ (`ChiSoMoi >= ChiSoCu`).
  * `BR10`: Mỗi căn hộ chỉ được xuất tối đa 01 hóa đơn định kỳ trong cùng một tháng/năm.

## 4. Danh sách màn hình (URL)
Context path khi chạy local: `http://localhost:8080/chungcu_war/`

| Nhóm | URL | Chức năng | Vai trò |
| :--- | :--- | :--- | :--- |
| Chung | `/login`, `/dang-nhap` | Đăng nhập | Tất cả |
| Chung | `/logout` | Đăng xuất | Tất cả |
| BQL | `/banquanly/so-do` | Sơ đồ căn hộ | BQL |
| BQL | `/banquanly/can-ho/cap-nhat` (POST) | Cập nhật diện tích, trạng thái căn hộ | BQL |
| BQL | `/banquanly/can-ho-detail` | Chi tiết căn hộ | BQL |
| BQL | `/banquanly/tai-khoan` | Quản lý tài khoản | BQL |
| BQL | `/banquanly/nhan-su` | Quản lý nhân sự | BQL |
| BQL | `/banquanly/binh-chon` | Quản lý bình chọn | BQL |
| BQL | `/banquanly/thong-bao` | Thông báo | BQL |
| BQL | `/banquanly/dashboard`, `/banquanly/thong-ke` | Thống kê | BQL |
| Lễ tân | `/letan/cu-dan` (+ `/them`, `/sua`, `/chuyen-di`) | Quản lý cư dân | Lễ tân |
| Lễ tân | `/letan/quan-ly-xe`, `/letan/the-tu`, `/letan/su-co` | Xe, thẻ từ, sự cố | Lễ tân |
| Kế toán | `/ketoan/chi-so` (+ `/luu`) | Nhập chỉ số điện, nước | Kế toán |
| Kế toán | `/ketoan/hoa-don` (+ `/chi-tiet`, `/xuat`, `/nhac-phi`) | Lập, xem hóa đơn | Kế toán |
| Kế toán | `/ketoan/thanh-toan`, `/ketoan/doi-soat`, `/ketoan/bieu-gia` | Thanh toán, đối soát, biểu giá | Kế toán |
| Kỹ thuật | `/kythuat/cong-viec`, `/kythuat/lich-su` | Công việc, lịch sử xử lý | Kỹ thuật |
| Bảo vệ | `/baove/dashboard`, `/baove/ca-truc`, `/baove/tuan-tra`, `/baove/tra-cuu` | Ca trực, tuần tra, tra cứu | Bảo vệ |
| Cư dân | `/cudan/thong-bao`, `/cudan/can-ho` | Thông báo, căn hộ | Cư dân |
| Cư dân | `/cudan/hoa-don` (+ `/chi-tiet`, `/thanh-toan-qr`) | Xem, thanh toán hóa đơn | Cư dân |
| Cư dân | `/cudan/phan-anh`, `/cudan/tien-ich` | Phản ánh, đặt tiện ích | Cư dân |

Điều hướng sau đăng nhập (hàm `LoginServlet.calculateRedirectUrl`): Cư dân → `/cudan/thong-bao`; BQL → `/banquanly/binh-chon`; Nhân viên Lễ tân → `/letan/su-co`, Kỹ thuật → `/kythuat/cong-viec`, Kế toán → `/ketoan/chi-so`, Bảo vệ → `/baove/dashboard`; vai trò hoặc bộ phận không xác định → `/dang-nhap` hoặc `/index.jsp`.

## 5. Trường dữ liệu & ràng buộc validation

### 5.1. Đăng nhập (`POST /login`)
| Trường | Tham số | Ràng buộc | Thông báo lỗi |
| :--- | :--- | :--- | :--- |
| Tên đăng nhập | `tenDangNhap` | Bắt buộc, không chỉ có khoảng trắng | "Vui lòng nhập đầy đủ ..." |
| Mật khẩu | `matKhau` | Bắt buộc, so khớp BCrypt | "Vui lòng nhập đầy đủ ..." |

### 5.2. Căn hộ (`POST /banquanly/can-ho/cap-nhat`)
| Trường | Tham số | Ràng buộc | Thông báo lỗi |
| :--- | :--- | :--- | :--- |
| Mã căn hộ (id) | `id` | Bắt buộc, là số nguyên; thiếu thì quay về sơ đồ | "Dữ liệu nhập vào không đúng định dạng." |
| Diện tích | `dienTich` | Bắt buộc, số, `> 0` | "Diện tích căn hộ phải là số dương (> 0)." |
| Trạng thái | `trangThai` | Chỉ nhận `DangO`, `TrongChoThue`, `BaoTri` (phân biệt hoa thường) | "Trạng thái căn hộ không hợp lệ." |

### 5.3. Cư dân (`POST /letan/cu-dan/them`, `/sua`)
| Trường | Tham số | Ràng buộc | Thông báo lỗi |
| :--- | :--- | :--- | :--- |
| Họ tên | `hoTen` | Bắt buộc, tối đa 255 ký tự | "Họ và tên cư dân không được để trống." |
| CCCD | `cccd` | Đúng 12 chữ số, không trùng cư dân đang ở | "Số CCCD phải bao gồm đúng 12 chữ số." / "...đã được đăng ký bởi một cư dân đang ở khác." |
| Số điện thoại | `soDienThoai` | 10 số theo BR07 (cần kiểm tra lại code có kiểm tra không) | |
| Loại cư dân | `loaiCuDan` | Chỉ `ChuHo` hoặc `KhachThue` | "Loại cư dân không hợp lệ ..." |
| Ngày chuyển đến | `ngayChuyenDen` | Không được sau ngày hiện tại | "Ngày chuyển đến không được vượt quá ngày hiện tại." |

### 5.4. Chỉ số điện, nước (`POST /ketoan/chi-so/luu`)
| Trường | Tham số | Ràng buộc | Thông báo lỗi |
| :--- | :--- | :--- | :--- |
| Tháng | `thang` | 1 đến 12 | |
| Chỉ số điện | `chiSoDien_<maCanHo>` | Số, không âm, `>=` chỉ số kỳ trước (BR09) | "Chỉ số mới không được nhỏ hơn 0." |
| Chỉ số nước | `chiSoNuoc_<maCanHo>` | Số, không âm, `>=` chỉ số kỳ trước (BR09) | như trên |
