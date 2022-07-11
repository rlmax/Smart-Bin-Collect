const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

var msgData;

exports.SendNotification = functions.database.ref()
    .onUpdate((snapshot, context) => {
        var newval = snapshot.after.val();
        if (newval.precent >= 80 && newval.status == false) {
            var payload = {
                    "notification": {
                        "title": "Smart Bin Collect",
                        "body": "Bin is full , now you can collect garbage bin " + newval.name,
                        "sound": "default"
                    }
                }
                //create topics
            var topics = "notification"
            return admin.messaging().sendToTopic(topics, payload).then((response) => {
                console.log('Pushed them all');
            }).catch((err) => {
                console.log(err);
            });
        }
        log(newval.precent);
        //Your notification code here
    });