package king.musicplayer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by King on 2016/12/21.
 */

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Song> list;
    public MyAdapter(MainActivity mainActivity,List<Song> list){
        this.context = mainActivity;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = View.inflate(context,R.layout.item_music_listview,null);
            viewHolder.song = (TextView) view.findViewById(R.id.item_mymuisc_song);
            viewHolder.singer = (TextView) view.findViewById(R.id.item_mymuisc_singer);
            viewHolder.duration = (TextView) view .findViewById(R.id.item_mymuisc_duration);
            viewHolder.position = (TextView) view.findViewById(R.id.item_mymuisc_position);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.song.setText(list.get(i).song.toString());
        viewHolder.singer.setText(list.get(i).singer.toString());
        /** 时间需要转换一下*/
        int duration = list.get(i).duration;
        String time = MusicUtils.formatTime(duration);
        viewHolder.duration.setText(time);
        viewHolder.position.setText(i+1+"");
        return view;
    }
    class ViewHolder{
        TextView song;/** 歌曲名*/
        TextView singer;/** 歌手*/
        TextView duration;/** 时长*/
        TextView position;/** 序号*/

    }
}
