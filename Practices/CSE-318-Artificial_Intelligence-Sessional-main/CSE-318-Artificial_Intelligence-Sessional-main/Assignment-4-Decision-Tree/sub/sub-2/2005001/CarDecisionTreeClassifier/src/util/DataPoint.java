package util;

import org.w3c.dom.Attr;

import java.util.HashMap;

public class DataPoint {
    public HashMap<Attribute, String> attributeValues;
    public String label;

    public DataPoint(HashMap<Attribute, String> attributeValues, String label) {
        this.attributeValues = attributeValues;
        this.label = label;
    }
}
