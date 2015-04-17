package com.hackforgood.myapplication;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity
{
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openButton = (Button)findViewById(R.id.open);
        Button sendButton = (Button)findViewById(R.id.send);
        Button closeButton = (Button)findViewById(R.id.close);
        myLabel = (TextView)findViewById(R.id.label);
        myTextbox = (EditText)findViewById(R.id.entry);

        //Open Button
        openButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    findBT();
                    openBT();
                }
                catch (IOException ex) { }
            }
        });

        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    sendData();
                }
                catch (IOException ex) { }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    closeBT();
                }
                catch (IOException ex) { }
            }
        });
    }

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("Polar iWL"))
                {
                    mmDevice = device;
                    break;
                }
                System.out.println(device.getName()+" "+device.getAddress());
            }
        }
        myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException
    {
//        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("00:22:D0:01:5E:BD");
//        BluetoothSocket tmp = null;
//        BluetoothSocket mmSocket = null;
//
//        // Get a BluetoothSocket for a connection with the
//        // given BluetoothDevice
//        try {
//            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//            Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
//            tmp = (BluetoothSocket) m.invoke(device, 1);
//        } catch (IOException e) {
//            Log.e(TAG, "create() failed", e);
//        }
//        mmSocket = tmp;


        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
//        final byte delimiter = (byte)0xfe; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                final PolarMessageParser messageParser = new PolarMessageParser();
                final byte[] buffer = new byte[messageParser.getFrameSize()];
                int bytes; // bytes read
                int offset = 0;
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try {
                        // Read from the inputStream
                        bytes = mmInputStream.read(buffer, offset, messageParser.getFrameSize() - offset);

                        if (bytes == -1) { throw new IOException("EOF reached."); }

                        offset += bytes;

                        if (offset != messageParser.getFrameSize()) {
                            // Partial frame received. Call read again to receive the rest.
                            continue;
                        }

                        if (!messageParser.isValid(buffer)) {
                            int index = messageParser.findNextAlignment(buffer);
                            if (index == -1) {
                                System.out.println("Could not find any valid data. Drop data.");
                                offset = 0;
                                continue;
                            }
                            System.out.println("Misaligned data. Found new message at " + index + ". Recovering...");
                            offset = messageParser.getFrameSize() - index;
                            System.arraycopy(buffer, index, buffer, 0, offset);
                            continue;
                        }
                        else{
//                            myLabel.setText(messageParser.parseBuffer(buffer)+ " bpm");
                            handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            myLabel.setText( (messageParser.parseBuffer(buffer)-30)+ " bpm");
                                            //System.err.println("string: "+data);
                                        }
                                    });

                            System.err.println("///////////////////////////////////");
                            System.err.println(messageParser.parseBuffer(buffer)+ " bpm");
                            System.err.println("///////////////////////////////////");

                            System.out.println("///////////////////////////////////");
                            System.out.println(messageParser.parseBuffer(buffer)+ " bpm");
                            System.out.println("///////////////////////////////////");

                            Log.e("myactivity", messageParser.parseBuffer(buffer)+ " bpm");
                        }

                        offset = 0;

                        // Send a copy of the obtained bytes to the handler to avoid memory
                        // inconsistency issues
                        //handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer.clone()).sendToTarget();
                    } catch (IOException e) {
                        System.out.println("Bluetooth connection lost.");
                        //setState(Sensor.SensorState.DISCONNECTED);
                        break;
                    }


//                    try
//                    {
//                        int bytesAvailable = mmInputStream.available();
//                        if(bytesAvailable > 0)
//                        {
//                            byte[] packetBytes = new byte[bytesAvailable];
//                            mmInputStream.read(packetBytes);
//                            for(int i=0;i<bytesAvailable;i++)
//                            {
//                                byte b = packetBytes[i];
//                                if(b == delimiter)
//                                {
//                                    byte[] encodedBytes = new byte[readBufferPosition];
//                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
//                                    final String data = new String(encodedBytes, "US-ASCII");
//                                    //final String data = Hex.encodeHexString(encodedBytes);
//                                    //byte [] subBytes = new byte[3];
//                                    //for(int j = 2; j >= 0; j--){
//                                    //    subBytes[2-j] = encodedBytes[encodedBytes.length - j];
//                                    //}
//
//                                    //Integer hr = new Integer(data);
//                                    byte [] subBytes = Arrays.copyOfRange(encodedBytes, 6, 8);
//                                    readBufferPosition = 0;
//                                    //System.out.println("int: "+hr);
//
//                                    handler.post(new Runnable()
//                                    {
//                                        public void run()
//                                        {
//                                            myLabel.setText(data);
//                                            System.err.println("string: "+data);
//                                        }
//                                    });
//                                }
//                                else
//                                {
//                                    readBuffer[readBufferPosition++] = b;
//                                }
//                            }
//                        }
//                    }
//                    catch (IOException ex)
//                    {
//                        stopWorker = true;
//                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException
    {
        String msg = myTextbox.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        myLabel.setText("Data Sent");
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }
}
