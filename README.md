NoHarp
======

Installation notes working environment
--------------------------------------
* Download & install LEAP SDK: https://developer.leapmotion.com/downloads/
* Download & install JMonkeyEngine: http://hub.jmonkeyengine.org/downloads/
* Setup JMonkeyEngine:
	* Create a new project called "leaptest" somewhere on your disk, but not inside the repo
	* Under the project tab, goto LeapTest, right click it and select "properties"
		* Under the Sources Category Add a new Source Package Folder: link this to the "src/" folder in the repo
		* Under the Libraries Category Add JAR/Folder linking to the LeapJava.jar in the LEAP SDK
		* Under the Run Category set the "VM Options" to point to the native libraries of the LEAP SDK for your architecture:
			-Djava.library.path=/path/to/LeapDeveloperKit/LeapSDK/lib
* Copy the Main.java from the template folder in the repo to your "src/leaptest/" folder of the repo			
* It should all be properly setup now!