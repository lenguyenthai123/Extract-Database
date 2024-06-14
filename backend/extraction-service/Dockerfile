FROM eclipse-temurin:22-jre-alpine

# Đặt biến môi trường để chỉ định tên của JAR file
ENV APP_JAR=app.jar

# Sao chép file JAR vào container
COPY target/extraction-service-0.0.1-SNAPSHOT.jar /app.jar

# Mở cổng 8080 để truy cập ứng dụng
EXPOSE 8080

# Chỉ định lệnh để chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "/app.jar"]