/**
 * MIT License
 *
 *
 * Copyright (c) 2022 Sebastian Klaus, Homeinfo GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * */

/**
 * dijkstra algorithms Priority Queue
 * https://www.codeunderscored.com/dijkstras-algorithm-in-javascript/
 * */
class PriorityQueue{

  constructor(){
    this.values =[]
  }

  enqueue(val, priority){
    let newNode = new Node(val, priority);
    this.values.push(newNode);
    this.bubbleUp();
  }
  bubbleUp(){
    let idx = this.values.length -1;
    const element = this.values[idx];

    while(idx > 0){
      let parentIdx = Math.floor((idx -1 ) / 2);
      let parent = this.values[parentIdx];
      if(element.priority >= parent.priority ) break;
      this.values[parentIdx] = element;
      this.values[idx] = parent;
      idx = parentIdx;
    }
  }

  dequeue() {
    const min = this.values[0];
    const end = this.values.pop();
    if(this.values.length > 0){
      this.values[0] =end;
      this.sinkDown();
    }
    return min;

  }
  sinkDown(){
    let idx = 0;
    const length = this.values.length;
    const element = this.values[0];
    while(true){
      let leftChildIdx = 2 * idx +1;
      let rightChildIdx = 2* idx +2;
      let leftChild, rightChild;
      let swap = null;
      if(leftChildIdx < length ){
        leftChild = this.values[leftChildIdx];
        if(leftChild.priority < element.priority){
          swap = leftChildIdx;
        }
      }
      if(rightChildIdx < length){
        rightChild = this.values[rightChildIdx];
        if(
          (swap === null && rightChild.priority < element.priority ) ||
          (swap !== null && rightChild.priority < leftChild.priority)
        ){
          swap = rightChildIdx;
        }
      }
      if (swap === null) break;
      this.values[idx] =this.values[swap];
      this.values[swap] =element;
      idx =swap;
    }

  }
}

/**
 * dijkstra algorithms Node
 * https://www.codeunderscored.com/dijkstras-algorithm-in-javascript/
 * */
class Node {
  constructor(val, priority){
    this.val =val;
    this.priority =priority;
  }
}

/**
 * dijkstra algorithms weighted Graph
 * https://www.codeunderscored.com/dijkstras-algorithm-in-javascript/
 * */
export class WeightedGraph{

  constructor(){
    this.adjacencyList ={}
  }
  addVertex(vertex){
    if(!this.adjacencyList[vertex]) this.adjacencyList[vertex] =[];
  }
  addEdge(vertex1, vertex2, weight){
    this.adjacencyList[vertex1].push({node:vertex2, weight})
    this.adjacencyList[vertex2].push({node:vertex1,weight})
  }

  Dijkstra(start, finish){
    const nodes = new PriorityQueue();
    const distances = {};
    const previous = {};
    let path = [] // to return at end
    let smallest;

    // build up initial state
    for(let vertex in this.adjacencyList){
      if(vertex === start){
        nodes.enqueue(vertex, 0);
        distances[vertex] =0;
      }else{
        distances[vertex] =Infinity;
        nodes.enqueue(vertex, Infinity);
      }
      previous[vertex] =null;
    }

    // as long as there is something to visit
    while(nodes.values.length){
      smallest = nodes.dequeue().val;
      if (smallest === finish){
        // WE ARE DONE
        // BUILD UP PATH TO RETURN AT END
        while(previous[smallest]){
          path.push(smallest);
          smallest =previous[smallest];
        }
      }
      if(smallest || distances[smallest] !== Infinity){
        for(let neighbor in this.adjacencyList[smallest]){
          // find neighboring node
          let nextNode = this.adjacencyList[smallest][neighbor];
          // calculate new distance to neighboring node
          let candidate =distances[smallest] + nextNode.weight;
          let nextNeighbor = nextNode.node;

          if(candidate < distances[nextNeighbor]){
            // updating new smallest distance to neighbor
            distances[nextNeighbor] = candidate;
            // updating previous – How we got to neighbor
            previous[nextNeighbor] = smallest;
            // enqueue in priority queue with new priority
            nodes.enqueue(nextNeighbor, candidate);
          }
        }
      }

    }
    return path.concat(smallest).reverse();
  }

}


