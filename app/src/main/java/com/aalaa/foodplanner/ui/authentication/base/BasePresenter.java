package com.aalaa.foodplanner.ui.authentication.base;

import java.lang.ref.WeakReference;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BasePresenter<V> {

    private WeakReference<V> viewRef;
    protected final CompositeDisposable disposables = new CompositeDisposable();

    public void attachView(V view) {
        viewRef = new WeakReference<>(view);
    }

    public void detachView() {
        disposables.clear();
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

    protected V getView() {
        return viewRef != null ? viewRef.get() : null;
    }

    protected boolean isViewAttached() {
        return getView() != null;
    }

    protected void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }
}
