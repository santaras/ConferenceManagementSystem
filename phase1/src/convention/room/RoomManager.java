package convention.room;

import java.io.Serializable;
import java.util.*;

import convention.calendar.CalendarManager;
import convention.exception.*;

/**
 * Manages room entities
 */
public class RoomManager implements Serializable {

    private Map<UUID, Room> rooms;

    /**
     * Room Locations must be non-empty; this method tests for that condition
     *
     * @param name name to test
     * @return true iff the location is valid
     */
    private boolean validateRoomLocation(String name) {
        return name.length() > 0;
    }

    /**
     * Room capacities must be positive and non-zero; this method tests for that condition
     *
     * @param  capacity the capacity of the room
     * @return true iff the capacity is valid
     */
    private boolean validateRoomCapacity(int capacity) {
        return capacity > 0;
    }

    public RoomManager(Map<UUID, Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * Gets a set of all the Room UUIDs in the system.
     *
     * @return set of UUIDs
     */
    public Set<UUID> getRooms() {
        return new HashSet<>(rooms.keySet());
    }

    /**
     * Gets the CalendarManager for the Room given its roomUUID.
     *
     * @param roomUUID the UUID of a specific room
     * @return the CalendarManager associated with this room
     */
    public CalendarManager getCalendarManager(UUID roomUUID) {
        return new CalendarManager(getRoom(roomUUID).getCalendar());
    }

    /**
     * Checks if the Room with a given UUID exists.
     *
     * @param roomUUID the UUID of a specific room
     * @return True or False
     */
    public boolean roomExists(UUID roomUUID) {
         return rooms.containsKey(roomUUID);
    }

    private Room getRoom(UUID roomUUID) {
        if (!roomExists(roomUUID)) {
            throw new NullRoomException(roomUUID);
        }
        return rooms.get(roomUUID);
    }

    /**
     * Returns the UUID of the new Room created.
     *
     * @param roomLocation the provided location for the new Room
     * @param roomCapacity the provided capacity for the new Room
     * @return the UUID of the newly created Room
     */
    public UUID createRoom(String roomLocation, int roomCapacity) {
        if (!validateRoomLocation(roomLocation)) {
            throw new InvalidNameException();
        }
        if (!validateRoomCapacity(roomCapacity)){
            throw new InvalidCapacityException();
        }
        Room room = new Room(roomLocation, roomCapacity);// make the room here and stuff
        rooms.put(room.getUUID(), room);

        return room.getUUID();
    }

    /**
     * Sets the Room location to the provided new location.
     *
     * @param roomUUID the Room whose location is to be set
     * @param roomLocation the new location to which the Room has shifted
     */
    public void setRoomLocation(UUID roomUUID, String roomLocation) {
        if (!roomExists(roomUUID)){
            throw new NullRoomException(roomUUID);
        }

        getRoom(roomUUID).setRoomLocation(roomLocation);
    }

    /**
     * Sets the Room capacity.
     *
     * @param roomUUID the Room whose capacity is to be set
     * @param capacity the new capacity of the Room
     */
    public void setRoomCapacity(UUID roomUUID, int capacity) {
        if (!roomExists(roomUUID)){
            throw new NullRoomException(roomUUID);
        }

        getRoom(roomUUID).setCapacity(capacity);
    }

    /**
     * Deletes a Room with the given UUID.
     *
     * @param roomUUID the UUID of the Room to be deleted
     */
    public void deleteRoom(UUID roomUUID) {
        if (!roomExists(roomUUID)) {
            throw new NullRoomException(roomUUID);
        }

        rooms.remove(roomUUID);
    }

    /**
     * Returns the location of the Room.
     *
     * @param roomUUID the UUID of the Room
     * @return the location of the Room
     */
    public String getRoomLocation(UUID roomUUID) {
        return getRoom(roomUUID).getRoomLocation();
    }

    /**
     * Returns the capacity of the Room.
     *
     * @param roomUUID the UUID of the Room
     * @return the capacity of the Room
     */
    public int getRoomCapacity(UUID roomUUID) {
        return getRoom(roomUUID).getCapacity();
    }

}
