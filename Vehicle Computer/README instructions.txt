To run the system, use the bat files found in their respective repositories.   

Note that runVC.bat has predefined the arguments that are given to the main method.
These may need to be changed. They are as follows:
#1: Zone number of the start zone
#2: The port number for the socket that will communicate up to the TrafficManager on the business logic backend server. 
#3: The location of the TrafficManager. This may be a literal IPv6 address or a host name. Use 'localhost' if the TrafficManager runs at the same location.

runServer.bat should be used for running the backend server. Note that the predfined arguments are here:
#1: Location of the RMI registry - either literal IPv6 address or host name. 
#2: Port number of the RMI registry.
#3: The name by which the JourneyManagerRMIImplementation class is stored in the RMI regeistry. 