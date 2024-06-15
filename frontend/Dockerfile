# Sử dụng image Node.js làm base image
FROM node:20

# Cài đặt Angular CLI toàn cục
RUN npm install -g @angular/cli

# Tạo thư mục ứng dụng
WORKDIR /app

# Sao chép file package.json và package-lock.json để cài đặt dependencies
COPY package*.json ./

# Cài đặt các dependencies
RUN npm install

# Sao chép toàn bộ mã nguồn ứng dụng vào thư mục làm việc
COPY . .

# Expose port 4200 để Angular có thể phục vụ ứng dụng
EXPOSE 4200

# Lệnh khởi động ứng dụng Angular
CMD ["ng", "serve", "--host", "0.0.0.0"]