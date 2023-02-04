package nextstep.subway.unit;

import nextstep.subway.common.exception.*;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static nextstep.subway.common.error.SubwayError.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 구간 도메인 기능 테스트")
class SectionsTest {

    private static final String 신분당선 = "신분당선";
    private static final String 빨간색 = "bg-red-600";
    private static final Station 강남역 = new Station(1L, "강남역");
    private static final Station 양재역 = new Station(2L, "양재역");
    private static final Station 몽촌토성역 = new Station(3L, "몽촌토성역");
    private static final Station 검암역 = new Station(4L, "검암역");
    private static final Station 부평역 = new Station(5L, "부평역");

    @DisplayName("새로운 역을 구간의 하행 종점역으로 등록한다.")
    @Test
    void addDownSection() {
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, 강남역, 양재역, 10);
        final Sections 구간들 = 노선_신분당선.getSections();
        구간들.addSection(노선_신분당선, 양재역, 몽촌토성역, 4);

        assertAll(
                () -> assertThat(구간들.getSections()).hasSize(2),
                () -> assertThat(구간들.getSections().get(0).getUpStation()).isEqualTo(강남역),
                () -> assertThat(구간들.getSections().get(0).getDownStation()).isEqualTo(양재역),
                () -> assertThat(구간들.getSections().get(0).getDistance()).isEqualTo(new Distance(10)),
                () -> assertThat(구간들.getSections().get(1).getUpStation()).isEqualTo(양재역),
                () -> assertThat(구간들.getSections().get(1).getDownStation()).isEqualTo(몽촌토성역),
                () -> assertThat(구간들.getSections().get(1).getDistance()).isEqualTo(new Distance(4))
        );
    }

    @DisplayName("새로운 역을 구간의 상행 종점역으로 등록한다.")
    @Test
    void addUpSection() {
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, 강남역, 양재역, 10);
        final Sections 구간들 = 노선_신분당선.getSections();
        구간들.addSection(노선_신분당선, 검암역, 강남역, 4);

        assertAll(
                () -> assertThat(구간들.getSections()).hasSize(2),
                () -> assertThat(구간들.getSections().get(0).getUpStation()).isEqualTo(검암역),
                () -> assertThat(구간들.getSections().get(0).getDownStation()).isEqualTo(강남역),
                () -> assertThat(구간들.getSections().get(0).getDistance()).isEqualTo(new Distance(4)),
                () -> assertThat(구간들.getSections().get(1).getUpStation()).isEqualTo(강남역),
                () -> assertThat(구간들.getSections().get(1).getDownStation()).isEqualTo(양재역),
                () -> assertThat(구간들.getSections().get(1).getDistance()).isEqualTo(new Distance(10))
        );
    }

    @DisplayName("구간 역 사이에 새로운 역을 등록한다.")
    @Test
    void addMiddleSection() {
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, 강남역, 양재역, 10);
        final Sections 구간들 = 노선_신분당선.getSections();
        구간들.addSection(노선_신분당선, 강남역, 검암역, 4);

        assertAll(
                () -> assertThat(구간들.getSections()).hasSize(2),
                () -> assertThat(구간들.getSections().get(0).getUpStation()).isEqualTo(강남역),
                () -> assertThat(구간들.getSections().get(0).getDownStation()).isEqualTo(검암역),
                () -> assertThat(구간들.getSections().get(0).getDistance()).isEqualTo(new Distance(6)),
                () -> assertThat(구간들.getSections().get(1).getUpStation()).isEqualTo(검암역),
                () -> assertThat(구간들.getSections().get(1).getDownStation()).isEqualTo(양재역),
                () -> assertThat(구간들.getSections().get(1).getDistance()).isEqualTo(new Distance(4))
        );
    }

    @DisplayName("요청한 상행역과 하행역이 이미 노선에 등록되어 있어서 추가가 불가하다.")
    @Test
    void error_addSection() {
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, 강남역, 양재역, 10);

        final Sections 구간들 = 노선_신분당선.getSections();
        assertThatThrownBy(() -> 구간들.addSection(노선_신분당선, 강남역, 양재역, 10))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessage(NO_REGISTER_EXIST_STATION.getMessage());
    }

    @DisplayName("요청한 상행역과 하행역 모두 노선에 등록되어 있지 않아서 추가가 불가하다.")
    @Test
    void error_addSection_2() {
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, 강남역, 양재역, 10);
        final Sections 구간들 = 노선_신분당선.getSections();
        구간들.addSection(노선_신분당선, 양재역, 몽촌토성역, 10);

        assertThatThrownBy(() -> 구간들.addSection(노선_신분당선, 검암역, 부평역, 10))
                .isInstanceOf(NoExistStationException.class)
                .hasMessage(NO_REGISTER_NO_EXIST_STATION.getMessage());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void error_addSection_3() {
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, 강남역, 양재역, 10);

        final Sections 구간들 = 노선_신분당선.getSections();
        assertThatThrownBy(() -> 구간들.addSection(노선_신분당선, 강남역, 부평역, 10))
                .isInstanceOf(DistanceGreaterThanException.class)
                .hasMessage(NO_REGISTER_DISTANCE_GREATER_THAN.getMessage());
    }

    @DisplayName("기존 등록된 구간을 삭제한다.")
    @Test
    void removeSection() {
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, 강남역, 양재역, 10);
        final Sections 구간들 = new Sections(노선_신분당선.getSectionsList());
        구간들.addSection(노선_신분당선, 양재역, 몽촌토성역, 10);

        구간들.removeSection(몽촌토성역);

        assertAll(
                () -> assertThat(구간들.getSections()).hasSize(1)
        );
    }

    @DisplayName("노선 구간 제거 시 구간이 하나인 경우 삭제 불가로 구간 삭제가 불가능하다.")
    @Test
    void error_removeSection() {
        final Section 첫번째_구간 = new Section(1L, 강남역, 양재역, 10);
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, new Sections(List.of(첫번째_구간)));
        final Sections 구간들 = new Sections(노선_신분당선.getSectionsList());

        assertThatThrownBy(() -> 구간들.removeSection(양재역))
                .isInstanceOf(NoDeleteOneSectionException.class)
                .hasMessage(NO_DELETE_ONE_SECTION.getMessage());
    }

    @DisplayName("노선 구간 제거 시 등록된 하행 종점역이 아니어서 구간 삭제가 불가능하다.")
    @ParameterizedTest(name = "{0}은 노선의 하행종점역이 아닙니다.")
    @MethodSource("provideDeleteStation")
    void error_removeSection_2(final Station deleteStation) {
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, 강남역, 양재역, 10);
        final Sections 구간들 = new Sections(노선_신분당선.getSectionsList());
        구간들.addSection(노선_신분당선, 양재역, 몽촌토성역, 10);

        assertThatThrownBy(() -> 구간들.removeSection(deleteStation))
                .isInstanceOf(NoRegisterStationException.class)
                .hasMessage(NO_REGISTER_LAST_LINE_STATION.getMessage());
    }

    private static Stream<Arguments> provideDeleteStation() {
        return Stream.of(
                Arguments.of(강남역.getName(), 강남역),
                Arguments.of(양재역.getName(), 양재역)
        );
    }
}