package com.goit.hotelonlinebooking.controller;

import com.goit.hotelonlinebooking.dao.HotelDAO;
import com.goit.hotelonlinebooking.dao.UserDAO;
import com.goit.hotelonlinebooking.entity.Hotel;
import com.goit.hotelonlinebooking.entity.Room;
import com.goit.hotelonlinebooking.entity.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {

    private HotelDAO hotelDAO = new HotelDAO();
    private UserDAO userDAO = new UserDAO();
    private CurrentUser currentUser = new CurrentUser();
    private boolean flagLogin;

    public void userRegistration(User user) {
        if (this.userDAO.checkRegistration(user)) {
            this.userDAO.save(user);
            System.out.println("User " + user.getName() + " " + user.getLastName() + " successfully registered");
        }
    }

    public List<Hotel> findHotelByHotelName(String hotelName) {
        List<Hotel> hotelList = new ArrayList<>();
        if (this.flagLogin) {
            hotelList = this.hotelDAO.getList().stream()
                    .filter(n -> hotelName.equals(n.getHotelName()))
                    .collect(Collectors.toList());
            if (hotelList.size() != 0) {
                return hotelList;
            } else {
                System.out.println("Hotel " + hotelName + " is not found");
                return hotelList;
            }
        } else {
            System.out.println("Perform user authentication. Use the method \"login\"");
            return hotelList;
        }
    }
    public List<Hotel> getHotelList() {
        return hotelDAO.getList();
    }

    public List<Hotel> findHotelByCity(String hotelCity) {
        List<Hotel> hotelList = new ArrayList<>();
        if (this.flagLogin) {
            hotelList = this.hotelDAO.getList().stream()
                    .filter(n -> hotelCity.equals(n.getCityName()))
                    .collect(Collectors.toList());
            if (hotelList.size() != 0) {
                System.out.println("A list with all the hotels in " + hotelCity);
                return hotelList;
            } else {
                System.out.println("No hotels found in city: " + hotelCity); // ✅ fix here
                return hotelList;
            }
        } else {
            System.out.println("Perform user authentication. Use the method \"login\"");
            return hotelList;
        }

    }

    public List<Room> getFreeRoomsByHotel(String nameHotel) {
        List<Room> list = new ArrayList<>();
        Iterator<Hotel> iterator = this.hotelDAO.getList().iterator();
        if (this.flagLogin) {
            while (iterator.hasNext()) {
                Hotel h = iterator.next();
                if (h.getHotelName().equals(nameHotel)) {
                    System.out.println("The hotel " + nameHotel + " has the following rooms available:");
                    list = h.getRooms().stream()
                            .filter(r -> r.getUserReserved() == null)
                            .collect(Collectors.toList());
                }
            }
            if (list.isEmpty()) {
                System.out.println("Hotel with a name " + nameHotel + " not found");
            }
            return list;
        } else {
            System.out.println("Perform user authentication. Use the method \"login\"");
            return list;
        }
    }

    public void bookRoom(int roomId, int userId, int hotelId) {
        if (this.flagLogin) {
            Hotel foundHotel = this.hotelDAO.objectById(hotelId);
            if (foundHotel != null) {
                Room foundRoom;
                List<Room> listFoundRooms = foundHotel.getRooms().stream()
                        .filter(room -> room.getId() == roomId)
                        .collect(Collectors.toList());
                if (listFoundRooms.size() == 0 || listFoundRooms.size() >= 2) {
                    System.out.println("Error. With room ID: " + roomId + " found more that 1 room...");
                    foundRoom = null;
                } else {
                    foundRoom = listFoundRooms.get(0);
                }
                if (foundRoom != null) {
                    User foundUser = this.userDAO.objectById(userId);
                    if (foundUser != null) {
                        foundRoom.setUserReserved(foundUser);
                        System.out.println("Room reserved successfully");

                    }
                }
            } else {
                System.out.println("Sorry, hotel not found");
            }
        } else {
            System.out.println("Perform user authentication. Use the method \"login\"");
        }
    }

    public void cancelReservation(long roomId, int hotelId) {
        if (this.flagLogin) {
            Hotel foundHotel = this.hotelDAO.objectById(hotelId);
            if (foundHotel != null) {
                Room foundRoom;
                List<Room> listFoundRooms = foundHotel.getRooms().stream()
                        .filter(room -> room.getId() == roomId)
                        .collect(Collectors.toList());
                if (listFoundRooms.size() == 0 || listFoundRooms.size() >= 2) {
                    System.out.println("Error. With room ID: " + roomId + " found more that 1 room...");
                    foundRoom = null;
                } else {
                    foundRoom = listFoundRooms.get(0);
                }
                if (foundRoom != null && foundRoom.getUserReserved() != null) {
                    foundRoom.setUserReserved(null);
                    System.out.println("Room cancelled successfully");

                }
            } else {
                System.out.println("Sorry, hotel not found");
            }
        } else {
            System.out.println("Perform user authentication. Use the method \"login\"");
        }
    }

    public List findRoom(Map<String, String> params) {
        List<Room> roomList = new ArrayList<>();
        if (this.flagLogin) {
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue();
                    switch (fieldName) {
                        case "price":
                            roomList = this.hotelDAO.getAllRoom().stream()
                                    .filter(r -> r.getPrice() == Integer.valueOf(fieldValue))
                                    .collect(Collectors.toList());
                            break;
                        case "floor":
                            roomList.addAll(this.hotelDAO.getAllRoom().stream()
                                    .filter(x -> x.getFloor() == Integer.valueOf(fieldValue))
                                    .collect(Collectors.toList()));
                            break;
                        case "capacity":
                            roomList.addAll(this.hotelDAO.getAllRoom().stream()
                                    .filter(x -> x.getCapacity() == Integer.valueOf(fieldValue))
                                    .collect(Collectors.toList()));
                            break;
                        default:
                            System.out.println("Parameter \'" + fieldName + "\' given to method FindRoom() is wrong. Interrupted");
                    }
                }
                return roomList;
            } catch (NumberFormatException | NullPointerException e) {
                System.out.println("Input parameters should be of type string");
                return roomList;
            }
        } else {
            System.out.println("Perform user authentication. Use the method \"login\"");
            return roomList;
        }
    }

    public void login(int id) {
        this.flagLogin = false;
        try {
            this.currentUser.setCurrentUser(this.userDAO.objectById(id));
            this.flagLogin = true;
        } catch (NullPointerException e) {
            System.out.println("Sorry. This user does not exist");
        }
    }

    public List<Hotel> getAllHotel() {
        return this.hotelDAO.getList();
    }
}

