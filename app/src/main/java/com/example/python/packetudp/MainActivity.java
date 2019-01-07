package com.example.python.packetudp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText editTextAddress, editTextPort, editTextMsg;
    Button buttonConnect,buttonStop;
    TextView textViewState, textViewRx;

    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextAddress = findViewById(R.id.address);
        editTextPort = findViewById(R.id.port);
        editTextMsg = findViewById(R.id.msg);
        buttonConnect = findViewById(R.id.connect);
        buttonStop = findViewById(R.id.stop);
        textViewState = findViewById(R.id.state);
        textViewRx = findViewById(R.id.received);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonStop.setOnClickListener(buttonStopOnClickListener);

        udpClientHandler = new UdpClientHandler(this);

    }

    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {

            udpClientThread = new UdpClientThread(
                    editTextAddress.getText().toString(),
                    Integer.parseInt(editTextPort.getText().toString()),
                    udpClientHandler, editTextMsg.getText().toString());
            udpClientThread.start();

            buttonConnect.setEnabled(false);
        }
    };

    View.OnClickListener buttonStopOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            udpClientThread = new UdpClientThread(
                    editTextAddress.getText().toString(),
                    Integer.parseInt(editTextPort.getText().toString()),
                    udpClientHandler,editTextMsg.getText().toString());
            udpClientThread.interrupt();
            textViewState.setText("Stop Send Msg");
            buttonConnect.setEnabled(true);
        }
    };

    private void updateState(String state){
        textViewState.setText(state);
    }

    private void updateRxMsg(String rxmsg){
        textViewRx.append(rxmsg + "\n");
    }

    private void clientEnd(){
        udpClientThread = null;
        textViewState.setText("clientEnd()");
        buttonConnect.setEnabled(true);

    }

    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private MainActivity parent;

        public UdpClientHandler(MainActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_STATE:
                    parent.updateState((String)msg.obj);
                    break;
                case UPDATE_MSG:
                    parent.updateRxMsg((String)msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }
}

