This is a very terrible Android app that I hacked together. It's intended to be used with a server
(https://github.com/jackmurray/broodminder-diy/blob/master/Bluetooth%20Reader/Python/sqlite_to_influxdb.py)
that will parse the uploaded database and insert the data into InfluxDB. See the linked repo for a
Dockerfile / docker-compose file to run the server.

When you run the app you will see a text box for you to fill in the URL that you're hosting the server
on. Do that, and press save. Then you can export the database from the BroodMinder 'Bees' app. Once
you've got an exported file, you can press the 'Select DB3 File' button to upload the db file to the
server.

Disclaimer: I don't know wtf I'm doing when it comes to Android development. This app could
well be a total pile of crap (and probably is), and if anything bad happens to you it's your own fault.