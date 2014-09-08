/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.vtype.json;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import org.epics.util.array.ArrayBoolean;
import org.epics.util.array.ArrayByte;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VBooleanArray;
import org.epics.vtype.VDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VFloat;
import org.epics.vtype.VInt;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VType;
import org.epics.vtype.VTypeValueEquals;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.epics.vtype.ValueFactory.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author carcassi
 */
public class VTypeToJsonTest {

    public void compareJson(JsonObject json, String text) {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(writer);
        jsonWriter.writeObject(json);
        assertThat(writer.toString(), equalTo(text));
    }

    @Test
    public void testVDouble() {
        VDouble vDouble = newVDouble(3.14, alarmNone(), newTime(Timestamp.of(0, 0)), displayNone());
        JsonObject json = VTypeToJson.toJson(vDouble);
        compareJson(json, "{\"type\":{\"name\":\"VDouble\",\"version\":1},\"value\":3.14,\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"lowAlarm\":null,\"highAlarm\":null,\"lowDisplay\":null,\"highDisplay\":null,\"lowWarning\":null,\"highWarning\":null,\"units\":\"\"}}");
    }

    @Test
    public void testVInt() {
        VInt vInt = newVInt(314, alarmNone(), newTime(Timestamp.of(0, 0)), displayNone());
        JsonObject json = VTypeToJson.toJson((VType) vInt);
        compareJson(json, "{\"type\":{\"name\":\"VInt\",\"version\":1},\"value\":314,\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"lowAlarm\":null,\"highAlarm\":null,\"lowDisplay\":null,\"highDisplay\":null,\"lowWarning\":null,\"highWarning\":null,\"units\":\"\"}}");
    }

    @Test
    public void testVBoolean() {
        VBoolean vBoolean = newVBoolean(true, alarmNone(), newTime(Timestamp.of(0, 0)));
        JsonObject json = VTypeToJson.toJson((VType) vBoolean);
        compareJson(json, "{\"type\":{\"name\":\"VBoolean\",\"version\":1},\"value\":true,\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"unixSec\":0,\"nanoSec\":0,\"userTag\":null}}");
    }

    @Test
    public void testVString() {
        VString vString = newVString("Flower", alarmNone(), newTime(Timestamp.of(0, 0)));
        JsonObject json = VTypeToJson.toJson((VType) vString);
        compareJson(json, "{\"type\":{\"name\":\"VString\",\"version\":1},\"value\":\"Flower\",\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"unixSec\":0,\"nanoSec\":0,\"userTag\":null}}");
    }

    @Test
    public void testVEnum() {
        VEnum vEnum = newVEnum(1, Arrays.asList("One", "Two", "Three"), alarmNone(), newTime(Timestamp.of(0, 0)));
        JsonObject json = VTypeToJson.toJson((VType) vEnum);
        compareJson(json, "{\"type\":{\"name\":\"VEnum\",\"version\":1},\"value\":\"Two\",\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"unixSec\":0,\"nanoSec\":0,\"userTag\":null},\"enum\":{\"labels\":[\"One\",\"Two\",\"Three\"]}}");
    }

    @Test
    public void testVEnumArray() {
        VEnumArray vEnumArray = newVEnumArray(new ArrayInt(1,0,1), Arrays.asList("One", "Two", "Three"), alarmNone(), newTime(Timestamp.of(0, 0)));
        JsonObject json = VTypeToJson.toJson((VType) vEnumArray);
        compareJson(json, "{\"type\":{\"name\":\"VEnumArray\",\"version\":1},\"value\":[\"Two\",\"One\",\"Two\"],\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"unixSec\":0,\"nanoSec\":0,\"userTag\":null},\"enum\":{\"labels\":[\"One\",\"Two\",\"Three\"]}}");
    }

