<h1 align="center">CodeDocs</h1>
<h2 align="center"><i>Code, Collaborate, Comprehend on CODEDOCS !</i></h2>

<hr>

# Table of Contents

* [ About CodeDocs ](#about)
* [ What it does ](#features)
* [What's next for CodeDocs](#future)
* [Instructions to run](#instructions)
* [ A Glimpse of CodeDocs ](#images)
* [ Contributors ](#contributors)

# <a name="about"></a>About CodeDocs
Getting stuck on coding problems is quite common among coding geeks. No doubt struggling for hours solves the problem, but discussing the same with peers can help grow and save time. So our team thought of why not build a collaborative coding platform. CodeDocs enables users to collaborate with other users in real-time and work on a particular problem. 

# <a name="features"></a>What it does
* A user can signup/login/logout.
* Email verification.

![](Screenshots/LoginScreen.png)
 
 
* User can create/edit/save CodeDoc.
* Users can fetch his previous codeDocs and those he collaborates with.

![](Screenshots/MainScreen.png)


* Users can allow other users to edit/see in their CodeDoc by sending invites, and the receiver can accept/reject it. 
* The owner of the CodeDoc can change the write permissions of the collaborators and remove them.

![](Screenshots/InvitationScreen.png)

![](Screenshots/ManageScreen.png)


* Coding area supports writing and compiling code in four languages. (Java, C , C++, Python)
* Coding area shows line numbers.
* Coding area has highlighting/autocomplete/recommendation feature according to language (implemented using trie).

![](Screenshots/CodeEditor.gif)


* Users can run programs by providing input and can have output.

![](Screenshots/RunScreen.png)


* Chat section where users can send/receive messages both privately and to everyone.

![](Screenshots/ChatScreen.png)


* When any user updates in the CodeDoc the same change is reflected in every other user's window, working on the same CodeDoc.
* Cursors for users are displayed with their respective names in every other user's window, working on the same CodeDoc.

![](Screenshots/EditorScreen.png)


* Audio communication between users.

![](Screenshots/ControlScreen.png)


* Multiple tabs for each collaborative session.
* Users can take screenshots and notes as a future reference for specific algorithms.

![](Screenshots/NotesScreen.png)

# <a name="future"></a>What's next for CodeDocs
* Add debug option.
* Add download CodeDoc feature for offline working.
* Implement code indentation.
* Implement video communication among the collaborators.
* Implement some advanced synchronization while editing CodeDoc.


# <a name="instructions"></a>Instructions to run
* Clone the repository. 
* Import codedocs.sql into your mysql workbench.
* Create the following configuration files and specify value for each field.
    *  /CodeDocs-Server/src/main/resources/configurations/auth.properties
    <p align="center"><img alt="pic" width="520" height="200" src="https://user-images.githubusercontent.com/59930598/147474680-5fb7cc9b-de01-48d5-9bbe-89e8774a7bde.png"/></p>
  
    *  /CodeDocs-Server/src/main/resources/configurations/db.properties
    <p align="center"><img alt="pic" width="520" height="200"  src="https://user-images.githubusercontent.com/59930598/147473707-8f931b9d-a2b0-49ee-83bd-3198cb482f80.png"/></p>
    
    *  /CodeDocs-Server/src/main/resources/configurations/server.properties
    <p align="center"><img alt="pic" width="520" height="200" src="https://user-images.githubusercontent.com/59930598/147474620-853d4477-aa65-47c6-aaf3-cdd38389b750.png"/></p>
    
    *  /CodeDocs-Server/src/main/resources/configurations/mail.properties
    <p align="center"><img alt="pic" width="520" height="200" src="https://user-images.githubusercontent.com/59930598/147474948-30e8d20e-d30e-4ea8-8b26-8ec658142a1b.png"/></p>
    
    
    *  /CodeDocs-Client/src/main/resources/configurations/server.properties
    <p align="center"><img alt="pic" width="420" src="https://user-images.githubusercontent.com/59930598/147475154-c674afc5-a596-411c-b50a-0dfd9f40f6ce.png"/></p>
  
    *  /CodeDocs-Client/src/main/resources/configurations/file.properties
    <p align="center"><img alt="pic" width="420" src="https://user-images.githubusercontent.com/59930598/147475162-5cff0073-16bc-479d-8187-dee655870f5f.png"/></p>

* Start the server and then client. Try hands on CodeDocs and give us your valuable feedback.     
    


# <a name="images"></a>A Glimpse of CodeDocs

![](20211226_203104.gif)

# <a name="contributors"></a>Contributors
* [Harsh Gyanchandani](https://github.com/harshh3010)
* [Gursimran Kaur Saini](https://github.com/gursimran18)

