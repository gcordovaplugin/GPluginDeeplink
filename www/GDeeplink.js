var exec = require("cordova/exec");

const SERVICE_NAME = "GDeeplink";
module.exports = {
  canOpen: function (success, error) {
    exec(success, error, SERVICE_NAME, "canopen", [""]);
  },
  init: function (success) {
    exec(success, null, SERVICE_NAME, "init", [""]);
  },
};
