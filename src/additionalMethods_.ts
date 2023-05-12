import * as Utils from "./additionalMethods";

export function loadData(data:string, floorplan: [Floorplan], graph: Utils.WeightedGraph){
        floorplan.forEach(room => {
            graph.addVertex(room.roomIdentifier);
            room.nextRooms.forEach(neighbour => graph.addEdge(neighbour.neighbourIdentifier, neighbour.distance))
        });
        return graph;
}

export function findShortestPath(start: string, end: string){
    let res = this.graph!.Dijkstra(start, end);
    res.splice(res.findIndex(e => e === end)+1);
    res.shift();
    const finalPath = [];
    finalPath[0] = start;
    return finalPath.concat(res);
}

export function getCurrentRoom(beacon: Beacon): Floorplan|null {
    const currentRoom = this.floorplan.find(el => el.beacon.id1 === beacon.id1 && el.beacon.id2 === beacon.id2 && el.beacon.id3 === beacon.id3);
    return currentRoom !== undefined?currentRoom:null;
}

export interface Beacon {
    distance: number;
    serviceUuid: number;
    id1: number;
    id2: number;
    id3: number;
    dataFields: [number];
}

export interface Floorplan {
    roomIdentifier: string;
    nextRooms:[{
        neighbourIdentifier: string;
        distance: number;
    }];
    beacon: Beacon;
}