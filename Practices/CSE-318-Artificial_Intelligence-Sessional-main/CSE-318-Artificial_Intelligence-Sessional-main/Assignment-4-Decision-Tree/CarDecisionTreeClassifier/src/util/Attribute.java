package util;

import java.util.ArrayList;

public class Attribute {
    public String name;
    public ArrayList<String> possibleValues;

    public Attribute(String name, ArrayList<String> possibleValues) {
        this.name = name;
        this.possibleValues = possibleValues;
    }

    @Override
    public String toString() {
        return String.format("%s (%d possible values: %s)",
                name,
                possibleValues.size(),
                String.join(", ", possibleValues));
    }
}
