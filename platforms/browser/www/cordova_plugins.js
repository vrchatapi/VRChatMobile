cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/cordova-plugin-appversion/www/app-version.js",
        "id": "cordova-plugin-appversion.RareloopAppVersion",
        "pluginId": "cordova-plugin-appversion",
        "clobbers": [
            "AppVersion"
        ]
    },
    {
        "file": "plugins/cordova-plugin-firebase/www/firebase-browser.js",
        "id": "cordova-plugin-firebase.FirebasePlugin",
        "pluginId": "cordova-plugin-firebase",
        "clobbers": [
            "FirebasePlugin"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-appversion": "1.0.0",
    "cordova-plugin-firebase": "1.0.5",
    "cordova-plugin-whitelist": "1.3.3"
}
// BOTTOM OF METADATA
});