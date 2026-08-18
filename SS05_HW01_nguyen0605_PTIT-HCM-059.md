# BÁO CÁO PHÂN TÍCH VÀ LỰA CHỌN THIẾT KẾ MÔ TẢ TOOL (METADATA DESCRIPTION) TỐI ƯU

## Phần 1: Tiêu đề bài tập & Tóm tắt yêu cầu
- **Tên bài tập:** BÀI 1: Phân tích & Lựa chọn - Thiết kế mô tả Tool (Metadata Description) tối ưu (Mức độ Khá)
- **Tóm tắt yêu cầu:** Đề bài đưa ra 3 phương án cấu hình siêu dữ liệu mô tả (Metadata Description) cho các công cụ (Tools/Functions) là `getRoomAvailability` và `calculateTotalPrice` trong hệ thống AI Booking Agent (Spring AI). Sinh viên cần:
  1. Lựa chọn phương án tối ưu nhất.
  2. Phân tích lập luận kỹ thuật chi tiết tại sao phương án đó tối ưu dựa trên cơ chế hoạt động Function Calling.
  3. Chỉ ra nhược điểm và rủi ro cụ thể của các phương án bị loại trừ.

---

## Phần 2: Giả lập cuộc đối thoại thực tế với AI

### 1. Câu lệnh Prompt gửi cho AI để giải quyết bài toán:
> "Chào AI, tôi đang phát triển một hệ thống AI Booking Agent bằng Spring AI. Tôi có 2 tools chính: `getRoomAvailability` (tra cứu phòng trống) và `calculateTotalPrice` (tính tiền phòng). Hãy phân tích và lựa chọn phương án tối ưu nhất giữa 3 phương án A (Mô tả tối giản), B (Chi tiết, tường minh và quy định điều kiện gọi), C (Mô tả kỹ thuật nội bộ code/DB). Hãy phân tích dựa trên cơ chế Function Calling của LLM và cấu trúc Spring AI. Đồng thời, hãy xây dựng toàn bộ mã nguồn của một ứng dụng Spring Boot hoàn chỉnh sử dụng phương án tối ưu đã chọn."

### 2. Phản hồi tóm tắt của AI:
- **Lựa chọn:** Phương án B.
- **Lý do lựa chọn:** Phương án B cung cấp đầy đủ thông tin về kiểu dữ liệu, định dạng (`yyyy-MM-dd`), ví dụ cụ thể (`Deluxe`, `Standard`), điều kiện ràng buộc (`numberOfDays > 0`), và đặc biệt là chỉ ra mối quan hệ phụ thuộc giữa hai công cụ (thứ tự gọi hàm). Điều này giúp LLM ra quyết định chính xác, trích xuất tham số không bị lỗi định dạng và tối ưu hóa số lượng API call.
- **Lý do loại trừ A:** Quá sơ sài, LLM dễ truyền thiếu tham số, sai định dạng ngày tháng, hoặc gọi nhầm hàm.
- **Lý do loại trừ C:** Đưa các thông tin chi tiết kỹ thuật không cần thiết (MySQL, JPA, class) vào mô tả. LLM không thể tương tác trực tiếp với Database ở tầng này mà chỉ cần biết API Contract. Việc này làm lãng phí token (context length) và làm nhiễu cơ chế Attention của mô hình.
- **Cung cấp mã nguồn:** Thiết lập cấu hình Spring Boot với Spring AI, sử dụng Jackson Annotations để định nghĩa tham số chính xác và implement cấu trúc dự án chuẩn.

---

## Phần 3: Nội dung phân tích kỹ thuật chi tiết

### Phương án lựa chọn: Phương án B (Mô tả chi tiết và tường minh)

#### Lập luận kỹ thuật chi tiết:
1. **Định nghĩa định dạng dữ liệu (Formatting Contract):**
   - LLM hiểu ngôn ngữ tự nhiên cực tốt nhưng khi chuyển đổi sang JSON (Function Call payload), nó cần định dạng cực kỳ nghiêm ngặt. Việc ghi rõ `yyyy-MM-dd` giúp loại bỏ hoàn toàn sự mập mờ. Nếu người dùng nói "tôi muốn đặt phòng từ thứ Hai tới thứ Sáu tuần sau", LLM có thể tự động tính toán chính xác ngày dương lịch và chuyển đổi về dạng `yyyy-MM-dd` thay vì truyền text thô.
2. **Ràng buộc workflow và phụ thuộc giữa các Tool (Tool Dependency):**
   - Trong mô tả của `calculateTotalPrice`: *"Công cụ này chỉ được gọi sau khi đã xác định được loại phòng và tổng số ngày lưu trú"*. Đây là chỉ dẫn tuyệt vời ngăn chặn tình trạng LLM gọi hàm tính tiền một cách bừa bãi khi chưa biết cụ thể phòng đó có trống hay không.
3. **Cơ chế Function Calling của LLM:**
   - Khi LLM quyết định gọi Function, nó gửi một payload JSON đến ứng dụng khách. Nếu mô tả rõ ràng, LLM sẽ sinh JSON schema khớp 100% với DTO (Java record/POJO) của Spring AI.

---

## Phần 4: Phân tích rủi ro của các phương án loại trừ

### 1. Phương án A (Mô tả tối giản) - RỦI RO CAO
- **Rủi ro 1: Sai lệch định dạng dữ liệu (Format Mismatch Exception).** LLM có thể truyền định dạng ngày bất kỳ (ví dụ: "25/12/2026", "tomorrow", "2026.12.25") gây lỗi Parser Exception ở backend.
- **Rủi ro 2: Thiếu tham số bắt buộc.** Do mô tả quá ngắn, LLM không biết nó bắt buộc phải thu thập đủ `checkInDate`, `checkOutDate` và `roomType` từ user trước khi gọi `getRoomAvailability`.
- **Rủi ro 3: Gọi sai trình tự.** LLM có thể gọi luôn `calculateTotalPrice` ngay khi khách hàng hỏi giá, trong khi chưa hề check xem phòng đó còn trống hay không.

### 2. Phương án C (Mô tả kỹ thuật nội bộ) - RỦI RO TRUNG BÌNH & GÂY NHIỄU
- **Rủi ro 1: Lãng phí Token & Suy giảm hiệu năng Attention.** Việc liệt kê các từ khóa công nghệ như `BookingService`, `room_status`, `MySQL DB`, `JPA` hoàn toàn vô nghĩa với LLM vì nó không có quyền truy cập trực tiếp vào các thành phần này. Việc này làm phình to context window (Prompt Token Cost tăng).
- **Rủi ro 2: Nhầm lẫn về hành vi.** LLM có thể hiểu lầm rằng nó cần phải viết truy vấn SQL hoặc cố gắng suy luận cấu trúc database, dẫn đến việc giải thích lan man cho người dùng thay vì chỉ thực thi gọi hàm.
- **Rủi ro 3: Vi phạm nguyên tắc đóng gói (Encapsulation).** Việc để lộ thông tin cấu trúc cơ sở dữ liệu nội bộ trong Metadata Tool là một thiết kế tồi, gây khó khăn cho việc bảo trì code khi backend thay đổi công nghệ (ví dụ chuyển sang MongoDB hay PostgreSQL).