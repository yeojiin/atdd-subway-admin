package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import nextstep.subway.domain.raw.Color;
import nextstep.subway.domain.raw.Name;
import nextstep.subway.dto.LineRequest;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static nextstep.subway.constant.Message.NOT_FOUND_SECTION_ERR;

@Entity
@Builder
@AllArgsConstructor
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Color color;

    @Embedded
    private Sections sections;

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = new Name(name);
        this.color = new Color(color);
        // Line과 매핑하기 위해 this를 넘겨줌
        this.sections = new Sections(new Section(upStation, downStation, this, distance));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
    }

    public List<Station> getStations() {
        return sections.getAllStations();
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void update(LineRequest updateRequest) {
        if (!updateRequest.getName().isEmpty() && updateRequest.getName() != "") {
            this.name = new Name(updateRequest.getName());
        }
        if (!updateRequest.getColor().isEmpty() && updateRequest.getColor() != "") {
            this.color = new Color(updateRequest.getColor());
        }
    }

    public void addSection(Section newSection) {
        Objects.requireNonNull(newSection, NOT_FOUND_SECTION_ERR);

        // line에도 새로운 section 정보 등록 (양방향)
        newSection.updateLine(this);
        sections.addSection(newSection);

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }
}
