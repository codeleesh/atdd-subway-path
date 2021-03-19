package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));
        Station 판교역 = stationRepository.save(new Station("판교역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red", 강남역, 양재역, 5));
        int expectedSize = 신분당선.getStations().size() + 1;

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 판교역.getId(), 5));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(신분당선.getStations().size()).isEqualTo(expectedSize);
        assertThat(신분당선.getStations())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(Arrays.asList(강남역, 양재역, 판교역));
    }

    @Test
    void removeSection() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));
        Station 판교역 = stationRepository.save(new Station("판교역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red", 강남역, 양재역, 5));
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 판교역.getId(), 5));

        // when
        lineService.removeSection(신분당선.getId(), 양재역.getId());

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(2);
        assertThat(신분당선.getStations())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(Arrays.asList(강남역, 판교역));
    }
}