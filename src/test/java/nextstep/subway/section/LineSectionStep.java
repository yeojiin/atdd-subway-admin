package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.line.LineAcceptanceStep.노선_한개_생성한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

public class LineSectionStep {

    public static ExtractableResponse<Response> 역_2개와_노선을_생성한다() {
        int upLastStationId = 지하철역을_생성한다("강남역").jsonPath().get("id");
        int downLastStationId = 지하철역을_생성한다("선릉역").jsonPath().get("id");

        return 노선_한개_생성한다(upLastStationId, downLastStationId);
    }

    public static ExtractableResponse<Response> 역_사이에_새로운_역을_등록한다(int lineId) {
        return RestAssured.given()
                .body(구간_하나를_생성한다()).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/" + lineId + "/sections")
                .then()
                .log().all()
                .extract();
    }

    private static SectionRequest 구간_하나를_생성한다() {
        int newStationId =  지하철역을_생성한다("역삼역").jsonPath().get("id");

        return SectionRequest.builder()
                .upStationId(1L)
                .downStationId(Long.valueOf(newStationId))
                .distance(4)
                .build();
    }


    public static void 구간_추가_성공_확인(ExtractableResponse<Response> savedSection) {
        assertThat(savedSection.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

}
