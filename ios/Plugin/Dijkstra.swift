//
//  Dijkstra.swift
//  Bleindoorlocation
//  src: https://www.fivestars.blog/articles/dijkstra-algorithm-swift/
//  Created by sebastian on 09.06.23
//  @author: Sebastian Klaus, HOMEINFO GMBH
//

import Foundation

public class Node {
  var visited = false
  var connections: [Connection] = []
}

public class Connection {
  public let to: Node
  public let weight: Int

  public init(to node: Node, weight: Int) {
    assert(weight >= 0, "weight has to be equal or greater than zero")
    self.to = node
    self.weight = weight
  }
}

public class Path {
  public let cumulativeWeight: Int
  public let node: Node
  public let previousPath: Path?

  init(to node: Node, via connection: Connection? = nil, previousPath path: Path? = nil) {
    if
      let previousPath = path,
      let viaConnection = connection {
      self.cumulativeWeight = viaConnection.weight + previousPath.cumulativeWeight
    } else {
      self.cumulativeWeight = 0
    }

    self.node = node
    self.previousPath = path
  }
}

extension Path {
  var array: [Node] {
    var array: [Node] = [self.node]

    var iterativePath = self
    while let path = iterativePath.previousPath {
      array.append(path.node)

      iterativePath = path
    }

    return array
  }
}

func shortestPath(source: Node, destination: Node) -> Path? {
  var frontier: [Path] = [] {
    didSet { frontier.sort { return $0.cumulativeWeight < $1.cumulativeWeight } } // the frontier has to be always ordered
  }

  frontier.append(Path(to: source)) // the frontier is made by a path that starts nowhere and ends in the source

  while !frontier.isEmpty {
    let cheapestPathInFrontier = frontier.removeFirst() // getting the cheapest path available
    guard !cheapestPathInFrontier.node.visited else { continue } // making sure we haven't visited the node already

    if cheapestPathInFrontier.node === destination {
      return cheapestPathInFrontier // found the cheapest path 😎
    }

    cheapestPathInFrontier.node.visited = true

    for connection in cheapestPathInFrontier.node.connections where !connection.to.visited { // adding new paths to our frontier
      frontier.append(Path(to: connection.to, via: connection, previousPath: cheapestPathInFrontier))
    }
  } // end while
  return nil // we didn't find a path 😣
}



/////////////////////////////////////////////////////////////////

// **** EXAMPLE BELOW ****
class MyNode: Node {
  let name: String

  init(name: String) {
    self.name = name
    super.init()
  }
}

let nodeA = MyNode(name: "A")
let nodeB = MyNode(name: "B")
let nodeC = MyNode(name: "C")
let nodeD = MyNode(name: "D")
let nodeE = MyNode(name: "E")

nodeA.connections.append(Connection(to: nodeB, weight: 1))
nodeB.connections.append(Connection(to: nodeC, weight: 3))
nodeC.connections.append(Connection(to: nodeD, weight: 1))
nodeB.connections.append(Connection(to: nodeE, weight: 1))
nodeE.connections.append(Connection(to: nodeC, weight: 1))

let sourceNode = nodeA
let destinationNode = nodeD

var path = shortestPath(source: sourceNode, destination: destinationNode)

if let succession: [String] = path?.array.reversed().flatMap({ $0 as? MyNode}).map({$0.name}) {
  print("🏁 Quickest path: \(succession)")
} else {
  print("💥 No path between \(sourceNode.name) & \(destinationNode.name)")
}