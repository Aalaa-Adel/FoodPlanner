package com.aalaa.foodplanner.ui.search.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.aalaa.foodplanner.ui.common.AppSnack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.domain.models.Area;
import com.aalaa.foodplanner.domain.models.Category;
import com.aalaa.foodplanner.domain.models.Ingredients;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.ui.search.FlagUtils;
import com.aalaa.foodplanner.ui.search.presenter.SearchPresenter;
import com.aalaa.foodplanner.ui.search.presenter.SearchPresenterImpl;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SearchFragment extends Fragment implements SearchView, OnSearchMealClickListener {

    private static final String TAG = "SearchFragment";

    private EditText etSearch;
    private TabLayout tabLayout;
    private RecyclerView rvCategories, rvCountries, rvIngredients;
    private LinearLayout layoutSearchResults;
    private RecyclerView rvSearchResults;
    private TextView tvResultsTitle;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private TextView tvEmptyMessage;

    private SearchPresenter presenter;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final PublishSubject<String> searchSubject = PublishSubject.create();

    private ExploreCategoryAdapter categoriesAdapter, countriesAdapter, ingredientsAdapter;
    private SearchResultAdapter searchResultsAdapter;

    private int currentTab = 0;
    private static final int INGREDIENTS_PREVIEW_LIMIT = 30;
    private List<Ingredients> ingredientsCache = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupPresenter();
        setupAdapters();

        setupTabs();
        setupRxSearch();

        presenter.loadCategories();
        presenter.loadAreas();
        presenter.loadIngredients();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        tabLayout = view.findViewById(R.id.tab_layout);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvCountries = view.findViewById(R.id.rv_countries);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        layoutSearchResults = view.findViewById(R.id.layout_search_results);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        tvResultsTitle = view.findViewById(R.id.tv_results_title);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_message);
    }

    private void setupPresenter() {
        presenter = new SearchPresenterImpl(this, MealRepositoryImpl.getInstance(MealRemoteDataSource.getInstance()));
    }

    private void setupAdapters() {
        categoriesAdapter = new ExploreCategoryAdapter(requireContext(), new ArrayList<>(),
                name -> presenter.getMealsByCategory(name));

        countriesAdapter = new ExploreCategoryAdapter(requireContext(), new ArrayList<>(),
                name -> presenter.getMealsByArea(name));

        ingredientsAdapter = new ExploreCategoryAdapter(requireContext(), new ArrayList<>(),
                name -> presenter.getMealsByIngredient(name));

        searchResultsAdapter = new SearchResultAdapter(requireContext(), new ArrayList<>(), this);

        rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvCategories.setAdapter(categoriesAdapter);

        rvCountries.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvCountries.setAdapter(countriesAdapter);

        rvIngredients.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvIngredients.setAdapter(ingredientsAdapter);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSearchResults.setAdapter(searchResultsAdapter);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Categories"));
        tabLayout.addTab(tabLayout.newTab().setText("Countries"));
        tabLayout.addTab(tabLayout.newTab().setText("Ingredients"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                showTab(currentTab);
                etSearch.setText("");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void showTab(int position) {
        rvCategories.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        rvCountries.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
        rvIngredients.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
        layoutSearchResults.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);

        if (position == 2 && etSearch != null && etSearch.getText().toString().trim().isEmpty()) {
            int end = Math.min(INGREDIENTS_PREVIEW_LIMIT, ingredientsCache.size());
            ingredientsAdapter.updateData(toIngredientExploreItems(ingredientsCache.subList(0, end)));
        }
    }

    private void setupRxSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSubject.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        disposables.add(searchSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> {
                    if (query.trim().isEmpty()) {
                        showTab(currentTab);
                    } else {
                        performSearch(query.trim());
                    }
                }));
    }

    private void performSearch(String query) {
        switch (currentTab) {
            case 0:
                presenter.searchCategories(query);
                break;
            case 1:
                presenter.searchAreas(query);
                break;
            case 2:
                presenter.searchIngredients(query);
                break;
        }
    }

    @Override
    public void onMealClick(MealsItem meal) {
        SearchFragmentDirections.ActionSearchFragmentToRecipeDetailFragment action = SearchFragmentDirections
                .actionSearchFragmentToRecipeDetailFragment(meal);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void showCategories(List<Category> categories) {
        List<ExploreCategoryAdapter.ExploreItem> items = new ArrayList<>();
        for (Category cat : categories) {
            items.add(new ExploreCategoryAdapter.ExploreItem(cat.getStrCategory(), cat.getStrCategoryThumb()));
        }
        categoriesAdapter.updateData(items);
    }

    @Override
    public void showAreas(List<Area> areas) {
        List<ExploreCategoryAdapter.ExploreItem> items = new ArrayList<>();
        for (Area area : areas) {
            String title = area.getStrArea();
            items.add(new ExploreCategoryAdapter.ExploreItem(
                    title,
                    FlagUtils.getFlagUrl(title)));
        }
        countriesAdapter.updateData(items);
    }

    @Override
    public void showIngredients(List<Ingredients> ingredients) {
        ingredientsCache = (ingredients != null) ? ingredients : new ArrayList<>();

        int end = Math.min(INGREDIENTS_PREVIEW_LIMIT, ingredientsCache.size());
        List<Ingredients> preview = ingredientsCache.subList(0, end);

        ingredientsAdapter.updateData(toIngredientExploreItems(preview));
    }

    private List<ExploreCategoryAdapter.ExploreItem> toIngredientExploreItems(List<Ingredients> list) {
        List<ExploreCategoryAdapter.ExploreItem> items = new ArrayList<>();
        if (list == null)
            return items;

        for (Ingredients ing : list) {
            items.add(new ExploreCategoryAdapter.ExploreItem(
                    ing.getStrIngredient(),
                    "https://www.themealdb.com/images/ingredients/" + ing.getStrIngredient() + "-Small.png"));
        }
        return items;
    }

    @Override
    public void showExploreItems(List<ExploreCategoryAdapter.ExploreItem> items) {
        switch (currentTab) {
            case 0:
                categoriesAdapter.updateData(items);
                break;
            case 1:
                countriesAdapter.updateData(items);
                break;
            case 2:
                ingredientsAdapter.updateData(items);
                break;
        }
        rvCategories.setVisibility(currentTab == 0 ? View.VISIBLE : View.GONE);
        rvCountries.setVisibility(currentTab == 1 ? View.VISIBLE : View.GONE);
        rvIngredients.setVisibility(currentTab == 2 ? View.VISIBLE : View.GONE);
        layoutSearchResults.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void showMeals(List<MealsItem> meals, String title) {
        tvResultsTitle.setText(title);
        searchResultsAdapter.updateData(meals);
        layoutSearchResults.setVisibility(View.VISIBLE);
        rvCategories.setVisibility(View.GONE);
        rvCountries.setVisibility(View.GONE);
        rvIngredients.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        AppSnack.showError(requireView(), message);
    }

    @Override
    public void showEmptyState(String message) {
        tvEmptyMessage.setText(message);
        layoutSearchResults.setVisibility(View.GONE);
        rvCategories.setVisibility(View.GONE);
        rvCountries.setVisibility(View.GONE);
        rvIngredients.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        if (presenter != null) {
            presenter.dispose();
        }
    }
}