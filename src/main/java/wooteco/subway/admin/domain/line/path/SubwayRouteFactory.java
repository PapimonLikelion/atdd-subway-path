package wooteco.subway.admin.domain.line.path;

import java.util.Objects;

import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import wooteco.subway.admin.domain.line.LineStation;
import wooteco.subway.admin.domain.line.LineStations;

public class SubwayRouteFactory {
    private EdgeWeightStrategy edgeWeightStrategy;

    public SubwayRouteFactory(EdgeWeightStrategy edgeWeightStrategy) {
        this.edgeWeightStrategy = edgeWeightStrategy;
    }

    public SubwayRoute create(LineStations lineStations, Long departureId, Long arrivalId) {
        WeightedGraph<Long, RouteEdge> graph = new WeightedMultigraph<>(RouteEdge.class);
        Graphs.addAllVertices(graph, lineStations.getStationIds());

        for (LineStation lineStation : lineStations.getStations()) {
            if (Objects.nonNull(lineStation.getPreStationId())) {
                RouteEdge edge = lineStation.toEdge();
                graph.addEdge(lineStation.getPreStationId(), lineStation.getStationId(), edge);
                edgeWeightStrategy.setWeight(graph, edge);
            }
        }

        return new SubwayRoute(DijkstraShortestPath.findPathBetween(graph, departureId, arrivalId));
    }
}
