package Location;

import javax.swing.*;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;


public class RouteMapGenerator {
    static class Place {
        String name;
        double x, y;

        Place(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return name; 
        }
    }

    public static void main(int userId, String username) {
        List<Place> places = new ArrayList<>();
        places.add(new Place("meenkshi",9.919500,78.11930));
        places.add(new Place("rameshwrm",9.288100 ,79.317400));
        places.add(new Place("brihadeeswr",10.782700 ,79.131500));
        places.add(new Place("marina",13.050000,80.282400));
        places.add(new Place("shore",12.616500,80.199300));
        printDistances(places);
        List<Place> shortestRoute = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Place startPlace : places) {
            List<Place> route = generateRoute(places, startPlace);
            double totalDistance = calculateTotalDistance(route);
            if (totalDistance < shortestDistance) {
                shortestRoute = route;
                shortestDistance = totalDistance;
            }
        }
        shortestRoute = optimizeRoute(shortestRoute);
        createRouteMapViewer(shortestRoute,userId,username);
    }

    static void createRouteMapViewer(List<Place> shortestRoute, int userId, String username) {
    	SwingUtilities.invokeLater(() -> new RouteMapViewer(shortestRoute, null, userId, username));
    }

    static List<Place> generateRoute(List<Place> places, Place startPlace) {
        List<Place> route = new ArrayList<>();
        List<Place> unvisited = new ArrayList<>(places);
        Place current = startPlace;
        route.add(current);
        unvisited.remove(current);

        while (!unvisited.isEmpty()) {
            Place nearestNeighbor = findNearestNeighbor(current, unvisited);
            route.add(nearestNeighbor);
            unvisited.remove(nearestNeighbor);
            current = nearestNeighbor;
        }
      //System.out.println("Generated Route: " + route);
        return route;
    }
    
    static Place findNearestNeighbor(Place current, List<Place> unvisited) {
        double minDistance = Double.MAX_VALUE;
        Place nearest = null;
        for (Place place : unvisited) {
            double distance = calculateDistance(current, place);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = place;
            }
        }
        return nearest;
    }

    static double calculateDistance(Place a, Place b) {
        final int R = 6371; 
        double latDistance = Math.toRadians(b.x - a.x);
        double lonDistance = Math.toRadians(b.y - a.y);
        double aHarv = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(a.x)) * Math.cos(Math.toRadians(b.x))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(aHarv), Math.sqrt(1 - aHarv));
        return R * c; 
    }

    static double calculateTotalDistance(List<Place> route) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += calculateDistance(route.get(i), route.get(i + 1));
        }
        return totalDistance;
    }

    static List<Place> optimizeRoute(List<Place> route) {
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            for (int i = 1; i < route.size() - 1; i++) {
                for (int j = i + 1; j < route.size(); j++) {
                    if (shouldSwap(route, i, j)) {
                        Collections.reverse(route.subList(i, j));
                        improvement = true;
                    }
                }
            }
        }
        return route;
    }

    static boolean shouldSwap(List<Place> route, int i, int j) {
        Place A = route.get(i - 1);
        Place B = route.get(i);
        Place C = route.get(j - 1);
        Place D = route.get(j);
        return calculateDistance(A, B) + calculateDistance(C, D) > calculateDistance(A, C) + calculateDistance(B, D);
    }
    public static void printDistances(List<Place> places) {
        for (int i = 0; i < places.size(); i++) {
            for (int j = i + 1; j < places.size(); j++) {
                double distance = calculateDistance(places.get(i), places.get(j));
              //System.out.println("Distance between " + places.get(i).name + " and " + places.get(j).name + ": " + distance + " km");
            }
        }
    }
}