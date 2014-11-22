To run the system, use the bat files - those located in the build\classes folder if looking in the NetBeans project folder.  

Note that runVC.bat has predefined the arguments that are given to the main method.
These may need to be changed. They are as follows:
#1: Zone number of the start zone
#2: The port number for the socket that will communicate up to the TrafficManager on the business logic backend server. 
#3: The location of the TrafficManager. They may be a literal IPv6 address or a host name. Use 'localhost' if the TrafficManager runs at the same location.

For testing the integration in this subsystem by itself, use the runTestServer.bat, which utilizes a dummy RMI-impl class, and a local registry.
For full-scale integration testing, runServer.bat should be used. However, this file needs to be modified first, with the inputs that are described in that file. 