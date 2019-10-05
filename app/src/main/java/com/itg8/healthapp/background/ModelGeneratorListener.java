package com.itg8.healthapp.background;

import org.qap.ctimelineview.TimelineRow;

import java.util.List;

public interface ModelGeneratorListener {
    void onListAvail(List<TimelineRow> timelineRows);
}
