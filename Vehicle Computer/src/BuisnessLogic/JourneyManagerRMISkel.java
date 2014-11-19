package BuisnessLogic;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Stensig
 */
public interface JourneyManagerRMISkel extends Remote{
    TicketList getExistingTickets(PassengerList passengers) throws RemoteException;
    TicketList createNewTickets(PassengerList passengers) throws RemoteException;
}
