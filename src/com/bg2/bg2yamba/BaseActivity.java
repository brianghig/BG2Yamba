package com.bg2.bg2yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

	protected YambaApplication yamba;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		yamba = (YambaApplication) getApplication();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch( item.getItemId() ) {
			case R.id.itemPrefs:
				startActivity(new Intent(this, PrefsActivity.class));
				break;
			case R.id.itemToggleService:
				if( yamba.isServiceRunning() ) {
					stopService(new Intent(this, UpdaterService.class));
				}
				else {
					startService(new Intent(this, UpdaterService.class));
				}
				break;
			case R.id.itemPurge:
				Toast.makeText(this, R.string.comingSoon, Toast.LENGTH_LONG).show();
				break;
			case R.id.itemTimeline:
				startActivity(new Intent(this, TimelineActivity.class).addFlags(
					Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(
					Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
				));
				break;
			case R.id.itemStatus:
				startActivity( new Intent(this, StatusActivity.class).addFlags(
					Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
				));
				break;
		}
		
		return true;
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		
		// get a handle on the Toggle Service menu item
		MenuItem toggleItem = menu.findItem(R.id.itemToggleService);
		
		if( yamba.isServiceRunning() ) {
			toggleItem.setTitle( R.string.titleServiceStop );
			toggleItem.setIcon(android.R.drawable.ic_media_pause);
		}
		else {
			toggleItem.setTitle( R.string.titleServiceStart );
			toggleItem.setIcon(android.R.drawable.ic_media_play);
		}
		
		return true;
	}
	
}
