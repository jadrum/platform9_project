# Platform9 Project Submission

I decided to attempt Problem 1: Remote exec utility implementation.

## Problem Topic
In a programming language of your choice, write a client and server program that allows selected users to remotely execute selected programs on a server:
* ##### `Server machine: ./server config-file`
  * `config-file` allows an Admin to specify users, their passwords, and the programs they are allowed 
    to execute.
* ##### `Client machine: ./client --server <server ip> --user <username> --password <password> --exec <program to execute>`

I chose to use Java for my implementation. Everything seemed rather straight forward. I struggled most with attempting to find a simple implementation that supported command line arguments while retaining a straightforward configuration file. The easiest implementation that I could think of was comparing the beginning of the command line program string with the allowed programs listed in the configuration file.

## Configuration File
For the configuration file I decided to use a properties file in the hope that it would be rather straight forward to implement and maintain. The server simply loads a given `.properties` file upon startup to use in validation of clients. The maintainer of the server can store users and there allowed programs as follows:

* ##### `user.<name> = <password>`
* ##### `user.<name>.prog = <prog 1>,<prog2>,<etc.>`

I thought this would allow easy modifications to both the configuration file and the Server side code. 
When listing the programs permitted in the config file, ensure that you write it exactly as you would execute the program on the command line, omitting the parameters. The parameters can simply be included when the client accesses the server.

## Final Thoughts
The code itself has plenty of documentation to explain my thought process and the functionality. I used a solution that did not require downloading any jars. I ran the program on Bash on Ubuntu on Windows. 
Thank you for the opportunity to complete this project. I was recently learning about message queues and forking processes in my Operating Systems course, so it was fun to use java streams between the client and server as well as ProcessBuilder. I greatly enjoyed learning how topics I am learning in school can be applied to realistic applications.

Justin Drum