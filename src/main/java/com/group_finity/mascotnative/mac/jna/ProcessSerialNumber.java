package com.group_finity.mascotnative.mac.jna;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"highLongOfPSN", "lowLongOfPSN"})
public class ProcessSerialNumber extends Structure {

    public long highLongOfPSN;
    public long lowLongOfPSN;

}
