package nextstep.subway.domain.exception;

public class CannotAddSectionException extends RuntimeException {

    private static final String EXIST_BOTH_MESSAGE = "추가하려는 역이 이미 노선에 존재합니다. %s, %s";
    private static final String NON_EXISTS_MESSAGE = "기존 구간과 연결되는 역이 없습니다.";
    private static final String INVALID_DISTANCE_MESSAGE = "기존 구간의 길이보다 같거나 크면 추가할 수 없습니다. 기존 구간의 길이 : %d, 신규 구간의 길이 : %d";

    public CannotAddSectionException() {
        super(NON_EXISTS_MESSAGE);
    }

    public CannotAddSectionException(String upStationName, String downStationName) {
        super(String.format(EXIST_BOTH_MESSAGE, upStationName, downStationName));
    }

    public CannotAddSectionException(int targetDistance, int distance) {
        super(String.format(INVALID_DISTANCE_MESSAGE, targetDistance, distance));
    }
}