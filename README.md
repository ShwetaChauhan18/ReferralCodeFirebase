"# ReferralCodeFirebase" 

"# Firebase cloud function"
####Javascript

```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.grantSignupReward = functions.database.ref('/users/{uid}/last_signin_at')
.onCreate((snap,context) => {
  const uid = context.params.uid;
    admin.database().ref(`users/${uid}/referred_by`)
      .once('value').then((data) => {
        var referred_by_somebody = data.val();
        if (referred_by_somebody) {
          var moneyRef = admin.database().ref(`/users/${uid}/earned`);
          moneyRef.transaction((current_value)=> {
            return (current_value || 0) + 10;
          });

          var moneyReferred=admin.database().ref(`/users/${referred_by_somebody}/earned`);
          moneyReferred.transaction((current_value)=> {
            return (current_value || 0) + 10;
          });
        }
         
        return console.log('reddem updated')
       //return Promise.resolve();
      }).catch(error => {
        console.log("Got an error: ",error);
      });
  });
