package wooteco.subway.path.domain;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import wooteco.subway.line.domain.Section;
import wooteco.subway.station.domain.Station;

import java.util.List;

public class SubwayGraph {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public SubwayGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        this.graph = graph;
        initPath(sections);
    }

    private void initPath(List<Section> sections) {
        for (Section section : sections) {
            initEdge(section);
        }
    }

    private void initEdge(Section section) {
        addVertex(section.getUpStation());
        addVertex(section.getDownStation());
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    private void addVertex(Station station) {
        if (!graph.containsVertex(station)) {
            graph.addVertex(station);
        }
    }

    public WeightedMultigraph<Station, DefaultWeightedEdge> getGraph() {
        return graph;
    }
}
