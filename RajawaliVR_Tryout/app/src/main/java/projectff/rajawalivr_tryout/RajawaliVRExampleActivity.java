package projectff.rajawalivr_tryout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
 
import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

import com.example.inputmanagercompat.InputManagerCompat;
import com.example.inputmanagercompat.InputManagerCompat.InputDeviceListener;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

public class RajawaliVRExampleActivity extends Activity implements InputDeviceListener {
 
	RajawaliVRExampleRenderer renderer;
    public RajawaliSurfaceView rajawaliSurface;
	private HeadTracker mHeadTracker;
	private InputManagerCompat mInputManager;
	private InputDevice dev;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        mInputManager = InputManagerCompat.Factory.getInputManager(this);
		mInputManager.registerInputDeviceListener(this, null);
        rajawaliSurface = (RajawaliSurfaceView) findViewById(R.id.rajawali_surface);
        rajawaliSurface.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE 
	            );
        renderer = new RajawaliVRExampleRenderer(this);
        mHeadTracker = new HeadTracker(this);
        //renderer.setHeadTracker(mHeadTracker);
        rajawaliSurface.setSurfaceRenderer(renderer);
    }

    @Override
	public void onResume() {
		super.onResume();
		mHeadTracker.startTracking();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mHeadTracker.stopTracking();
	}
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			this.finish();
		}
		
		//renderer.onKeyDown(keyCode, event);
		return true;
	};
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//renderer.onKeyUp(keyCode, event);
		return true;
	};
	
	@Override
	public void onInputDeviceAdded(int deviceId) {
		// TODO Auto-generated method stub
		dev = InputDevice.getDevice(deviceId);
		Log.d("deviceName", dev.getName());
	}

	@Override
	public void onInputDeviceChanged(int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInputDeviceRemoved(int deviceId) {
		// TODO Auto-generated method stub
		
	}
}