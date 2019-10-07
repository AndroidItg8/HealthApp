package com.itg8.healthapp.background;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.itg8.healthapp.model.BreathModel;
import com.itg8.healthapp.utils.AppConst;

import org.qap.ctimelineview.TimelineRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class BgModelThread implements Runnable {

    private List<BreathModel> breathModels;

    public BgModelThread(List<BreathModel> breathModels) {
        Collections.reverse(breathModels);
        this.breathModels =   breathModels;

    }

    @Override
    public void run() {
        List<TimelineRow> timelineRows=new ArrayList<>();
        for(int i=0; i<breathModels.size(); i++){
            timelineRows.add(getTimeLineModel(breathModels.get(i),i));
        }
        run(timelineRows);
    }

    private TimelineRow getTimeLineModel(BreathModel model, int position){
        TimelineRow timeRowModel = new TimelineRow(position, new Date(model.getTimestamp()), String.valueOf(model.getValue()), model.getStatus());
        setData(timeRowModel);
        return timeRowModel;
    }

    private TimelineRow setData(TimelineRow timeLineModel) {


String model = timeLineModel.getTitle();
        int colorTitle = 0;
        int colorBackground = 0;

        if (Integer.parseInt(model) > AppConst.THRESHOLD_CALM && Integer.parseInt(model) < AppConst.THRESHOLD_STRESS) {
            colorTitle = Color.parseColor("#00BCD4");
            colorBackground = Color.parseColor("#00BCD4");
        } else if (Integer.parseInt(model)>AppConst.THRESHOLD_CALM) {
            colorTitle = Color.parseColor("#FF5722");
            colorBackground = Color.parseColor("#FF5722");

        } else if (Integer.parseInt(model) > AppConst.THRESHOLD_STRESS) {
            colorTitle = Color.parseColor("#FF540E01");
            colorBackground = Color.parseColor("#FF540E01");


        }else if (Integer.parseInt(model) >= AppConst.MIN_BREATH_VAKLUE && Integer.parseInt(model) < AppConst.THRESHOLD_CALM) {
            colorTitle = Color.parseColor("#00BCD4");
            colorBackground = Color.parseColor("#00BCD4");
        }


        timeLineModel.setBellowLineSize(4);
// To set row Image Size in dp (optional)
        timeLineModel.setImageSize(40);
        timeLineModel.setBellowLineColor(colorBackground);
// To set background color of the row image (optional)
        timeLineModel.setBackgroundColor(colorBackground);
// To set the Background Size of the row image in dp (optional)
        timeLineModel.setBackgroundSize(20);
// To set row Date text color (optional)
        timeLineModel.setDateColor(Color.parseColor("#878887"));
// To set row Title text color (optional)
        timeLineModel.setTitleColor(colorTitle);
// To set row Description text color (optional)
        timeLineModel.setDescriptionColor(colorTitle);
        return timeLineModel;
    }

    public abstract void run(List<TimelineRow> list);
}
