package com.caltech.utils;

import java.util.UUID;
import org.bson.types.ObjectId;

public class ObjectIdUtils {

    public static String objectIdToString(ObjectId objectId) {
        return objectId.toHexString();
    }

    public static ObjectId stringToObjectId(String objectIdString) {
        return new ObjectId(objectIdString);
    }

    public static UUID generateUUIDFromObjectId(ObjectId objectId) {
        String objectIdString = objectId.toHexString();
        return UUID.nameUUIDFromBytes(objectIdString.getBytes());
    }
}
