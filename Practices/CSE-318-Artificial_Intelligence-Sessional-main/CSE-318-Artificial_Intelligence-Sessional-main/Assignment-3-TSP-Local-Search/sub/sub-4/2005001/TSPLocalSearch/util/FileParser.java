package util;

import problem.TSP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    private static class Point {
        int id;
        double x;
        double y;
        Point(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    public static TSP parseTSPFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        String name = "unnamed", type, edge_weight_type;
        int dimension = 0;
        List<Point> points = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty()) continue;

            if (line.startsWith("NAME")) {
                name = line.split(":")[1].trim();
            }
            if (line.startsWith("TYPE")) {
                type = line.split(":")[1].trim();
                if(!type.equals("TSP")){
                    System.out.println("Sorry! Please provide a TSP file.");
                    return null;
                }
            }
            if (line.startsWith("EDGE_WEIGHT_TYPE")) {
                edge_weight_type = line.split(":")[1].trim();
                if(!edge_weight_type.equals("EUC_2D")){
                    System.out.println("Sorry! Please provide a TSP file with EUC_2D edge weights.");
                    return null;
                }
            }

            if (line.startsWith("DIMENSION")) {
                dimension = Integer.parseInt(line.split(":")[1].trim());
            }

            if (line.equals("NODE_COORD_SECTION")) {
                break;
            }
        }

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.equals("EOF")) break;

            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 3) {
                int id = Integer.parseInt(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                points.add(new Point(id, x, y));
            }
        }
        reader.close();

        TSP tsp = new TSP(name, points.size());

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                Point p1 = points.get(i);
                Point p2 = points.get(j);
                double d = calculate2DEuclideanDistance(p1, p2);
                tsp.setDistance(p1.id, p2.id, d);
                tsp.setDistance(p2.id, p1.id, d);
            }
        }

        return tsp;
    }

    private static double calculate2DEuclideanDistance(Point p1, Point p2) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
