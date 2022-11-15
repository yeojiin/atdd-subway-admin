package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.CannotFindException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.constant.Message.NOT_FOUND_LINE_ERR;
import static nextstep.subway.constant.Message.NOT_FOUND_UP_STATION_ERR;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStationById(Long.valueOf(lineRequest.getUpLastStationId()));
        Station downStation = findStationById(Long.valueOf(lineRequest.getDownLastStationId()));

        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.of(lines);
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(()-> new CannotFindException(NOT_FOUND_LINE_ERR));
        return LineResponse.of(line);
    }

    // @Transactional 이 있어야 update문 탐
    @Transactional
    public void updateLineById(Long id, LineRequest updateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(()-> new CannotFindException(NOT_FOUND_LINE_ERR));

        if(updateRequest.getUpLastStationId() > 0) {
            line.changeUpStation(findStationById(Long.valueOf(updateRequest.getUpLastStationId())));
        }

        if(updateRequest.getDownLastStationId() > 0) {
            line.changeDownStation(findStationById(Long.valueOf(updateRequest.getDownLastStationId())));
        }

        line.update(updateRequest);

        // save 없어도 처리
//        lineRepository.save(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new CannotFindException(NOT_FOUND_UP_STATION_ERR));
    }
}
