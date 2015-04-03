package com.ngusta.cupassist.adapters;

import com.hb.views.PinnedSectionListView;
import com.ngusta.cupassist.BuildConfig;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.CompetitionPeriod;
import com.ngusta.cupassist.domain.Tournament;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TournamentListAdapter extends ArrayAdapter<Tournament> implements
        PinnedSectionListView.PinnedSectionListAdapter, SectionIndexer {

    public static final String TAG = TournamentListAdapter.class.getSimpleName();

    public static final int VIEW_TYPE_TOURNAMENT = 0;

    public static final int VIEW_TYPE_SECTION_HEADER = 1;

    public static final int NUMBER_OF_VIEW_TYPES = 2;

    private LayoutInflater mInflater;

    /**
     * Array of CompetitionPeriod list section positions. E.g. #mSections[3-1] == 7 means that
     * the section header for period 3 ("TP 03") has position 7 in the list view.
     */
    private Integer[] mSections;

    public TournamentListAdapter(Context context, List<Tournament> tournaments) {
        super(context, 0, tournaments);
        mInflater = LayoutInflater.from(context);
        mSections = calculateSections(tournaments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_SECTION_HEADER:
                return getSectionHeaderView(position, convertView, parent);
            case VIEW_TYPE_TOURNAMENT:
                return getTournamentView(position, convertView, parent);
        }
        throw new RuntimeException("Unknown view type: " + viewType);
    }

    private View getTournamentView(int position, View convertView, ViewGroup parent) {
        View view;
        TournamentViewHolder holder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.tournament_list_item, parent, false);
            view.setTag(holder = new TournamentViewHolder(view));
        } else {
            view = convertView;
            holder = (TournamentViewHolder) view.getTag();
        }

        Tournament tournament = getItem(position);

        holder.name.setText(tournament.getName());
        holder.club.setText(tournament.getClub());
        holder.dates.setText(getDates(tournament));
        holder.levelClazzIndicator
                .setBackgroundResource(getLevelIndicatorResource(tournament.getLevel()));

        boolean women = false, men = false, youth = false, mixed = false, veteran = false;

        for (Tournament.TournamentClazz tournamentClazz : tournament.getClazzes()) {
            String initialLetter = tournamentClazz.getClazz().getInitialLetter();
            if (Clazz.WOMEN.getInitialLetter().equals(initialLetter)) {
                women = true;
            } else if (Clazz.MEN.getInitialLetter().equals(initialLetter)) {
                men = true;
            } else if (Clazz.U13F.getInitialLetter().equals(initialLetter)) {
                youth = true;
            } else if (Clazz.MIXED.getInitialLetter().equals(initialLetter)) {
                mixed = true;
            } else if (Clazz.V35D.getInitialLetter().equals(initialLetter)) {
                veteran = true;
            }
        }

        holder.clazzIndicatorWomen.setVisibility(women ? View.VISIBLE : View.INVISIBLE);
        holder.clazzIndicatorMen.setVisibility(men ? View.VISIBLE : View.INVISIBLE);
        holder.clazzIndicatorYouth.setVisibility(youth ? View.VISIBLE : View.INVISIBLE);
        holder.clazzIndicatorMixed.setVisibility(mixed ? View.VISIBLE : View.INVISIBLE);
        holder.clazzIndicatorVeteran.setVisibility(veteran ? View.VISIBLE : View.INVISIBLE);

        return view;
    }

    private String getDates(Tournament tournament) {
        if (tournament.spansOverSeveralDays()) {
            return formatDate(tournament.getStartDate()) + " - " + formatDate(
                    tournament.getEndDate());
        }
        return formatDate(tournament.getStartDate());
    }

    private String formatDate(Date date) {
        return DateUtils.formatDateTime(getContext(), date.getTime(),
                DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY
                        | DateUtils.FORMAT_SHOW_DATE);
    }

    private View getSectionHeaderView(int position, View convertView, ViewGroup parent) {
        RelativeLayout view;
        SectionHeaderViewHolder holder;

        if (convertView == null) {
            view = (RelativeLayout) mInflater
                    .inflate(R.layout.tournament_list_header, parent, false);
            view.setTag(holder = new SectionHeaderViewHolder(view));
        } else {
            view = (RelativeLayout) convertView;
            holder = (SectionHeaderViewHolder) view.getTag();
        }

        int periodNumber = getSectionForPosition(position) + 1;
        CompetitionPeriod competitionPeriod = CompetitionPeriod.findPeriodByNumber(periodNumber);
        String currentString = competitionPeriod.isCurrent() ? " " + getContext()
                .getString(R.string.competition_period_current_marker) : "";
        holder.competitionPeriod.setText(competitionPeriod.getName() + currentString);
        holder.startDate.setText(DateUtils
                .formatDateTime(getContext(), competitionPeriod.getStartDate().getTime(), 0) + " - "
                + DateUtils.formatDateTime(
                getContext(), competitionPeriod.getEndDate().getTime(), 0));

        return view;
    }

    @Override
    public int getCount() {
        return super.getCount() + mSections.length;
    }

    @Override
    public Tournament getItem(int position) {
        if (getItemViewType(position) == VIEW_TYPE_SECTION_HEADER) {
            return null;
        }
        // Adjust position to the list of tournaments.
        position -= getSectionForPosition(position) + 1;
        return super.getItem(position);
    }

    @Override
    public int getViewTypeCount() {
        return NUMBER_OF_VIEW_TYPES;
    }

    @Override
    public int getItemViewType(int position) {
        return getPositionForSection(getSectionForPosition(position)) == position
                ? VIEW_TYPE_SECTION_HEADER : VIEW_TYPE_TOURNAMENT;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == VIEW_TYPE_SECTION_HEADER;
    }

    @Override
    public Object[] getSections() {
        int sectionsLength = mSections.length;
        CompetitionPeriod[] sections = new CompetitionPeriod[sectionsLength];

        for (int i = 0; i < sectionsLength; i++) {
            int periodNumber = getSectionForPosition(mSections[i]) + 1;
            sections[i] = CompetitionPeriod.findPeriodByNumber(periodNumber);
        }

        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSections[sectionIndex];
    }

    @Override
    public int getSectionForPosition(int position) {
        int start = 0;
        int end = mSections.length;
        int diff = end - start;
        while (diff > 1) {
            int mid = start + (diff / 2);
            if (mSections[mid] > position) {
                end = mid;
            } else {
                start = mid;
            }
            diff = end - start;
        }
        return start;
    }

    private static Integer[] calculateSections(List<Tournament> tournaments) {
        // Assuming tournaments are sorted by competition period in ascending order.
        Integer[] sections = new Integer[CompetitionPeriod.COMPETITION_PERIODS.length];
        int listIndex;
        int periodIndex;
        int firstPeriodIndex = tournaments.get(0).getCompetitionPeriod().getPeriodNumber() - 1;
        listIndex = periodIndex = firstPeriodIndex;

        for (int i = 0; i <= firstPeriodIndex; i++) {
            sections[i] = i;
        }

        for (Tournament tournament : tournaments) {
            int newPeriodIndex = tournament.getCompetitionPeriod().getPeriodNumber() - 1;

            if (newPeriodIndex > periodIndex) {
                listIndex += newPeriodIndex - periodIndex;
                sections[newPeriodIndex] = listIndex;
                periodIndex = newPeriodIndex;
            }

            listIndex++;
        }

        listIndex += sections.length - periodIndex - 1;

        // Fill gaps from behind.
        for (int i = sections.length - 1; i > firstPeriodIndex; i--) {
            if (sections[i] == null) {
                sections[i] = listIndex;
            } else {
                listIndex = sections[i];
            }
            listIndex--;
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Sections: " + Arrays.toString(sections));
        }

        return sections;
    }

    public static int getLevelIndicatorResource(Tournament.Level level) {
        switch (level) {
            case OPEN:
                return R.color.level_open;
            case OPEN_GREEN:
                return R.color.level_open_green;
            case CHALLENGER:
                return R.color.level_challenger;
            case YOUTH:
                return R.color.level_youth;
            case VETERAN:
                return R.color.level_veteran;
            case SWEDISH_BEACH_TOUR:
                return R.color.level_sbt;
            case UNKNOWN:
            default:
                return R.color.level_unknown;
        }
    }

    private static final class TournamentViewHolder {

        final LinearLayout levelClazzIndicator;

        final TextView clazzIndicatorWomen;

        final TextView clazzIndicatorMen;

        final TextView clazzIndicatorYouth;

        final TextView clazzIndicatorMixed;

        final TextView clazzIndicatorVeteran;

        final TextView name;

        final TextView club;

        final TextView dates;

        private TournamentViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            club = (TextView) view.findViewById(R.id.club);
            dates = (TextView) view.findViewById(R.id.dates);
            levelClazzIndicator = (LinearLayout) view.findViewById(R.id.level_clazz_indicator);
            clazzIndicatorWomen = (TextView) levelClazzIndicator
                    .findViewById(R.id.clazz_indicator_women);
            clazzIndicatorMen = (TextView) levelClazzIndicator
                    .findViewById(R.id.clazz_indicator_men);
            clazzIndicatorYouth = (TextView) levelClazzIndicator
                    .findViewById(R.id.clazz_indicator_youth);
            clazzIndicatorMixed = (TextView) levelClazzIndicator
                    .findViewById(R.id.clazz_indicator_mixed);
            clazzIndicatorVeteran = (TextView) levelClazzIndicator
                    .findViewById(R.id.clazz_indicator_veteran);
            clazzIndicatorWomen.setText(Clazz.WOMEN.getInitialLetter());
            clazzIndicatorMen.setText(Clazz.MEN.getInitialLetter());
            clazzIndicatorYouth.setText(Clazz.U13F.getInitialLetter());
            clazzIndicatorMixed.setText(Clazz.MIXED.getInitialLetter());
            clazzIndicatorVeteran.setText(Clazz.V35D.getInitialLetter());
        }
    }

    private static final class SectionHeaderViewHolder {

        final TextView competitionPeriod;

        final TextView startDate;

        private SectionHeaderViewHolder(View view) {
            competitionPeriod = (TextView) view.findViewById(R.id.competition_period);
            startDate = (TextView) view.findViewById(R.id.start_date);
        }
    }
}
