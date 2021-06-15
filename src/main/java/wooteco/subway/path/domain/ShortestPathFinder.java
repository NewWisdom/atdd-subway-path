package wooteco.subway.path.domain;

import wooteco.subway.path.domain.strategy.ShortestPathStrategy;
import wooteco.subway.station.domain.Station;

public class ShortestPathFinder {
    private final ShortestPathStrategy shortestPathStrategy;

    public ShortestPathFinder(ShortestPathStrategy shortestPathStrategy) {
        this.shortestPathStrategy = shortestPathStrategy;
    }

    public Path findShortestPath(Station source, Station target) {
        return new Path(shortestPathStrategy.getVertexList(source, target), shortestPathStrategy.getPathWeight(source, target));
    }
}