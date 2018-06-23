package announcements.tninis.cloud.teilamannouncements;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import announcements.tninis.cloud.teilamannouncements.R;


public class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.MyViewHolder> {


    private List<AnnouncementsItems> AnnouncementsList;
    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText;
        public TextView linkText;
        public View view;

        public MyViewHolder(View view) {
            super(view);
            titleText = (TextView) view.findViewById(R.id.title);
            linkText = (TextView) view.findViewById(R.id.link);
            this.view = view;
        }
    }

    public AnnouncementsAdapter(List<AnnouncementsItems> AnnouncementsList) {
        this.AnnouncementsList = AnnouncementsList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        AnnouncementsItems c = AnnouncementsList.get(position);
        holder.linkText.setText(c.title);
        holder.titleText.setText("Ημερ. Ανακοίνωσης : "+c.date);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return AnnouncementsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.available_announcements_row,parent, false);
        return new MyViewHolder(v);
    }

    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
