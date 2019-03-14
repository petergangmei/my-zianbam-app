const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
exports.hellowWorld = functions.https.onRequest((req, res) =>{
	res.send("Hellow world from firesebase cloud functions");

});

exports.insertIntoDB = functions.https.onRequest((req, res) =>{
const text = req.query.text;

});

