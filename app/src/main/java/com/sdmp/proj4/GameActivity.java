package com.sdmp.proj4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class GameActivity extends AppCompatActivity {
    ImageAdapter imgAdapter;
    ArrayAdapter<String> listAdapter;
    GridView gridview;
    TextView tv;
    ListView listview;
    Button b1,bGuessbyGuess;
    int winX,winY,x1,y1,x2,y2,turn,mode;
    public static final int UPDATE_FROM_THREAD1=1;
    public static final int UPDATE_FROM_THREAD2=2;
    public int REPLY_FOR_THREAD1=1;
    public int REPLY_FOR_THREAD2=2;
    public static final int SUCCESS=101;
    public static final int NEAR_MISS=102;
    public static final int CLOSE_GUESS=103;
    public static final int COMP_MISS=104;
    public static final int DISASTER=105;
    ArrayList<String> moves=new ArrayList<String>(Arrays.asList("Player Moves"));
    workerThreadOne t1;
    workerThreadTwo t2;
    Boolean isW1Turn,isW2Turn;
    Handler worker1handler,worker2handler;

    private ArrayList<Integer> images = new ArrayList<Integer>( Arrays.asList(
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2,
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2,
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2,
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2,
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2,
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2,
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2,
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2,
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2,
            R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2, R.drawable.image2,R.drawable.image2, R.drawable.image2));
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int what = msg.what ;
            switch (what) {
                case UPDATE_FROM_THREAD1:
                    int pos=msg.arg1;
                    x1=pos/10;
                    y1=pos%10;

                    worker1handler=t1.getmHandler1();
                    REPLY_FOR_THREAD1=replyToThread(x1,y1,x2,y2);
                    Log.i("Handler UI","REPLY FOR THREAD 1 "+REPLY_FOR_THREAD1+" for pos "+pos);
                    if(REPLY_FOR_THREAD1==SUCCESS)
                    {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        setImage(pos,R.drawable.image7);
                        tv.setText("Thread 1 wins!");
                        Toast.makeText(getApplicationContext(),"Thread 1 won!",Toast.LENGTH_LONG).show();
                        Message msg2=worker2handler.obtainMessage(REPLY_FOR_THREAD1);
                        worker2handler.sendMessage(msg2);
                    }
                    else
                        setImage(pos,R.drawable.image3);

                    Message msgW1=worker1handler.obtainMessage(REPLY_FOR_THREAD1);
                    worker1handler.sendMessage(msgW1);
                    isW1Turn=false;
                    isW2Turn=true;
                    break;
                case UPDATE_FROM_THREAD2:
                    pos=msg.arg1;
                    x2=pos/10;
                    y2=pos%10;

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    worker2handler=t2.getmHandler2();
                    REPLY_FOR_THREAD2=replyToThread(x2,y2,x1,y1);
                    if(REPLY_FOR_THREAD2==SUCCESS)
                    {
                        setImage(pos,R.drawable.image8);
                        tv.setText("Thread 2 wins!");
                        Toast.makeText(getApplicationContext(),"Thread 2 won!",Toast.LENGTH_LONG).show();
                        Message msg2=worker1handler.obtainMessage(REPLY_FOR_THREAD2);
                        worker1handler.sendMessage(msg2);
                    }
                    else
                        setImage(pos,R.drawable.image4);
                    Log.i("Handler UI","REPLY FOR THREAD 2 "+REPLY_FOR_THREAD2+" for pos "+pos);
                    Message msgW2=worker2handler.obtainMessage(REPLY_FOR_THREAD2);
                    worker2handler.sendMessage(msgW2);
                    isW1Turn=true;
                    isW2Turn=false;
                    break;
            }

        }
    };

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridview = (GridView) findViewById(R.id.gridView);
        listview=(ListView)findViewById(R.id.list);
        tv=(TextView)findViewById(R.id.textView);
        tv.setText("");
        // Create a new ImageAdapter and set it as the Adapter for this GridView
        listAdapter=new ArrayAdapter<String>(this,R.layout.listitem,moves);
        imgAdapter = new ImageAdapter(this, images);
        gridview.setAdapter(imgAdapter);
        listview.setAdapter(listAdapter);
        b1 = findViewById(R.id.button);
        bGuessbyGuess=findViewById(R.id.button2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(t1==null && t2== null)
                {
                    t1 = new workerThreadOne();
                    t1.start();
                    t2 = new workerThreadTwo();
                    t2.start();
                }
                mode=1;
                bGuessbyGuess.setEnabled(false);
            }
        });
        bGuessbyGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode=2;
                isW1Turn=true;
                isW2Turn=false;
                if(t1==null && t2== null)
                {
                    t1 = new workerThreadOne();
                    t1.start();
                    t2 = new workerThreadTwo();
                    t2.start();
                }
                b1.setEnabled(false);
                /*
                t1=new workerThreadOne();
                t1.start();
                t2=new workerThreadTwo();
                t2.start();
                 */
            }
        });
        x2=0;y2=0;x1=0;y1=0;turn=0;
        winX=(int)(Math.random()*(10));
        winY=(int)(Math.random()*(10));
        int gopher=(winX*10)+winY;
        setImage(gopher,R.drawable.image6);
        Log.i("onCreate","Winning matrix"+winX+"-"+winY);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            worker1handler.getLooper().quitSafely();
            worker2handler.getLooper().quitSafely();
        }
        return super.onKeyDown(keyCode, event);
    }

    private int replyToThread(int x, int y, int xOther, int yOther)
    {
        if(x==winX && y==winY)
        {
            return SUCCESS;
        }
        else
        {
            if(x==xOther && y==yOther)
                return DISASTER;
            else
            {
                if((winX==(x-1)&& winY==y)||(winX==(x+1)&& winY==y)||(winX==x && winY==(y-1))||(winX==x && winY==(y+1))||(winX==(x+1) && winY==(y+1))||(winX==(x+1) && winY==(y-1))||(winX==(x-1) && winY==(y-1))||(winX==(x-1) && winY==(y+1)))
                {
                    return NEAR_MISS;
                }
                else
                {
                    if((winX==(x-2)&& winY==y)||(winX==(x+2)&& winY==y)||(winX==x && winY==(y-2))||(winX==x && winY==(y+2))||(winX==(x+2) && winY==(y+2))||(winX==(x+2) && winY==(y-2))||(winX==(x-2) && winY==(y-2))||(winX==(x-2) && winY==(y+2)))
                        return CLOSE_GUESS;
                    else
                        return COMP_MISS;
                }
            }
        }
    }
    private void setImage(int position, int image){
        //imgAdapter.getItem(position).setImageResource(image);
        images.set(position,image);
        imgAdapter.notifyDataSetChanged();
    }
    public class workerThreadOne extends Thread
    {
        int x,y;
        Handler mHandler1;
        Handler getmHandler1()
        {
            return mHandler1;
        }
        void handleCompMiss1()
        {
            if(mode==2)
            {
                while(isW1Turn==false) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Random
            x=(int)(Math.random()*(10));
            y=(int)(Math.random()*(10));
            /*
            if(x==9)
                x=0;
            else
                x=x+1;
            */

            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD1);
            msg.arg1=((x*10)+y);
            mHandler.sendMessageDelayed(msg,1000);

            mHandler.postDelayed(new Runnable() {
                public void run() {
                    moves.add("Player 1 at position "+"("+x+","+y+")"+" COMPLETE MISS");
                    listAdapter.notifyDataSetChanged();
                }
            },1000 ) ;
        }
        void handle_nearMiss1()
        {
            if(mode==2)
            {
                while(isW1Turn==false) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Select block on right
            if(x==9)
            {
               x=8;
            }
            else
                x=x+1;

            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD1);
            msg.arg1=((x*10)+y);
            mHandler.sendMessageDelayed(msg,1000);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    moves.add("Player 1 at position "+"("+x+","+y+")"+" NEAR MISS");
                    listAdapter.notifyDataSetChanged();
                }
            } ,1000) ;
        }
        void handle_closeGuess1()
        {
            if(mode==2)
            {
                while(isW1Turn==false) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Select below right diagonal
            if(x==8)
                x=6;
            else
            {
                if(x==9)
                    x=7;
                else
                    x=x+2;
            }
            if(y==8)
                y=6;
            else
            {
                if(y==9)
                    y=7;
                else
                    y=y+2;
            }

            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD1);
            msg.arg1=((x*10)+y);
            mHandler.sendMessageDelayed(msg,1000);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    moves.add("Player 1 at position "+"("+x+","+y+")"+" CLOSE GUESS");
                    listAdapter.notifyDataSetChanged();
                }
            } ,1000) ;
        }
        void handle_Disaster()
        {
            if(mode==2)
            {
                while(isW1Turn==false) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
           //Select above left diagonal
            if(x==0)
                x=8;
            else
            {
                if(x==1)
                    x=9;
                else
                    x=x-2;
            }
            if(y==0)
                y=8;
            else
            {
                if(y==1)
                    y=9;
                else
                    y=y-2;
            }

            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD1);
            msg.arg1=((x*10)+y);
            mHandler.sendMessageDelayed(msg,1000);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    moves.add("Player 1 at position "+"("+x+","+y+")"+" DISASTER");
                    listAdapter.notifyDataSetChanged();
                }
            } ,1000) ;
        }

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {

            Log.i("workerThread1","run method");
            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD1);
            x=(int)(Math.random()*(10));
            y=(int)(Math.random()*(10));
            msg.arg1=((x*10)+y);
            mHandler.post(new Runnable() {
                public void run() {
                    moves.add("Player 1 at position "+"("+x+","+y+")");
                    listAdapter.notifyDataSetChanged();
                }
            } ) ;
            /*
            if(mode==2)
            {
                if(isW1Turn==true)
                {
                    mHandler.sendMessage(msg);
                }
            }
            else
            {

             */
                mHandler.sendMessage(msg);
            //}


            Looper.prepare();
            mHandler1=new Handler(){
                public void handleMessage(Message m)
                {
                    int what=m.what;
                    switch(what)
                    {
                        case DISASTER:
                            Log.i("WorkerThread1","Message received from UI DISASTER");
                            handle_Disaster();
                            break;
                        case SUCCESS:
                            Log.i("WorkerThread1","Message received from UI SUCCESS");
                            Looper.myLooper().quit();
                            return;
                            //break;
                        case NEAR_MISS:
                            Log.i("WorkerThread1","Message received from UI NEAR MISS");
                            handle_nearMiss1();
                            break;
                        case CLOSE_GUESS:
                            Log.i("WorkerThread1","Message received from UI CLOSE GUESS");
                            handle_closeGuess1();
                            break;
                        case COMP_MISS:
                            Log.i("WorkerThread1","Message received from UI COMPLETE MISS");
                            handleCompMiss1();
                            break;
                    }

                }
            };
            Looper.loop();
            //Log.i("WorkerThread1","After looper");

        }
    }

    public class workerThreadTwo extends Thread
    {
        int x,y;
        Handler mHandler2;
        Handler getmHandler2()
        {
            return mHandler2;
        }
        void handleDisaster2()
        {
            if(mode==2)
            {
                while(isW2Turn==false) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Choose random
            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD2);
            x=(int)(Math.random()*(10));
            y=(int)(Math.random()*(10));
            msg.arg1=((x*10)+y);
            mHandler.sendMessageDelayed(msg,1000);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    moves.add("Player 2 at position "+"("+x+","+y+")"+" DISASTER");
                    listAdapter.notifyDataSetChanged();
                }
            } ,1000) ;
        }
        void handleCompMiss2()
        {
            if(mode==2)
            {
                while(isW2Turn==false) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Choose random
            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD2);
            x=(int)(Math.random()*(10));
            y=(int)(Math.random()*(10));
            msg.arg1=((x*10)+y);
            mHandler.sendMessageDelayed(msg,1000);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    moves.add("Player 2 at position "+"("+x+","+y+")"+" COMPLETE MISS");
                    listAdapter.notifyDataSetChanged();
                }
            },1000 ) ;
        }

        void handle_nearMiss2()
        {
            if(mode==2)
            {
                while(isW2Turn==false) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Choose left below diagonal
            if(y==0)
                y=1;
            else
                y=y-1;
            if(x==0)
                x=1;
            else
                x=x-1;
            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD2);
            msg.arg1=((x*10)+y);
            mHandler.sendMessageDelayed(msg,1000);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    moves.add("Player 2 at position "+"("+x+","+y+")"+" NEAR MISS");
                    listAdapter.notifyDataSetChanged();
                }
            },1000 ) ;
        }
        void handle_closeGuess2()
        {
            if(mode==2)
            {
                while(isW2Turn==false) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Choose block below
            if(x==8)
                x=6;
            else
            {
                if(x==9)
                    x=7;
                else
                    x=x+2;
            }
            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD2);
            msg.arg1=((x*10)+y);
            mHandler.sendMessageDelayed(msg,1000);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    moves.add("Player 1 at position "+"("+x+","+y+")"+" CLOSE GUESS");
                    listAdapter.notifyDataSetChanged();
                }
            } ,1000) ;
        }
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i("workerThread2","run method");
            Message msg=mHandler.obtainMessage(GameActivity.UPDATE_FROM_THREAD2);
            x=(int)(Math.random()*(10));
            y=(int)(Math.random()*(10));
            msg.arg1=((x*10)+y);
            if(mode==2)
            {
                while(isW2Turn==false)
                {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            mHandler.post(new Runnable() {
                public void run() {
                    moves.add("Player 2 at position "+"("+x+","+y+")");
                    listAdapter.notifyDataSetChanged();
                }
            } ) ;
            mHandler.sendMessage(msg);
            Looper.prepare();
            mHandler2=new Handler(){
                public void handleMessage(Message m)
                {
                    int what=m.what;
                    switch(what)
                    {
                        case DISASTER:
                            Log.i("WorkerThread2","Message received from UI DISASTER");
                            handleDisaster2();
                            break;
                        case SUCCESS:
                            Log.i("WorkerThread2","Message received from UI SUCCESS");
                            //Toast.makeText(getApplicationContext(),"Thread 2 won!",Toast.LENGTH_SHORT).show();
                            Looper.myLooper().quit();
                            return;
                            //break;
                        case NEAR_MISS:
                            Log.i("WorkerThread2","Message received from UI NEAR MISS");
                            handle_nearMiss2();
                            break;
                        case CLOSE_GUESS:
                            Log.i("WorkerThread2","Message received from UI CLOSE GUESS");
                            handle_closeGuess2();
                            break;
                        case COMP_MISS:
                            Log.i("WorkerThread2","Message received from UI COMPLETE MISS");
                            handleCompMiss2();
                            break;
                    }

                }
            };
            Looper.loop();

        }
    }

}
