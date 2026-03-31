package com.example.smishingdetectionapp;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Integer> threatLevelLiveData;
    private ThreatRepository threatRepository;

    public MainViewModel(Context context) {
        threatLevelLiveData = new MutableLiveData<>();
        threatRepository = new DatabaseThreatRepository(context);
        refreshThreatLevel();
    }

    public LiveData<Integer> getThreatLevel() {
        return threatLevelLiveData;
    }

    public void refreshThreatLevel() {
        int currentLevel = threatRepository.getThreatLevel();
        threatLevelLiveData.setValue(currentLevel);
    }

    public int getCurrentScore() {
        return threatRepository.calculateThreatScore();
    }
}