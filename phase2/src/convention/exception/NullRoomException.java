package convention.exception;

import java.util.UUID;

/**
 * Thrown when an invalid room UUID is used
 */
public class NullRoomException extends RuntimeException {
    public NullRoomException(UUID roomUUID) {
        super(String.format("Room %s does not exist.", roomUUID));
    }
}
