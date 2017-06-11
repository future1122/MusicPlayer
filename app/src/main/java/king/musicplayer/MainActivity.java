package king.musicplayer;


import android.media.MediaPlayer;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
    private ListView mListView;
    private List<Song> list;
    private MyAdapter myAdapter;
    private MediaPlayer mediaPlayer;
    private Button previous;
    private Button pause;
    private Button next;
    private Timer timer;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView allTime;
    private TextView main_singer;
    private TextView main_song;
    protected boolean isPause;
    protected boolean isFirst;
    protected boolean isSeekBarChanging;
    private int num;
    private int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
        setItemClick();
    }

    /**
     * 这是设置主界面中listView的item点击事件
     * */
    private void setItemClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(isFirst){
                    isPause = false;
                    setPauseIcon();
                    isFirst = false;
                }
                pos = position;
                play();
            }
        });
    }
    /**
     * 设置监听
     * */
    private void setListener() {
        InnerListener innerListener = new InnerListener();
        pause.setOnClickListener(innerListener);
        previous.setOnClickListener(innerListener);
        next.setOnClickListener(innerListener);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
        mediaPlayer.setOnCompletionListener(new InnerOnCompletionListener());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = false;
                if(isFirst){
                    isPause = false;
                    setPauseIcon();
                    play();
                }else if(isPause){
                    mediaPlayer.seekTo(seekBar.getProgress());
                    mediaPlayer.start();
                    isPause = false;
                    setPauseIcon();
                }else mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }
    /**
     * 播放完成监听类。
     * */
    private final class InnerOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
                next();
        }
    }

    /**
     * 监听的按钮实现
     * */
    private class InnerListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.play_pause:
                    pause();
                    break;
                case R.id.play_previous:
                    isFirst = false;
                    previous();
                    break;
                case R.id.play_next:
                    isFirst = false;
                    next();
                    break;

            }
        }
    }

    /**
     * 播放函数
     * */
    public void play(){
        seekBar.setMax(list.get(pos).duration);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(list.get(pos).path);
            mediaPlayer.prepare();
            if(isFirst){
                isFirst = false;
                mediaPlayer.seekTo(seekBar.getProgress());
            }
            mediaPlayer.start();
            setTimer();
            setText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下一曲的函数
     * */
    private void next() {
        if(pos == num-1) {
            pos = 0;
        }
        else pos++;
        play();
        isPause = false;
        setPauseIcon();
    }

    /**
     * 上一曲的函数
     * */
    private void previous() {
        if(pos == 0) pos = num-1;
        else pos--;
        play();
        isPause = false;
        setPauseIcon();
    }

    /**
     * 暂停播放的函数
     * */
    private void pause() {
        if(isFirst){
            isFirst = false;
            play();
            isPause = false;
            setPauseIcon();
        }
        else if(isPause == true) {
            mediaPlayer.start();
            isPause = false;
        }
        else{
            mediaPlayer.pause();
            isPause = true;
        }
        setPauseIcon();
    }

    /**
     * 暂停与播放按钮的切换
     * */
    private void setPauseIcon() {
        if(isPause == true) pause.setBackgroundResource(R.drawable.pause);
        else pause.setBackgroundResource(R.drawable.play);
    }


    private void setText() {
        main_song.setText(list.get(pos).song);
        main_singer.setText(list.get(pos).singer);
        allTime.setText(MusicUtils.formatTime(list.get(pos).duration));
        currentTime.setText("00:00");
    }
    /**
     *改变当前歌曲播放时间的handler
     * */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            currentTime.setText(MusicUtils.formatTime(msg.arg1));
            super.handleMessage(msg);
        }
    };

    /**
     *50ms一次的循环执行，改变seekbar的进度以及给handler传送message来改变当前播放时间
     * */
    private void setTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isSeekBarChanging){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    Message message = Message.obtain();
                    message.arg1 = mediaPlayer.getCurrentPosition();
                    handler.sendMessage(message);
                }
            }
        },0,50);
    }
    /**
     * 查找手机中的音频文件并存储到list中，初始化界面
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.main_listView);
        list = new ArrayList<>();
        list = MusicUtils.getMusicData(this);
        myAdapter = new MyAdapter(this,list);
        mListView.setAdapter(myAdapter);
        num = mListView.getCount();
        isPause = false;
        isSeekBarChanging = false;
        isFirst = true;
        pos = 0;
        mediaPlayer = new MediaPlayer();
        previous = (Button) findViewById(R.id.play_previous);
        pause = (Button) findViewById(R.id.play_pause);
        next = (Button) findViewById(R.id.play_next);
        main_singer = (TextView) findViewById(R.id.current_singer);
        main_song = (TextView) findViewById(R.id.current_song);
        currentTime = (TextView) findViewById(R.id.current_time);
        allTime = (TextView) findViewById(R.id.all_time);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(list.get(pos).duration);
        setText();
    }
}
