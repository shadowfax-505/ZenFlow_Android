package com.zenflow.mobile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zenflow.mobile.service.AppUsageItem;
import com.zenflow.mobile.service.AppUsageRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardViewModel extends AndroidViewModel {

    private final AppUsageRepository appUsageRepository;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<AppUsageItem>> topApps = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasUsageAccess = new MutableLiveData<>(false);

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        this.appUsageRepository = new AppUsageRepository(application);
    }

    public LiveData<List<AppUsageItem>> getTopApps() {
        return topApps;
    }

    public LiveData<Boolean> getHasUsageAccess() {
        return hasUsageAccess;
    }

    public void refresh() {
        io.execute(() -> {
            try {
                boolean allowed = appUsageRepository.hasUsageAccess();
                hasUsageAccess.postValue(allowed);
                if (!allowed) {
                    topApps.postValue(null);
                    return;
                }
                topApps.postValue(appUsageRepository.getTopUsageLast24h(8));
            } catch (Throwable t) {
                hasUsageAccess.postValue(false);
                topApps.postValue(null);
            }
        });
    }

    @Override
    protected void onCleared() {
        io.shutdownNow();
    }
}
