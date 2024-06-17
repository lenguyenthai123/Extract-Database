const express = require("express");
const carbone = require("carbone");
const fs = require("fs");
const app = express();
const port = 3000;
const path = require("path");
const multer = require('multer');

// Middleware
app.use(express.json()); // For parsing application/json

// Set up storage for multer
const storage = multer.memoryStorage();
const upload = multer({ storage: storage });

// POST route to generate and send a .docx file
app.post('/generate-report', upload.single('file'), (req, res) => {
  var data = req.body.dataJson;
  var type = req.body.type;
  var file = req.file;

  console.log(file);

  console.log("Data: ", data);
  console.log("Data: ", JSON.parse(data));

  // Lưu file vào thư mục hiện tại
  fs.writeFileSync(file.originalname, file.buffer);


  var options = {
    convertTo: type, //output
  };


  carbone.render(`./${file.originalname}`, JSON.parse(data),options, function (err, result) {
    if (err) {
      return console.log(err);
    }


    const resultPath = `./Report.${type}`;
    fs.writeFileSync(resultPath, result);

    // Gửi file .docx về client
    res.download(resultPath, resultPath, (err) => {
      if (err) {
        console.error('Error sending file:', err);
      }

      // Xóa các file tạm thời sau khi gửi
      fs.unlink(file.originalname, err => {
        if (err) console.error('Error deleting temp file:', err);
      });
      // fs.unlink(resultPath, err => {
      //   if (err) console.error('Error deleting result file:', err);
      // });
    });

  });
});

// Start the server
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}/`);
});
