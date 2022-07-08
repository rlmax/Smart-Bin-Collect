const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

var msgData;

exports.SendNotification = functions.database.ref()
    .onUpdate((snapshot, context) => {
        var newval = snapshot.after.val();
        if (newval.precent >= 80) {
            var payload = {
                    "notification": {
                        "title": "Smart Bin Collect",
                        "body": "Bin Full  prescentage " + newval.precent + "\n" + " now you can collect this bin",
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