package de.homeinfogmbh.plugins.ble;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {

  private String name;

  private LinkedList<Node> shortestPath = new LinkedList<>();

  private Integer distance = Integer.MAX_VALUE;

  private String id1;
  private String id2;
  private String id3;

  private Map<Node, Integer> adjacentNodes = new HashMap<>();

  public Node(String name, String id1, String id2, String id3) {
    this.name = name;
    this.id1 = id1;
    this.id2 = id2;
    this.id3 = id3;
  }

  public void addDestination(Node destination, int distance) {
    adjacentNodes.put(destination, distance);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<Node, Integer> getAdjacentNodes() {
    return adjacentNodes;
  }

  public void setAdjacentNodes(Map<Node, Integer> adjacentNodes) {
    this.adjacentNodes = adjacentNodes;
  }

  public String getId1(){
    return id1;
  }
  public String getId2(){
    return id2;
  }
  public String getId3(){
    return id3;
  }

  public void setId1(String id){
    id1 = id;
  }
  public void getId2(String id){
    id2 = id;
  }
  public void getId3(String id){
    id3 = id;
  }


  public Integer getDistance() {
    return distance;
  }

  public void setDistance(Integer distance) {
    this.distance = distance;
  }

  public List<Node> getShortestPath() {
    return shortestPath;
  }

  public void setShortestPath(LinkedList<Node> shortestPath) {
    this.shortestPath = shortestPath;
  }

}