    @Test
    public void testVDoubleArray() {
        VDoubleArray vDoubleArray = newVDoubleArray(new ArrayDouble(0.0, 0.1, 0.2), alarmNone(), newTime(Timestamp.of(0, 0)), displayNone());
        JsonObject json = VTypeToJson.toJson((VType) vDoubleArray);
        compareJson(json, "{\"type\":{\"name\":\"VDoubleArray\",\"version\":1},\"value\":[0.0,0.1,0.2],\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"lowAlarm\":null,\"highAlarm\":null,\"lowDisplay\":null,\"highDisplay\":null,\"lowWarning\":null,\"highWarning\":null,\"units\":\"\"}}");
    }

    @Test
    public void testVByteArray() {
        VNumberArray vByteArray = newVNumberArray(new ArrayByte(new byte[]{0, 1, 2}), alarmNone(), newTime(Timestamp.of(0, 0)), displayNone());
        JsonObject json = VTypeToJson.toJson((VType) vByteArray);
        compareJson(json, "{\"type\":{\"name\":\"VByteArray\",\"version\":1},\"value\":[0,1,2],\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"lowAlarm\":null,\"highAlarm\":null,\"lowDisplay\":null,\"highDisplay\":null,\"lowWarning\":null,\"highWarning\":null,\"units\":\"\"}}");
    }

    @Test
    public void testVStringArray() {
        VStringArray vStringArray = newVStringArray(Arrays.asList("A", "B", "C"), alarmNone(), newTime(Timestamp.of(0, 0)));
        JsonObject json = VTypeToJson.toJson((VType) vStringArray);
        compareJson(json, "{\"type\":{\"name\":\"VStringArray\",\"version\":1},\"value\":[\"A\",\"B\",\"C\"],\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"unixSec\":0,\"nanoSec\":0,\"userTag\":null}}");
    }

    @Test
    public void testVBooleanArray() {
        VBooleanArray vBooleanArray = newVBooleanArray(new ArrayBoolean(true, false, true), alarmNone(), newTime(Timestamp.of(0, 0)));
        JsonObject json = VTypeToJson.toJson((VType) vBooleanArray);
        compareJson(json, "{\"type\":{\"name\":\"VBooleanArray\",\"version\":1},\"value\":[true,false,true],\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"unixSec\":0,\"nanoSec\":0,\"userTag\":null}}");
    }

    @Test
    public void parseVDouble() {
        JsonObject json;
        try (JsonReader reader = Json.createReader(new StringReader("{\"type\":{\"name\":\"VDouble\",\"version\":1},\"value\":3.14,\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"lowAlarm\":null,\"highAlarm\":null,\"lowDisplay\":null,\"highDisplay\":null,\"lowWarning\":null,\"highWarning\":null,\"units\":\"\"}}"))) {
            json = reader.readObject();
        }
        VType vType = VTypeToJson.toVType(json);
        VDouble expected = newVDouble(3.14, alarmNone(), newTime(Timestamp.of(0, 0)), displayNone());
        assertThat("Value mismatch", VTypeValueEquals.valueEquals(expected, vType), equalTo(true));
    }

    @Test
    public void parseVFloat() {
        JsonObject json;
        try (JsonReader reader = Json.createReader(new StringReader("{\"type\":{\"name\":\"VFloat\",\"version\":1},\"value\":3.14,\"alarm\":{\"severity\":\"NONE\",\"status\":\"NONE\"},\"time\":{\"lowAlarm\":null,\"highAlarm\":null,\"lowDisplay\":null,\"highDisplay\":null,\"lowWarning\":null,\"highWarning\":null,\"units\":\"\"}}"))) {
            json = reader.readObject();
        }
        VType vType = VTypeToJson.toVType(json);
        VFloat expected = newVFloat((float) 3.14, alarmNone(), newTime(Timestamp.of(0, 0)), displayNone());
        assertThat("Value mismatch", VTypeValueEquals.valueEquals(expected, vType), equalTo(true));
    }
    
}
