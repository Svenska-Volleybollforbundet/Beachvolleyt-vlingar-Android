package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Region;
import com.ngusta.cupassist.domain.Tournament;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TournamentListDialogs {

    private static final String CLAZZ_PREFERENCES_KEY = "defaultClazzIndexes";

    private static final String LEVEL_PREFERENCES_KEY = "defaultLevelIndexes";

    private static final String REGION_PREFERENCES_KEY = "defaultRegionIndexes";

    private static final int MEN_INDEX = 0;

    private static final int WOMEN_INDEX = 1;

    private static final int MIXED_INDEX = 2;

    private static final int YOUTH_INDEX = 3;

    private static final int VETERAN_INDEX = 4;

    private static final int OPEN_GREEN_INDEX = 0;

    private static final int OPEN_BLACK_INDEX = 1;

    private static final int CHALLENGER_INDEX = 2;

    private static final int SBT_INDEX = 3;

    private static final int YOUTH_LEVEL_INDEX = 4;

    private static final int VETERAN_LEVEL_INDEX = 5;

    private static final int NORTH_REGION_INDEX = 0;

    private static final int STOCKHOLM_REGION_INDEX = 1;

    private static final int MIDDLE_REGION_INDEX = 2;

    private static final int SOUTH_REGION_INDEX = 3;

    static List<Clazz> getDefaultClazzes(SharedPreferences preferences) {
        Set<String> savedClazzIndexes = getSavedClazzPreferences(preferences);
        ArrayList<Clazz> clazzesToFilter = new ArrayList<>();
        for (String clazzIndex : savedClazzIndexes) {
            int i = Integer.parseInt(clazzIndex);
            addClazzesFromIndex(clazzesToFilter, i);
        }
        return clazzesToFilter;
    }

    private static Set<String> getSavedClazzPreferences(SharedPreferences preferences) {
        Set<String> defaultClazzIndexes = new HashSet<>();
        defaultClazzIndexes.add("" + MEN_INDEX);
        defaultClazzIndexes.add("" + WOMEN_INDEX);
        defaultClazzIndexes.add("" + MIXED_INDEX);
        return preferences.getStringSet(CLAZZ_PREFERENCES_KEY, defaultClazzIndexes);
    }

    private static void addClazzesFromIndex(ArrayList<Clazz> clazzesToFilter, int i) {
        switch (i) {
            case MEN_INDEX:
                clazzesToFilter.add(Clazz.MEN);
                break;
            case WOMEN_INDEX:
                clazzesToFilter.add(Clazz.WOMEN);
                break;
            case MIXED_INDEX:
                clazzesToFilter.add(Clazz.MIXED);
                break;
            case YOUTH_INDEX:
                clazzesToFilter.addAll(Clazz.getYouthClazzes());
                break;
            case VETERAN_INDEX:
                clazzesToFilter.addAll(Clazz.getVeteranClazzes());
                break;

        }
    }

    static AlertDialog.Builder createClazzFilterDialog(final TournamentListActivity activity, final SharedPreferences preferences) {
        Set<String> savedClazzIndexes = getSavedClazzPreferences(preferences);
        final ArrayList<Integer> selectedItemsIndexList = new ArrayList<>();
        boolean[] defaultSelected = {false, false, false, false, false};
        for (String clazzIndex : savedClazzIndexes) {
            int i = Integer.parseInt(clazzIndex);
            defaultSelected[i] = true;
            selectedItemsIndexList.add(i);
        }
        String[] clazzNames = {"Herr", "Dam", "Mixed", "Ungdom", "Veteran"};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(R.string.show_clazzes)
                .setMultiChoiceItems(clazzNames, defaultSelected, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedItemsIndexList.add(which);
                    } else if (selectedItemsIndexList.contains(which)) {
                        selectedItemsIndexList.remove(Integer.valueOf(which));
                    }
                })
                .setPositiveButton(R.string.show, (dialog, id) -> {
                    ArrayList<Clazz> clazzesToFilter = new ArrayList<>();
                    HashSet<String> clazzIndexesToSave = new HashSet<>();
                    for (Integer selectedClazzIndex : selectedItemsIndexList) {
                        clazzIndexesToSave.add(selectedClazzIndex.toString());
                        addClazzesFromIndex(clazzesToFilter, selectedClazzIndex);
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putStringSet(CLAZZ_PREFERENCES_KEY, clazzIndexesToSave);
                    editor.apply();
                    activity.setClazzesToFilter(clazzesToFilter);
                    activity.updateList();
                });
        return dialogBuilder;
    }

    static List<Tournament.Level> getDefaultLevels(SharedPreferences preferences) {
        Set<String> savedLevelIndexes = getSavedLevelPreferences(preferences);
        ArrayList<Tournament.Level> levelsToFilter = new ArrayList<>();
        for (String levelIndex : savedLevelIndexes) {
            int i = Integer.parseInt(levelIndex);
            addLevelsFromIndex(levelsToFilter, i);
        }
        return levelsToFilter;
    }

    private static Set<String> getSavedLevelPreferences(SharedPreferences preferences) {
        Set<String> defaultLevelIndexes = new HashSet<>();
        defaultLevelIndexes.add("" + OPEN_GREEN_INDEX);
        defaultLevelIndexes.add("" + OPEN_BLACK_INDEX);
        defaultLevelIndexes.add("" + CHALLENGER_INDEX);
        defaultLevelIndexes.add("" + SBT_INDEX);
        return preferences.getStringSet(LEVEL_PREFERENCES_KEY, defaultLevelIndexes);
    }

    private static void addLevelsFromIndex(ArrayList<Tournament.Level> levelsToFilter, int i) {
        switch (i) {
            case OPEN_GREEN_INDEX:
                levelsToFilter.add(Tournament.Level.OPEN_GREEN);
                break;
            case OPEN_BLACK_INDEX:
                levelsToFilter.add(Tournament.Level.OPEN);
                break;
            case CHALLENGER_INDEX:
                levelsToFilter.add(Tournament.Level.CHALLENGER);
                break;
            case SBT_INDEX:
                levelsToFilter.add(Tournament.Level.MASTER);
                levelsToFilter.add(Tournament.Level.FIVE_STAR);
                levelsToFilter.add(Tournament.Level.TOUR_FINAL);
                levelsToFilter.add(Tournament.Level.SM);
                break;
            case YOUTH_LEVEL_INDEX:
                levelsToFilter.add(Tournament.Level.YOUTH);
                levelsToFilter.add(Tournament.Level.UNKNOWN);
                break;
            case VETERAN_LEVEL_INDEX:
                levelsToFilter.add(Tournament.Level.VETERAN);
                levelsToFilter.add(Tournament.Level.UNKNOWN);
                break;
        }
    }

    static AlertDialog.Builder createLevelFilterDialog(final TournamentListActivity activity, final SharedPreferences preferences) {
        Set<String> savedLevelIndexes = getSavedLevelPreferences(preferences);
        final ArrayList<Integer> selectedItemsIndexList = new ArrayList<>();
        boolean[] defaultSelected = {false, false, false, false, false, false};
        for (String levelIndex : savedLevelIndexes) {
            int i = Integer.parseInt(levelIndex);
            defaultSelected[i] = true;
            selectedItemsIndexList.add(i);
        }
        String[] levelNames = {"SBT 1*", "SBT 2*", "SBT 3*", "SBT 4-7*", "Ungdom", "Veteran"};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(R.string.show_levels)
                .setMultiChoiceItems(levelNames, defaultSelected, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedItemsIndexList.add(which);
                    } else if (selectedItemsIndexList.contains(which)) {
                        selectedItemsIndexList.remove(Integer.valueOf(which));
                    }
                })
                .setPositiveButton(R.string.show, (dialog, id) -> {
                    ArrayList<Tournament.Level> levelsToFilter = new ArrayList<>();
                    HashSet<String> levelIndexesToSave = new HashSet<>();
                    for (Integer selectedLevelIndex : selectedItemsIndexList) {
                        levelIndexesToSave.add(selectedLevelIndex.toString());
                        addLevelsFromIndex(levelsToFilter, selectedLevelIndex);
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putStringSet(LEVEL_PREFERENCES_KEY, levelIndexesToSave);
                    editor.apply();
                    activity.setLevelsToFilter(levelsToFilter);
                    activity.updateList();
                });
        return dialogBuilder;
    }

    public static List<Region> getDefaultRegions(SharedPreferences preferences) {
        Set<String> savedRegionIndexes = getSavedRegionPreferences(preferences);
        ArrayList<Region> regionsToFilter = new ArrayList<>();
        for (String regionIndex : savedRegionIndexes) {
            int i = Integer.parseInt(regionIndex);
            addRegionsFromIndex(regionsToFilter, i);
        }
        return regionsToFilter;
    }

    private static Set<String> getSavedRegionPreferences(SharedPreferences preferences) {
        Set<String> defaultRegionIndexes = new HashSet<>();
        defaultRegionIndexes.add("" + NORTH_REGION_INDEX);
        defaultRegionIndexes.add("" + STOCKHOLM_REGION_INDEX);
        defaultRegionIndexes.add("" + MIDDLE_REGION_INDEX);
        defaultRegionIndexes.add("" + SOUTH_REGION_INDEX);
        return preferences.getStringSet(REGION_PREFERENCES_KEY, defaultRegionIndexes);
    }

    private static void addRegionsFromIndex(ArrayList<Region> regionsToFilter, int i) {
        switch (i) {
            case NORTH_REGION_INDEX:
                regionsToFilter.add(Region.NORTH);
                break;
            case STOCKHOLM_REGION_INDEX:
                regionsToFilter.add(Region.EAST);
                break;
            case MIDDLE_REGION_INDEX:
                regionsToFilter.add(Region.WEST);
                break;
            case SOUTH_REGION_INDEX:
                regionsToFilter.add(Region.SOUTH);
                break;
        }
    }

    static AlertDialog.Builder createRegionFilterDialog(final TournamentListActivity activity, final SharedPreferences preferences) {
        Set<String> savedRegionIndexes = getSavedRegionPreferences(preferences);
        final ArrayList<Integer> selectedItemsIndexList = new ArrayList<>();
        boolean[] defaultSelected = {false, false, false, false};
        for (String regionIndex : savedRegionIndexes) {
            int i = Integer.parseInt(regionIndex);
            defaultSelected[i] = true;
            selectedItemsIndexList.add(i);
        }
        String[] clazzNames = {"Norr", "\u00D6st", "V\u00E4st", "S\u00F6der"};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(R.string.show_regions)
                .setMultiChoiceItems(clazzNames, defaultSelected, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedItemsIndexList.add(which);
                    } else if (selectedItemsIndexList.contains(which)) {
                        selectedItemsIndexList.remove(Integer.valueOf(which));
                    }
                })
                .setPositiveButton(R.string.show, (dialog, id) -> {
                    ArrayList<Region> regionsToFilter = new ArrayList<>();
                    HashSet<String> regionIndexesToSave = new HashSet<>();
                    for (Integer selectedRegionIndex : selectedItemsIndexList) {
                        regionIndexesToSave.add(selectedRegionIndex.toString());
                        addRegionsFromIndex(regionsToFilter, selectedRegionIndex);
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putStringSet(REGION_PREFERENCES_KEY, regionIndexesToSave);
                    editor.apply();
                    activity.setRegionsToFilter(regionsToFilter);
                    activity.updateList();
                });
        return dialogBuilder;
    }
}
