# Nhật ký AI 01: Thiết kế Test Plan và Test Case (Y2)

> CÁC MỤC CÓ [ĐIỀN] LÀ PHẦN BẠN PHẢI TỰ DÁN BẰNG NỘI DUNG THẬT. Xóa dòng này khi nộp.

## 1. Ngữ cảnh & Mục tiêu
- Công cụ: Claude (Anthropic).
- Cần làm: sinh `docs/TEST_PLAN.md` và `docs/TEST_CASES.md` cho Hệ thống Quản lý Chung cư (Servlet/JSP, SQL Server), dựa trên `docs/SPECIFICATIONS.md`.
- Kỹ thuật yêu cầu: Phân vùng tương đương và Phân tích giá trị biên.
- Phạm vi: đăng nhập và phân quyền, căn hộ, cư dân, hóa đơn.

## 2. Câu lệnh đã gửi cho AI (Prompt)
> [ĐIỀN prompt thật. Nếu bạn dùng đúng mẫu của thầy thì ghi:]
> "Bạn là QA Lead. Dưới đây là đặc tả nghiệp vụ dự án Web của tôi: [nội dung SPECIFICATIONS.md]. Hãy áp dụng kỹ thuật Phân vùng tương đương và Phân tích giá trị biên để thiết kế Test Plan và Bộ Test Case chi tiết cho Controller và UI."

## 3. Kết quả do AI sinh ra (Raw Output)
[ĐIỀN: dán nguyên văn câu trả lời gốc của AI, chưa chỉnh sửa]

## 4. Chỉnh sửa của Sinh viên & Lý do sửa
Kết quả cuối cùng nằm ở `docs/TEST_PLAN.md` (5 mục: mục tiêu, phạm vi, chiến lược, môi trường, tiêu chí nghiệm thu) và `docs/TEST_CASES.md` (10 test case TC_01 đến TC_10 chia 3 module).
- Những điểm AI làm sai hoặc thiếu: [ĐIỀN. Gợi ý nếu đúng với bạn: AI dùng URL `/admin/can-ho` và mã căn hộ `A101` trong khi web thật dùng `/banquanly/so-do` và căn hộ gồm số tầng, số phòng; AI chưa có test case cho tài khoản bị khóa (BR03) và hóa đơn trùng tháng (BR10).]
- Em đã sửa: [ĐIỀN]
- Kỹ thuật áp dụng trong test case: giá trị biên diện tích (`0`, `-10`), CCCD sai định dạng (`12345abc`), chỉ số nước mới nhỏ hơn cũ (`140` so với `150`).

## 5. Kết quả
- `TEST_PLAN.md` và `TEST_CASES.md` đã lưu trong `docs/`.
