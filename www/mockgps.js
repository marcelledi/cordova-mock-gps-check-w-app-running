var mockgps = {
  check: function (successCallback, errorCallback, whitelist) {
    cordova.exec(successCallback, errorCallback, 'MockGpsChecker', 'check', whitelist);
  }
}

cordova.addConstructor(function () {
  if (!window.plugins) {window.plugins = {};}

  window.plugins.mockgpschecker = mockgps;
  return window.plugins.mockgpschecker;
});
