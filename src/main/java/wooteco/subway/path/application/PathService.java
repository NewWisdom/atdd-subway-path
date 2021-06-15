package wooteco.subway.path.application;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.path.domain.Path;
import wooteco.subway.path.domain.ShortestPathFinder;
import wooteco.subway.path.domain.SubwayGraph;
import wooteco.subway.path.domain.strategy.DijkstraShortestPathStrategy;
import wooteco.subway.path.domain.strategy.ShortestPathStrategy;
import wooteco.subway.path.dto.PathResponse;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public PathResponse findPath(Long sourceId, Long targetId) {
        List<Section> sections = sectionDao.findByStationIds(Arrays.asList(sourceId, targetId));
        SubwayGraph subwayGraph = new SubwayGraph(new WeightedMultigraph<>(DefaultWeightedEdge.class), sections);
        ShortestPathFinder shortestPathFinder = new ShortestPathFinder(new DijkstraShortestPathStrategy(subwayGraph));
        Path shortestPath = shortestPathFinder.findShortestPath(new Station(sourceId), new Station(targetId));
        return new PathResponse(StationResponse.listOf(combineStationById(shortestPath)), (int) shortestPath.distance());
    }

    private List<Station> combineStationById(Path path) {
        List<Long> stationIds = path.stations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());
        return stationDao.findByIds(stationIds).sortedStation(stationIds);
    }
}
