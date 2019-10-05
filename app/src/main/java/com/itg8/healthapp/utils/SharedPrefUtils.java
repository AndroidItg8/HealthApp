package com.itg8.healthapp.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itg8.healthapp.common.Prefs;
import com.itg8.healthapp.model.BreathModel;

import java.util.ArrayList;
import java.util.List;

public class SharedPrefUtils {

    public static final String CURRENT_BREATH_RATE="current_breath_rate";
    private static final String BREATH_LIST = "breathing_model_list";
    public static final String IS_INCREAMENT="IS_INCREAMENT";

    public static void saveBreath(BreathModel breathModel){
        List<BreathModel> listBreath = getAllBreathModel();
        listBreath.add(breathModel);
        Prefs.putString(BREATH_LIST,new Gson().toJson(listBreath));
    }

    public static List<BreathModel> getAllBreathModel() {
        String breaths= Prefs.getString(BREATH_LIST);
        if(breaths!=null){
            List<BreathModel> models=new Gson().fromJson(breaths,new TypeToken<List<BreathModel>>(){}.getType());
            if(models!=null)
                return models;
        }
        return new ArrayList<>();
    }

    public static BreathModel getLastBreathModel() {
        List<BreathModel> breathModels=getAllBreathModel();
        if(breathModels.size()>0)
            return breathModels.get(breathModels.size()-1);

        return null;
    }


}
