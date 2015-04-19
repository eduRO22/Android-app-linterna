package com.example.linterna;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	
	private Flash flash;
	private WakeLock wl;
	private static final String WAKE_LOCK_TAG = "Linterna";
	private NotificationManager nm;
	private static final int NOTIFICATION_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Encender el flash
		if(initFlash())
		{
			flash.on();
		}		
		
		//Cargar interfaz
		setContentView(R.layout.activity_main);
		
		//Boton a la escucha
		ToggleButton button = (ToggleButton) findViewById(R.id.button_on_off);
		button.setOnClickListener(this);
		
		//Adquirir wake lock
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
		
		if(!wl.isHeld()){
			wl.acquire();
		}
		
		//Iniciar NotificarioManager
		nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE); 
		//Creamos la notificacion
		createNotification();
	}
	
	private boolean initFlash()
	{		
		try {
			//acceder a la cammara
			flash = new Flash();
		} catch (Exception e) {
			//creamos mensaje para usuario
			Toast.makeText(this, getResources().getString(R.string.text_error), Toast.LENGTH_LONG).show();
			//cerramos la aplicación
			finish();
			
			return false;
		}
		return true;
	}
	
	@Override
	public void onClick(View v)
	{
		if(flash.isOn())
		{
			flash.off();
			destroyNotification();
		}
		else
		{
			flash.on();
			createNotification();
		}		
	}
	

	
	//Linterna
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		
		//Destruir notificación
		destroyNotification();
		//Apagar Linterna
		flash.release();
		flash = null;
		//soltar el wake lock
		wl.release();
	}


	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		if(flash == null && initFlash())
		{
			wl.acquire();
		}
	}
	protected void onStop()
	{
		super.onStop();
		
		if(flash != null && flash.isOn())
		{
			flash.release();
			flash = null;
			wl.release();
		}
	}
	
	private void createNotification()
	{		
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification n = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(getResources().getString(R.string.app_name))
			.setContentText(getResources().getString(R.string.notification_text))
			.setOngoing(true)
			.setContentIntent(pi)
			.build();
		
		nm.notify(NOTIFICATION_ID, n);
	}
	
	private void destroyNotification()
	{
		nm.cancel(NOTIFICATION_ID);
	};
	

	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
