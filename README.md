# RockPaperScissors
 
A simple Rock-Paper-Scissors game made with libGDX and Kryonet. Also, inspired from RPSNet project on GitHub to create network communications in project. 

Any graphics are not licensed and are not used for any commercial purposes. This project developed only for self-education and enthusiasm.

# Things to Remember Before Start

Some details about the game's network structure must be explained as;

  - If you are testing for server-side; in Application.java (which is project's main class), ADMIN boolean variable should be true. It will be understood while the code in corresponding class reviewed by the user.
 
  - In NetworkHandler.java file, you should set your server's public IP Address and PORT number - which will be used for communication between client-server sides - to created variables, appropriately.
  
  - After any match (GameInstance) has ended, clients will return to main menu and their connection is stopped. Clients must join(connect) again to play another game.
  
  - At the same time, a nickname cannot be taken by more than one client. If the second one insists, s/he will be disconnected from the server.
  

Thanks for attention.

# Some Screenshots 

https://drive.google.com/file/d/1LG7x1yOZD9clOasv8LrSdS_l6TLzoUoc/view?usp=sharing
https://drive.google.com/file/d/1LAdt_aD9AzZ9KEFE4c1VZtVL_FZcVSmW/view?usp=sharing
https://drive.google.com/file/d/1L9e4X9j9UE5EA6bnmfhc9oEjN--f7KqD/view?usp=sharing
