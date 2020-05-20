package wooteco.subway.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.CriteriaType;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.req.PathRequest;
import wooteco.subway.admin.dto.res.GraphResultResponse;
import wooteco.subway.admin.dto.res.PathResponse;
import wooteco.subway.admin.dto.res.StationResponse;
import wooteco.subway.admin.exception.DuplicateStationException;
import wooteco.subway.admin.exception.ErrorCode;
import wooteco.subway.admin.exception.StationNotFoundException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final PathStrategy pathStrategy;

    public PathService(LineRepository lineRepository,
        StationRepository stationRepository, PathStrategy pathStrategy) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.pathStrategy = pathStrategy;
    }

    @Transactional(readOnly = true)
    public PathResponse showPaths(PathRequest pathRequest) {
        String source = pathRequest.getSource();
        String target = pathRequest.getTarget();
        CriteriaType criteria = pathRequest.getCriteria();
        validateSameStations(source, target);

        List<Line> lines = lineRepository.findAll();
        Station from = stationRepository.findByName(source)
            .orElseThrow(() -> new StationNotFoundException(ErrorCode.NOT_EXIST_STATION));
        Station to = stationRepository.findByName(target)
            .orElseThrow(() -> new StationNotFoundException(ErrorCode.NOT_EXIST_STATION));

        GraphResultResponse result = pathStrategy.getPath(lines, from.getId(), to.getId(), criteria);
        List<Station> stations = stationRepository.findAllById(result.getStationIds());
        List<StationResponse> stationResponses = StationResponse.listOf(stations);

        List<StationResponse> sortedStationResponses = sort(result.getStationIds(), stationResponses);

        return new PathResponse(sortedStationResponses, result.getDistance(), result.getDuration());
    }

    private void validateSameStations(String source, String target) {
        if (source.equalsIgnoreCase(target)) {
            throw new DuplicateStationException(ErrorCode.SOURCE_TARGET_SAME);
        }
    }

    private List<StationResponse> sort(List<Long> path, List<StationResponse> stationResponses) {
        List<StationResponse> result = new ArrayList<>();

        for (Long stationId : path) {
            StationResponse response = stationResponses.stream()
                .filter(stationResponse -> stationResponse.getId().equals(stationId))
                .findAny().orElseThrow(IllegalArgumentException::new);
            result.add(response);
        }
        return result;
    }
}