package com.aalaa.foodplanner.ui.favorites.presenter;


import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.domain.repository.FavoritesRepository;
import com.aalaa.foodplanner.ui.favorites.view.FavView;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoritesPresenterImp implements FavoritesPresenter {

    private final FavoritesRepository repository;
    private final FavView favView;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public FavoritesPresenterImp(FavoritesRepositoryImpl repository, FavView favView) {
        this.repository = repository;
        this.favView = favView;
    }

    @Override
    public void loadFavorites() {
        disposable.add(repository.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        favorites -> {
                            if (favorites == null || favorites.isEmpty()) {
                                favView.showEmptyState();
                            } else {
                                favView.showFavorites(favorites);
                            }
                        },
                        error -> favView.showError(error.getMessage())));
    }

    @Override
    public void addToFavorites(MealsItem meal) {
        disposable.add(repository.addToFavorites(meal)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> favView.showAddedToFavorites(),
                        error -> favView.showError(error.getMessage())));
    }

    @Override
    public void removeFromFavorites(MealsItem meal) {
        disposable.add(repository.removeFromFavorites(meal)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> favView.showRemovedFromFavorites(),
                        error -> favView.showError(error.getMessage())));
    }

    @Override
    public void checkIfFavorite(String mealId) {
        disposable.add(repository.isFavorite(mealId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isFavorite -> favView.updateFavoriteIcon(isFavorite),
                        error -> favView.showError(error.getMessage())));
    }

    @Override
    public void dispose() {
        disposable.clear();
    }
}
