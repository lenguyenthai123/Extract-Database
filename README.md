# ViettelDigitalTalent-Extract-Database

### Setup môi trường

#### Backend:
Nên chạy trong docker-compose
1. Chạy Redis trên docker với port mapping ra bên ngoài là 6379:
2. Chạy ElasticSearch trên Docker:
   - Image : docker.elastic.co/elasticsearch/elasticsearch:7.17.9  (chủ ý phải chạy đúng phiên bản này thì mới có thể kết nối được)
   - Mapping port: 9200:9200 và 9300:9300
3. Chạy database MySql với Docker:
   - Image: mysql:8.0
   - Chạy init script SQL được để trong thư mục backend/extraction-service/mysql-scripts/database.sql
     
#### Frontend:
 Yêu cầu: Sau khi npm install để tải các thư viện cần thiết cần tải thêm 2 thư viện sau:
  1. npm install mammoth  (thư viện chuyển dữ liệu file thành html)
  2. npm install util  
#### Service-Carbone:
Yêu cầu trên máy cần phải có ứng dụng  Libreoffice để hỗ trợ việc convert sang các định dạng file khác nhau như pdf, docx, xlxs

Link tải: https://download.documentfoundation.org/libreoffice/stable/24.2.4/win/x86_64/LibreOffice_24.2.4_Win_x86-64.msi

#### Quá trình CD trên EC2:
* Hiện tại quá trình CI/CD đang không ổn định cho việc các thư viện phụ thuộc ở phần Backend đang có sự xung đột với nhau: Cụ thể là phần Backend Spring Boot không kết nối được với Elastic Search trên môi trường Docker

 Yêu cầu:
  1. Vào file application.properties của backend để config lại các thông số cho phần hợp với môi trường docker và EC2
  2. Vào file env trong frontend để chuyển đổi địa chỉ ip tương ứng với địa chỉ IP của backend khi được deploy trên EC2
