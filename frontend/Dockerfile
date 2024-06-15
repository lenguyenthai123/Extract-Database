# Stage 1: Build the Angular application
FROM node:16-alpine AS build

# Đặt thư mục làm việc bên trong container
WORKDIR /app

# Sao chép file package.json và package-lock.json vào container
COPY package.json package-lock.json ./

# Cài đặt các dependencies
RUN npm ci

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# Build ứng dụng Angular
RUN npm run build --prod

# Stage 2: Serve the application using Nginx
FROM nginx:alpine

# Copy built Angular application từ stage build vào thư mục phục vụ của Nginx
COPY --from=build /app/dist/your-angular-app /usr/share/nginx/html

# Copy custom Nginx configuration file (nếu có)
# COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80 để truy cập ứng dụng
EXPOSE 80

# Default command để khởi chạy Nginx
CMD ["nginx", "-g", "daemon off;"]