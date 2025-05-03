package com.goit.hotelonlinebooking.Tests;

import com.goit.hotelonlinebooking.dao.UserDAO;
import com.goit.hotelonlinebooking.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import com.goit.hotelonlinebooking.controller.Controller;

import java.util.List; // you forgot this import
class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        userDAO.save(new User(3, "Ivan", "Sidorov", 32, "sidorov@i.ua", "0978763434", "987"));
        // Register users with common names and last names
        userDAO.save(new User(1, "Dana", "Sami", 21, "dana@i.ua", "0551112233", "pass1"));
        userDAO.save(new User(2, "Samar", "Alghamdi", 21, "samar@i.ua", "0551112244", "pass2"));
        userDAO.save(new User(4, "Dana", "Alghamdi", 22, "dee@i.ua", "0551112255", "pass3"));
    }

    // TC1: Invalid phone number length
    @Test
    public void test_TC1_InvalidPhoneNumber() {
        User newUser = new User(4, "James", "Brown", 28, "james@i.ua", "123", "pass123");

        boolean result = userDAO.checkRegistration(newUser);

        // Expect validation to fail (invalid phone length)
        assertFalse(result);
    }

    // TC2: Duplicate ID
    @Test
    public void test_TC2_DuplicateID() {
        User newUser = new User(3, "James", "Carter", 30, "james@i.ua", "0511223344", "123456");

        boolean result = userDAO.checkRegistration(newUser);

        // Expect validation to fail (duplicate ID)
        assertFalse(result);
    }

    // TC3: Valid registration input
    @Test
    public void test_TC3_ValidUserRegistration_MessageCheck() {
        Controller controller = new Controller();

        User newUser = new User(4, "James", "Carter", 30, "james@i.ua", "0511223344", "secure123");

        controller.userRegistration(newUser); // ✅ This triggers the print statement
    }

    //  TC4: Invalid email (missing '@')
    @Test
    public void test_TC4_InvalidEmail_MissingAt() {
        User user = new User(4, "James", "Carter", 30, "jamesi.ua", "0511223344", "pass123");

        boolean result = userDAO.checkRegistration(user);

        // Should return false due to invalid email
        assertFalse(result);
    }

    //  TC5: Invalid email (missing '.')
    @Test
    public void test_TC5_InvalidEmail_MissingDot() {
        User user = new User(4, "James", "Carter", 30, "james@iua", "0511223344", "pass123");

        boolean result = userDAO.checkRegistration(user);

        // Should return false due to invalid email
        assertFalse(result);
    }

    // TC6: Duplicate email
    @Test
    public void test_TC6_DuplicateEmail() {
        User user = new User(4, "John", "Smith", 30, "sidorov@i.ua", "0511223344", "pass123");

        boolean result = userDAO.checkRegistration(user);

        // Should return false because email already exists
        assertFalse(result);
    }

    // TC7: Invalid phone (too short) + duplicate ID
    @Test
    public void test_TC7_InvalidPhoneAndDuplicateID() {
        User user = new User(3, "Test", "User", 30, "sidoroviua", "123", "pass321");

        boolean result = userDAO.checkRegistration(user);

        assertFalse("Should fail due to invalid phone and duplicate ID", result);
    }

    // TC8: Invalid phone (too short) only
    @Test
    public void test_TC8_InvalidPhoneOnly() {
        User user = new User(4, "Test", "User", 30, "sidoroviua", "123", "pass321");

        boolean result = userDAO.checkRegistration(user);

        assertFalse("Should fail due to invalid phone number", result);
    }

    //  TC9: Duplicate phone number
    @Test
    public void test_TC9_DuplicatePhoneNumber() {
        User user = new User(4, "James", "Carter", 30, "james@i.ua", "0978763434", "securepass");

        boolean result = userDAO.checkRegistration(user);

        assertFalse("Should fail due to duplicate phone number", result);
    }
    // TC10: findUserByName() should return a user named "Ivan" (exists in setUp)
    @Test
    public void test_TC10_FindUserByName_Found() {
        List<User> users = userDAO.findUserByName("Ivan");

        assertNotNull(users);                  // List should not be null
        assertFalse(users.isEmpty());          // At least one match expected
        assertEquals("Ivan", users.get(0).getName()); // Confirm it's the expected user
        System.out.println("Found user name: " + users.get(0).getName());

    }
    // TC11: findUserByName() should return empty list for unmatched name
    @Test
    public void test_TC11_FindUserByName_NotFound() {
        List<User> users = userDAO.findUserByName("NoSuchName");

        assertNotNull(users);  // Still returns empty list, not null
        assertTrue(users.isEmpty());  // No match found
    }

    // TC12: findUserByLastName() should return user with last name "Ivanov"
    @Test
    public void test_TC12_FindUserByLastName_Found() {
        List<User> users = userDAO.findUserByLastName("Sidorov");

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals("Sidorov", users.get(0).getLastName());
        System.out.println("Found a user his last name: " + users.get(0).getLastName());

    }

    //  TC13: findUserByLastName() should return empty for nonexistent last name
    @Test
    public void test_TC13_FindUserByLastName_NotFound() {
        List<User> users = userDAO.findUserByLastName("Ghost");

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void test_FindUserByCommonFirstName() {
        List<User> result = userDAO.findUserByName("Dana");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> u.getName().equals("Dana")));
        System.out.println("Found users with name 'Dana':");
        result.forEach(u -> System.out.println(u.getName() + " " + u.getLastName() + " - " + u.getEmail()));
    }

    @Test
    void test_FindUserByCommonLastName() {
        List<User> result = userDAO.findUserByLastName("Alghamdi");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> u.getLastName().equals("Alghamdi")));

        System.out.println("Found users with last name 'Alghamdi':");
        result.forEach(u -> System.out.println(u.getName() + " " + u.getLastName() + " - " + u.getEmail()));
    }
}