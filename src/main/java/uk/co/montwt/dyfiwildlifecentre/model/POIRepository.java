package uk.co.montwt.dyfiwildlifecentre.model;

import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

public class POIRepository {
    private List<PointOfInterest> pointsOfInterest;

    public POIRepository() {
        pointsOfInterest = new ArrayList<>();
        pointsOfInterest.add(new PointOfInterest("Aberystwyth University", "A University located in" +
                "Aberystwyth in Ceredigion, Wales.", 52.41806, -4.06576));
        pointsOfInterest.add(new PointOfInterest("19 Queen Annes Gardens", "A house in London.",
                51.4999292, -0.2568716));
    }

    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }
}
