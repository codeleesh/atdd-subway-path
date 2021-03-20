package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.PathRequest;
import nextstep.subway.line.dto.PathResponse;
import nextstep.subway.line.exception.NotExistPathInfoException;
import nextstep.subway.station.exception.EqualStationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {
    private LineService lineService;

    public PathController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping(value = "/paths")
    public ResponseEntity findShortestPath(@ModelAttribute PathRequest pathRequest) {
        PathResponse pathResponse = lineService.findShortestPath(pathRequest);
        return ResponseEntity.ok(pathResponse);
    }

    @ExceptionHandler({EqualStationException.class, NotExistPathInfoException.class, IllegalArgumentException.class})
    public ResponseEntity handleNotExistStation(Exception e) {
        return ResponseEntity.badRequest().build();
    }
}