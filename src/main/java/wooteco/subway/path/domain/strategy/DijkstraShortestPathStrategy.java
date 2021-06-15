package wooteco.subway.path.domain.strategy;

import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import wooteco.subway.path.domain.SubwayGraph;
import wooteco.subway.station.domain.Station;

import java.util.List;

public class DijkstraShortestPathStrategy implements ShortestPathStrategy {
    private final ShortestPathAlgorithm<Station, DefaultWeightedEdge> shortestPathAlgorithm;

    public DijkstraShortestPathStrategy(SubwayGraph subwayGraph) {
        this.shortestPathAlgorithm = new DijkstraShortestPath<>(subwayGraph.getGraph());
    }

    @Override
    public List<Station> getVertexList(Station source, Station target) {
        return shortestPathAlgorithm.getPath(source, target).getVertexList();
    }

    @Override
    public double getPathWeight(Station source, Station target) {
        return shortestPathAlgorithm.getPathWeight(source, target);
    }
}
