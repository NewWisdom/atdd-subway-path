package wooteco.subway.path.domain.strategy;

import wooteco.subway.station.domain.Station;

import java.util.List;

public interface ShortestPathStrategy {
    List<Station> getVertexList(Station source, Station target);

    double getPathWeight(Station source, Station target);
}
