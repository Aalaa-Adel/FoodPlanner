package com.aalaa.foodplanner.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ConnectivityObserver {

    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final BehaviorSubject<Boolean> networkStatusSubject;
    private ConnectivityManager.NetworkCallback networkCallback;

    private static ConnectivityObserver instance;

    private ConnectivityObserver(Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.networkStatusSubject = BehaviorSubject.createDefault(isConnected());
    }

    public static synchronized ConnectivityObserver getInstance(Context context) {
        if (instance == null) {
            instance = new ConnectivityObserver(context);
        }
        return instance;
    }

    public void startListening() {
        if (networkCallback != null)
            return;

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                networkStatusSubject.onNext(true);
            }

            @Override
            public void onLost(Network network) {
                networkStatusSubject.onNext(false);
            }
        };

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    public void stopListening() {
        if (networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
            }
            networkCallback = null;
        }
    }

    public Observable<Boolean> observeNetwork() {
        return networkStatusSubject.distinctUntilChanged();
    }

    public boolean isConnected() {
        if (connectivityManager == null)
            return false;
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null)
            return false;
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }
}
