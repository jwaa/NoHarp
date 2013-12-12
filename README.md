NoHarp
======

Installation notes working environment
--------------------------------------
* Download & install the LEAP drivers: https://www.leapmotion.com/setup/
* Download & install LEAP SDK: https://developer.leapmotion.com/downloads/
* Download & install JMonkeyEngine: http://hub.jmonkeyengine.org/downloads/
* Setup JMonkeyEngine:
	* Create a new JM3/BasicGame project called "leaptest" somewhere on your disk, but not inside the repo
	* Under the project tab, goto LeapTest, right click it and select "properties"
		* Under the Sources Category Add a new Source Package Folder: link this to the "/src" folder in the repo
		* Under the Libraries Category Add JAR/Folder linking to the LeapJava.jar in the LEAP SDK
		* Under the Run Category set the "VM Options" to point to the native libraries of the LEAP SDK for your architecture. E.g. for 64 bit system: 
			-Djava.library.path=/path/to/LeapDeveloperKit/LeapSDK/lib/x64
* Navigate to the "/templates" folder in the repo			
	* Copy the Main.java from the template folder to your "/src/leaptest" folder of the repo
	* Copy the settings.txt from the template folder to where you created the leaptest project on your local machine (i.e. not in the repo). Put it in the root of your project folder e.g. "/leaptest".
* It should all be properly setup now!