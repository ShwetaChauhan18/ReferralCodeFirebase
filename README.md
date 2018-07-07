"# ReferralCodeFirebase" 

"# Firebase cloud function"
####Javascript

```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.grantSignupReward = functions.database.ref('/users/{uid}/last_signin_at')
  .onCreate(event => {
    var uid = event.params.uid;
    admin.database().ref(`users/${uid}/referred_by`)
      .once('value').then((data) => {
        var referred_by_somebody = data.val();
        if (referred_by_somebody) {
          var moneyRef = admin.database().ref(`/users/${uid}/inventory/pieces_of_eight`);
          moneyRef.transaction((current_value)=> {
            return (current_value || 0) + 10;
          });
        }
        return console.log('reddem updated')
      }).catch(error => {
        if (error) {
          throw error;
      }
      });
  });
