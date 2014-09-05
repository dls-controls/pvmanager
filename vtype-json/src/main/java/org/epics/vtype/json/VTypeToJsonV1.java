/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.vtype.json;

import javax.json.JsonObject;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VBooleanArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VType;

/**
 * 
 * @author carcassi
 */
class VTypeToJsonV1 {

    static VType toVType(JsonObject json) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    static JsonObject toJson(VType vType) {
        if (vType instanceof VNumber) {
            return toJson((VNumber) vType);
        } else if (vType instanceof VNumberArray) {
            return toJson((VNumberArray) vType);
        } else if (vType instanceof VBoolean) {
            return toJson((VBoolean) vType);
        } else if (vType instanceof VBooleanArray) {
            return toJson((VBooleanArray) vType);
        } else if (vType instanceof VString) {
            return toJson((VString) vType);
        } else if (vType instanceof VStringArray) {
            return toJson((VStringArray) vType);
        } else if (vType instanceof VEnum) {
            return toJson((VEnum) vType);
        } else if (vType instanceof VEnumArray) {
            return toJson((VEnumArray) vType);
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    static JsonObject toJson(VNumber vNumber) {
        return new JsonVTypeBuilder()
                .addType(vNumber)
                .addObject("value", vNumber.getValue())
                .addAlarm(vNumber)
                .addTime(vNumber)
                .addDisplay(vNumber)
                .build();
    }
    
    static JsonObject toJson(VNumberArray vNumberArray) {
        return new JsonVTypeBuilder()
                .addType(vNumberArray)
                .addObject("value", vNumberArray.getData())
                .addAlarm(vNumberArray)
                .addTime(vNumberArray)
                .addDisplay(vNumberArray)
                .build();
    }
    
    static JsonObject toJson(VBoolean vBoolean) {
        return new JsonVTypeBuilder()
                .addType(vBoolean)
                .add("value", vBoolean.getValue())
                .addAlarm(vBoolean)
                .addTime(vBoolean)
                .build();
    }
    
    static JsonObject toJson(VBooleanArray vBooleanArray) {
        return new JsonVTypeBuilder()
                .addType(vBooleanArray)
                .addObject("value", vBooleanArray.getData())
                .addAlarm(vBooleanArray)
                .addTime(vBooleanArray)
                .build();
    }
    
    static JsonObject toJson(VString vString) {
        return new JsonVTypeBuilder()
                .addType(vString)
                .add("value", vString.getValue())
                .addAlarm(vString)
                .addTime(vString)
                .build();
    }
    
    static JsonObject toJson(VStringArray vStringArray) {
        return new JsonVTypeBuilder()
                .addType(vStringArray)
                .addListString("value", vStringArray.getData())
                .addAlarm(vStringArray)
                .addTime(vStringArray)
                .build();
    }
    
    static JsonObject toJson(VEnum vEnum) {
        return new JsonVTypeBuilder()
                .addType(vEnum)
                .add("value", vEnum.getValue())
                .addAlarm(vEnum)
                .addTime(vEnum)
                .addEnum(vEnum)
                .build();
    }
    
    static JsonObject toJson(VEnumArray vEnum) {
        return new JsonVTypeBuilder()
                .addType(vEnum)
                .addListString("value", vEnum.getData())
                .addAlarm(vEnum)
                .addTime(vEnum)
                .addEnum(vEnum)
                .build();
    }
}
