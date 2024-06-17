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
  var file = req.file;

  console.log(req.body);
  console.log(file);

  carbone.render("./template.docx", data, function (err, result) {
    if (err) {
      return console.log(err);
    }
    // write the result
    fs.writeFileSync("result.docx", result);
  });
  res.send("Report generated successfully");
});

// Start the server
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}/`);
});
