const express = require("express");
const carbone = require("carbone");
const fs = require("fs");
const app = express();
const port = 3000;
const path = require("path");

// Middleware
app.use(express.json()); // For parsing application/json

// POST route to generate and send a .docx file
app.post("/generate-report", (req, res) => {
  var data = req.body;
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
