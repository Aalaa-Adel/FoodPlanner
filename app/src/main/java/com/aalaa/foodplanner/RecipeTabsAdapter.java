package com.aalaa.foodplanner;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RecipeTabsAdapter extends FragmentStateAdapter {

    public RecipeTabsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new IngredientsFragment();
            case 1:
                return new StepsFragment();
            case 2:
                return new VideoFragment();
            default:
                return new IngredientsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Ingredients, Steps, Video
    }
}