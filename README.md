# DnsClient
Java version: 1.8.0_60
Java<TN>SE Runtime Environment <build 1.8.0_60-b27>
Must be connected to the Internet.


Intructions
The project can be run in two ways:
**********FIRST WAY****************
- Download the DNS.jar file and place it somewhere in your documents.Then, from the command line, go to the directotry where the jar is located. Then, type:

java -jar DNS.jar [ ...]

where [...] is the arguments required. For instance;

java -jar DNS.jar -r 30 -t 30 -A @8.8.8.8 facebook.com

-Immeadiately after, you will receive a reponse.

Reapeat the last step in order to ask the DNS server a different type of question.

**********Second WAY****************

- Download the whole project where the java files are and save it somewhere within your Documents.
 
-Open the Eclipse program.

- Go the *File* tab and click on *Import*.

-Choose Existing Projects into Workspace.

-Click on  Browse and find the downloaded project.

-Hit Finish

-If the project has been succesfully donwloaded, compile the project.

-Right click on the project folder and go to *run as* and choose *run configurations*.

- A window will open. Change to *arguments window*.

-Within the Program arguments text field, pass in the arguments you require e.g;
 [-t 45 -r 5 @ 8.8.8.8 youtube.com] and hit *Run*
-A response will appear.
-You can change these values in here  whenever you want to ask another query type to the DNS server.

