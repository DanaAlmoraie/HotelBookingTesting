package com.goit.hotelonlinebooking.controller;

import com.goit.hotelonlinebooking.entity.Hotel;
import com.goit.hotelonlinebooking.entity.Room;
import com.goit.hotelonlinebooking.entity.User;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ControllerTest {

    private Controller controller;

    @Before
    public void setUp() {
        controller = new Controller();

        // Register users (needed to login)
        controller.userRegistration(new User(1, "Ivan", "Ivanov", 24, "ivanov@i.ua", "0679656343", "qwerty"));
        controller.userRegistration(new User(2, "Petr", "Petrov", 27, "petrov@i.ua", "0689871234", "123"));
        controller.userRegistration(new User(3, "Ivan", "Sidorov", 32, "sidorov@i.ua", "0978763434", "987"));
    }

    //  TC1: Valid hotel name = "Hayat", user is logged in
    @Test
    public void test_TC1_HotelFoundAndLoggedIn() {
        controller.login(1);
        List<Room> result = controller.getFreeRoomsByHotel("Hayat");

        System.out.println("Available rooms in 'Hayat':");
        result.forEach(r -> System.out.println("Room ID: " + r.getId()));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(r -> r.getUserReserved() == null));
    }

    //  TC2: Hotel does not exist
    @Test
    public void test_TC2_HotelNotFoundButLoggedIn() {
        controller.login(2);
        List<Room> result = controller.getFreeRoomsByHotel("FakeHotel");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //  TC3: User not logged in
    @Test
    public void test_TC3_NotLoggedIn() {
        Controller guest = new Controller();
        List<Room> result = guest.getFreeRoomsByHotel("Hayat");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TC4: City = Dnepr, logged in → should return hotels
    @Test
    public void test_TC4_Dnepr_LoggedIn() {
        controller.login(1);  // simulate a logged-in user

        List<Hotel> result = controller.findHotelByCity("Dnepr");

        assertNotNull(result);          // The list should not be null
        assertFalse(result.isEmpty());  // There should be hotels in "Dnepr"
    }

    // TC5: City = Paris, logged in → no hotels
    @Test
    public void test_TC5_Paris_LoggedIn() {
        controller.login(1);
        List<Hotel> result = controller.findHotelByCity("Paris");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }



    //  TC6: Uppercase city, logged in → case-sensitive match fails
    @Test
    public void test_TC6_DNEPR_Uppercase_LoggedIn() {
        controller.login(1);
        List<Hotel> result = controller.findHotelByCity("DNEPR");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TC7: Empty city, logged in
    @Test
    public void test_TC7_EmptyCity_LoggedIn() {
        controller.login(1);
        List<Hotel> result = controller.findHotelByCity("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //  TC8: Dnepr city, guest user
    @Test
    public void test_TC8_Dnepr_Guest() {
        Controller guest = new Controller();
        List<Hotel> result = guest.findHotelByCity("Dnepr");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //  TC9: Uppercase DNEPR, guest user
    @Test
    public void test_TC9_DNEPR_Guest() {
        Controller guest = new Controller();
        List<Hotel> result = guest.findHotelByCity("DNEPR");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //  TC10: Paris city, guest user
    @Test
    public void test_TC10_Paris_Guest() {
        Controller guest = new Controller();
        List<Hotel> result = guest.findHotelByCity("Paris");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void test_TC11_BookRoom_Successful() {
        Controller controller = new Controller();

        controller.userRegistration(new User(1, "Ivan", "Ivanov", 24, "ivanov@i.ua", "0679656343", "qwerty"));
        controller.login(1); // set flagLogin = true

        // Get hotel "Hayat" and find room with ID = 2
        List<Room> allRooms = controller.getFreeRoomsByHotel("Hayat");

        // Find room with id 2 in the list
        Room targetRoom = allRooms.stream()
                .filter(r -> r.getId() == 2)
                .findFirst()
                .orElse(null);
        assertNotNull("Room with ID 2 should exist in Hayat", targetRoom);
        assertNull("Room 2 should not already be reserved", targetRoom.getUserReserved());
        controller.bookRoom(2, 1, 1); // roomId=2, userId=1, hotelId=1
        assertNotNull("Room 2 should now be reserved", targetRoom.getUserReserved());
        assertEquals("Room 2 should be reserved by userId 1", 1, targetRoom.getUserReserved().getId());
    }


    @Test
    public void test_TC12_BookRoom_InvalidHotel_NoCrash() {
        Controller controller = new Controller();

        controller.userRegistration(new User(1, "Ivan", "Ivanov", 24, "ivanov@i.ua", "0679656343", "qwerty"));
        controller.login(1);  // flagLogin = true


        controller.bookRoom(2, 1, 999);  // this should just do nothing safely
    }
    
    @Test
    public void test_TC13_BookRoom_NotLoggedIn_NoCrash() {
        Controller controller = new Controller();

        controller.userRegistration(new User(1, "Ivan", "Ivanov", 24, "ivanov@i.ua", "0679656343", "qwerty"));

        controller.bookRoom(2, 1, 1);

    
    @Test
    public void test_TC14_BookRoom_InvalidRoomId_NoCrash() {
        Controller controller = new Controller();

        // Register and login user
        controller.userRegistration(new User(1, "Ivan", "Ivanov", 24, "ivanov@i.ua", "0679656343", "qwerty"));
        controller.login(1); // flagLogin = true

        controller.bookRoom(99, 1, 1);

    }

    @Test
    public void test_TC15_CancelReservation_Successful() {
        Controller controller = new Controller();
        controller.userRegistration(new User(1, "Ivan", "Ivanov", 24, "ivan@i.ua", "0666666666", "pass"));
        controller.login(1);

        List<Room> rooms = controller.getFreeRoomsByHotel("Hayat");
        Room room = rooms.get(0);
        int roomId = room.getId();

        controller.bookRoom(roomId, 1, 1);
        assertNotNull(room.getUserReserved());  // ensure it's reserved

        controller.cancelReservation(roomId, 1);
        assertNull(room.getUserReserved());  // ensure it's canceled
    }

    @Test
    public void test_TC16_CancelReservation_NotLoggedIn() {
        Controller controller = new Controller();
        controller.cancelReservation(1, 1);  // should print a message, no crash
    }
    @Test
    public void test_TC17_CancelReservation_InvalidHotel() {
        Controller controller = new Controller();
        controller.userRegistration(new User(1, "Ivan", "Ivanov", 24, "ivan@i.ua", "0666666666", "pass"));
        controller.login(1);

        controller.cancelReservation(1, 999);  // hotel ID does not exist
    }

    @Test
    public void test_TC18_Login_NonExistentUser() {
        Controller guest = new Controller();
        guest.login(99);  // ID that doesn't exist

        List<Hotel> result = guest.findHotelByCity("Dnepr");
        assertTrue(result.isEmpty());  // login failed, flagLogin = false
    }



}


