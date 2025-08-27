# 🚀 User Authentication & Authorization Service

## 📖 1. Giới thiệu tổng quan
Đây là **service quản lý người dùng và phân quyền** trong hệ thống.  
Nó đóng vai trò trung tâm cho:
- 👤 **Tài khoản & hồ sơ người dùng**  
- 🔑 **Phân quyền (Role & Permission)**  
- 🛡️ **Xác thực & Token**  

> ✅ Service đang chạy tại **port `8080`**.

---

## ⚙️ 2. Chức năng chi tiết

### 👤 Quản lý tài khoản
- **Đăng ký** → Tạo mới tài khoản người dùng.  
- **Đăng nhập** → Xác thực thông tin đăng nhập.  
- **Quản lý hồ sơ** → Lấy & cập nhật thông tin cá nhân.  

### 🔑 Quản lý phân quyền (Permission & Role)
- **Tạo role mặc định** → Gán sẵn role khi tạo người dùng.  
- **Gán quyền cho role** → Thiết lập permission cho từng nhóm người dùng.  
- **Quản lý scope/group** → Gom nhóm & phân tầng quyền linh hoạt.  

### 🛡️ Token & Authentication
- **Sinh JWT Token** → Cấp quyền truy cập cho người dùng.  
- **Refresh Token** → Cấp lại token khi token hết hạn.  
- **Xác thực phiên đăng nhập** → Kiểm tra token để bảo vệ API.  

---

## 📂 3. Cấu trúc thư mục

```bash
com.ecomhub.cartservice/
├── adapters/          # Controller: định nghĩa endpoint
├── application/      #  Service layer: xử lý logic ứng dụng
├── domain/           #  Business logic & entity chính
├── infrastructure/   #  Repository, kết nối DB, cấu hình hệ thống
├── shared/           #  Helper, utils, constants dùng chung
