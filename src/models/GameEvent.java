package models;

import enums.GameEventType;

public class GameEvent {

    private final GameEventType type;
    private final String        message;
    private final District      targetDistrict; 

    public GameEvent(GameEventType type, String message, District targetDistrict) {
        this.type           = type;
        this.message        = message;
        this.targetDistrict = targetDistrict;
    }

    public GameEventType getType()            { return type; }
    public String        getMessage()         { return message; }
    public District      getTargetDistrict()  { return targetDistrict; }

    @Override
    public String toString() {
        return "[" + type.getDisplayName() + "] " + message;
    }
}
