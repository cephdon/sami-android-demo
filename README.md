SAMI Android SDK sample app
===========================

This sample Android app was created to demonstrate how to use the [SAMI Android SDK](https://github.com/samiio/sami-android). The app was created to showcase how to manage SAMI's authentication (based on OAuth2), send and receive messages with SAMI's REST APIs and use SAMI's WebSockets.

Prerequisites
-------------


Installation
-------------

 * Create an Application in devportal.samsungsami.io:
  * The Redirect URI is set to 'http://localhost:9000/authorize'.
  * Choose "Client credentials, auth code, implicit" for OAuth 2.0 flow.
  * Under "PERMISSIONS", check "Read" for "Profile". 
  * Click the "Add Device Type" button. Pick a few device types to set the proper permissions per the following guideline. If you just want to see data of a device on the sample app, check "Read" permission for the corresponding device type. If you want to create a new device on the sample app, check "Read" and "Write" permissions for the corresponding device type.
 * Import in your favorite IDE
 * Edit `src/io/samsungsami/androidclient/Config.java` and change the value of `APP_ID` with the ID of the application you just created

More about SAMI
---------------

If you are not familiar with SAMI we have extensive documentation at http://developer.samsungsami.io

The full SAMI API specification with examples can be found at http://developer.samsungsami.io/sami/api-spec.html

To create and manage your services and devices on SAMI visit developer portal at http://devportal.samsungsami.io

License and Copyright
---------------------

Licensed under the Apache License. See LICENCE.

Copyright (c) 2015 Samsung Electronics Co., Ltd.
