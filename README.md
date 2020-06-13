# Nomadics

COP4656 - Mobile Programming

Semester Project - Nomadics is a prototype tourism application being developed within android studios.

- [X] Main Page UI
- [X] Fragment Creation and Communication
- [X] Search Bar
- [X] Weather Fragment
- [X] Bookmark Fragment
- [X] Places to go Fragment
  - [X] Restaurants
  - [X] Lodging
  - [X] Parks
  - [X] Points of Interest
- [X] Maps fragment
  - [ ] ~~Please note that to get this to work on other team memeber's machines, we needed to include the SHA-1 signing-certificate fingerprint (this will show up if the
	error reproduces itslef but we can likely find it through other means). To add it I (Ryan) used this link: https://console.developers.google.com/apis/credentials/key/f32deca3-d002-4542-bde9-b707160fb4f7?project=nomadics-274422&supportedpurview=project~~
	
	I later realized I misunderstood how the Google API funcitons and solved this problem so that anyone could freely use the application without issue.
- [X] Menu to switch between views

Please note that in the places to go fragment, if the user selects a place from the list, not all the information (address, hours, phone) will be displayed. 
This is due to the structure of JSON object returned from the HttpGET request. The ID does not work with the function to get the Place object for the search bar. 
The JSON object usually only contains the name, ID, and the coordinates. The user can still open the map by clicking on the picture on the dialog.
