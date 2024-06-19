# Sử dụng hình ảnh cơ bản là Ubuntu
FROM ubuntu:latest

# Thiết lập biến môi trường để không cần sự tương tác của người dùng
ENV DEBIAN_FRONTEND=noninteractive

# Cập nhật và cài đặt các gói cần thiết
RUN apt-get update && \
    apt-get install -y wget gnupg2 curl && \
    apt-get install -y libxinerama1 libfontconfig1 libdbus-glib-1-2 libcairo2 libcups2 libglu1-mesa libsm6 && \
    apt-get install -y ttf-mscorefonts-installer fonts-wqy-zenhei && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Tải xuống và cài đặt LibreOffice
RUN wget https://downloadarchive.documentfoundation.org/libreoffice/old/7.5.1.1/deb/x86_64/LibreOffice_7.5.1.1_Linux_x86-64_deb.tar.gz && \
    tar -zxvf LibreOffice_7.5.1.1_Linux_x86-64_deb.tar.gz && \
    dpkg -i LibreOffice_7.5.1.1_Linux_x86-64_deb/DEBS/*.deb && \
    rm -rf LibreOffice_7.5.1.1_Linux_x86-64_deb LibreOffice_7.5.1.1_Linux_x86-64_deb.tar.gz

# Cài đặt Node.js
RUN wget -qO- https://deb.nodesource.com/setup_16.x | bash - && \
    apt-get install -y nodejs

# Thiết lập biến môi trường để tránh lỗi liên quan đến DBus
ENV DBUS_SESSION_BUS_ADDRESS=/dev/null

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép package.json và package-lock.json vào thư mục làm việc
COPY package*.json ./

# Cài đặt các phụ thuộc của Node.js
RUN npm install

# Sao chép toàn bộ nội dung vào thư mục làm việc
COPY . .

# Mở cổng 3000
EXPOSE 3000

# Khởi động server khi container chạy
CMD ["npm", "start"]