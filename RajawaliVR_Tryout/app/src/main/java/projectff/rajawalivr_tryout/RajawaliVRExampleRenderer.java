package projectff.rajawalivr_tryout;

import java.io.File;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.microedition.khronos.opengles.GL10;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.Object3D;
import org.rajawali3d.animation.mesh.SkeletalAnimationObject3D;
import org.rajawali3d.animation.mesh.SkeletalAnimationSequence;
import org.rajawali3d.bounds.IBoundingVolume;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AlphaMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.loader.ALoader;
import org.rajawali3d.loader.Loader3DSMax;
import org.rajawali3d.loader.LoaderAWD;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.loader.async.IAsyncLoaderCallback;
import org.rajawali3d.loader.md5.LoaderMD5Anim;
import org.rajawali3d.loader.md5.LoaderMD5Mesh;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.surface.IRajawaliSurfaceRenderer;
import org.rajawali3d.terrain.SquareTerrain;
import org.rajawali3d.util.MeshExporter;
import org.rajawali3d.util.exporter.AExporter;
import org.rajawali3d.util.exporter.AwdExporter;

import org.rajawali3d.loader.AMeshLoader;

import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.rajawali3d.renderer.RajawaliRenderer;

public class RajawaliVRExampleRenderer extends RajawaliRenderer implements IRajawaliSurfaceRenderer {
        //, IAsyncLoaderCallback {
	float count = 0;
	Quaternion mPlayerOrientation = new Quaternion();
	Quaternion q = new Quaternion();
	Object3D room;
	ArrayList<String> objects = new ArrayList<String>();
	Resources res;
	String tr, tl;
	SkeletalAnimationObject3D player;
	Object3D playerbox;
	boolean intersected = false;
	boolean loaded = false;
	Object3D[] colliders = new Object3D[20];
	IBoundingVolume bbox, bbox2;
	Vector3 oldpos = new Vector3(0,0,0);
	private AlphaMapTexture mTextTexture;
	private Bitmap mTextBitmap;
	private Canvas mTextCanvas;
	private Paint mTextPaint;
	private boolean mShouldUpdateTexture;
	private boolean bulletIntersected;
	private String Score = "";
	private Material bullet_mat = new Material();
	private Object3D bullet;
	
	
	public RajawaliVRExampleRenderer(Context context) {
		super(context);
		res = context.getResources();
	}
	
	//public void setHeadTracker(HeadTracker headTracker){
	//	super.setHeadTracker(headTracker);
	//}
	
	@Override
	public void initScene() {
		
		DirectionalLight light = new DirectionalLight(0.2f, -1f, 0f);
		light.setPower(.7f);
		getCurrentScene().addLight(light);
		
		light = new DirectionalLight(0.2f, 1f, 0f);
		light.setPower(1f);
		getCurrentScene().addLight(light);
		
		getCurrentCamera().setFarPlane(10000);
	    //getCurrentCamera().setFieldOfView(120);
		
		getCurrentScene().setBackgroundColor(0xdddddd);
		
		try {
			
			getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx, R.drawable.posy, R.drawable.negy, R.drawable.posz, R.drawable.negz, 10000);
//			objects.add("SkyBox");
//
//			player = showMonster("player", new Vector3( 0f, -1.5f, -1.5), new Vector3(0,180,0), new Vector3(.5f));
//		    player.setRotY(180);
//		    player.setFps(15);
//			loadAnim2Obj(player,"player_idle", false);
//
//			Material textMat = new Material();
//			mTextBitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
//			mTextTexture = new AlphaMapTexture("timeTexture", mTextBitmap);
//			textMat.setColorInfluence(1);
//			textMat.addTexture(mTextTexture);
//
//    		playerbox = new Plane(2,1,1,1);
//			playerbox.setMaterial(textMat);
//			playerbox.setTransparent(true);
//			playerbox.setDoubleSided(true);
//			playerbox.setVisible(true);
//			playerbox.setPosition(0, 1,5);
//			getCurrentScene().addChild(playerbox);
//
//
//			//Loader3DSMax loader = new Loader3DSMax(this, R.raw.room2);
////		    LoaderAWD loader = new LoaderAWD(mContext.getResources(), mTextureManager, R.raw.model2);
////			LoaderOBJ loader = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.labobj);
////			loadModel(loader, this, R.raw.model2);

			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	//	super.initScene();
	}
	
	public SkeletalAnimationObject3D showMonster(String Monster, Vector3 pos, Vector3 rot, Vector3 scale, int fps){
		
	    SkeletalAnimationObject3D mObject = new SkeletalAnimationObject3D();
	    		
		int mesh = getContext().getResources().getIdentifier(Monster + "_mesh", "raw", "rajawali.vuforia.vr");
		//scale = scale.multiply(0.01f); 
		
		try {
			LoaderMD5Mesh meshParser = new LoaderMD5Mesh(this,
					mesh);
			meshParser.parse();

			mObject = (SkeletalAnimationObject3D) meshParser
					.getParsedAnimationObject();
			
			mObject.setPosition(pos);
			mObject.setRotation(rot);
			mObject.setScale(scale);
			mObject.setFps(fps);
			mObject.setTransparent(false);
			getCurrentScene().addChild(mObject);
			
			return mObject;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mObject;
	}
	
	public void loadAnim2Obj(SkeletalAnimationObject3D obj, String animname, boolean loop){
		try{
		
		int anim = getContext().getResources().getIdentifier(animname, "raw", "projectff.rajawalivr_tryout");

		LoaderMD5Anim animParser = new LoaderMD5Anim(animname, this,
													 anim);
		animParser.parse();

		SkeletalAnimationSequence sequence = (SkeletalAnimationSequence) animParser
			.getParsedAnimationSequence();
		
		obj.setAnimationSequence(sequence);
		obj.play(loop);
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	
			
	}
	
	public SkeletalAnimationObject3D showMonster(String Monster, Vector3 pos, Vector3 rot, Vector3 scale){
		return showMonster(Monster,pos,rot,scale , 8);
	}
	
	public SkeletalAnimationObject3D showMonster(String Monster, Vector3 pos, Vector3 rot){
		return showMonster(Monster,pos,rot,new Vector3(0.0001f), 8);
	}
	
	public void updateTimeBitmap() {
		new Thread(new Runnable() {
			public void run() {
				if (mTextCanvas == null) {

					mTextCanvas = new Canvas(mTextBitmap);
					mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
					mTextPaint.setColor(Color.WHITE);
					mTextPaint.setTextSize(35);
				}
				//
				// -- Clear the canvas, transparent
				//
				mTextCanvas.drawColor(0, Mode.CLEAR);
				//
				// -- Draw the time on the canvas
				//
				mTextCanvas.drawText(Score, 75,
						128, mTextPaint);
				//
				// -- Indicates that the texture should be updated on the OpenGL thread.
				//
				mShouldUpdateTexture = true;
			}
		}).start();
	}
	
	public ATexture textAsBitmap() {
		mTextBitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		mTextTexture = new AlphaMapTexture("timeTexture", mTextBitmap);
		return mTextTexture;
    }
	
	private static boolean isFireKey(int keyCode) {
        return KeyEvent.isGamepadButton(keyCode)
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_SPACE;
    }	
	
	public boolean inList(String search, ArrayList<String> myList){
		try{
			for(String str: myList) {
				Log.d("OBJECT", search);
				if (str.contains(search))
					return true;
				}
		}
		catch(Exception e){
			Log.d("Exception", e.toString());
		}
		return false;
	}
	
	public void createCube(Object3D obj){
		bullet_mat.setColorInfluence(1);
		bullet_mat.setColor(0);
		
		bullet = new Cube(.3f);
		bullet.setMaterial(bullet_mat);
		bullet.setDoubleSided(true);
		
		bullet.setPosition(obj.getPosition());
		bullet.setOrientation(obj.getOrientation());
		
		getCurrentScene().addChild(bullet);
		
	}
	
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	    float speed = .5f;
// 		int deviceId = event.getDeviceId();
//	        boolean handled = false;
// 		if (deviceId != -1) {
// 				oldpos = player.getPosition();
//	        	Log.d("KEYCODE", Integer.toString(event.getKeyCode()));
//	        	 switch (keyCode) {
//	                case KeyEvent.KEYCODE_DPAD_LEFT:
//	                	if (!loaded){
//	                		loadAnim2Obj(player,"player_sidestep", false); loaded = true;
//	                		}
//	                		super.mCameraLeft.setPosition(super.mCameraLeft.getPosition().x-speed,super.mCameraLeft.getPosition().y,super.mCameraLeft.getPosition().z);
//	                		super.mCameraRight.setPosition(super.mCameraRight.getPosition().x-speed,super.mCameraRight.getPosition().y,super.mCameraRight.getPosition().z);
//            				player.setPosition(player.getPosition().x-speed,player.getPosition().y,player.getPosition().z);
//	                	handled = true;
//	                    break;
//	                case KeyEvent.KEYCODE_DPAD_RIGHT:
//	                	if (!loaded){
//	                		loadAnim2Obj(player,"player_sidestep", false); loaded = true;
//	                		}
//                			player.setPosition(player.getPosition().x+speed,player.getPosition().y,player.getPosition().z);
//	                		super.mCameraLeft.setPosition(super.mCameraLeft.getPosition().x+speed,super.mCameraLeft.getPosition().y,super.mCameraLeft.getPosition().z);
//	                		super.mCameraRight.setPosition(super.mCameraRight.getPosition().x+speed,super.mCameraRight.getPosition().y,super.mCameraRight.getPosition().z);
//                		handled = true;
//	                    break;
//	                case KeyEvent.KEYCODE_DPAD_UP:
//	                	if (!loaded){
//	                		loadAnim2Obj(player,"player_run", false); loaded = true;
//	                		}
//                			player.setPosition(player.getPosition().x,player.getPosition().y,player.getPosition().z-speed);
//                			super.mCameraLeft.setPosition(super.mCameraLeft.getPosition().x,super.mCameraLeft.getPosition().y,super.mCameraLeft.getPosition().z-speed);
//                			super.mCameraRight.setPosition(super.mCameraRight.getPosition().x,super.mCameraRight.getPosition().y,super.mCameraRight.getPosition().z-speed);
//                			handled = true;
//	                    break;
//	                case KeyEvent.KEYCODE_DPAD_DOWN:
//	                	if (!loaded){
//	                		loadAnim2Obj(player,"player_walk", false); loaded = true;player.setFps(15);
//	                		}
//	                		super.mCameraLeft.setPosition(super.mCameraLeft.getPosition().x,super.mCameraLeft.getPosition().y,super.mCameraLeft.getPosition().z+speed);
//	                		super.mCameraRight.setPosition(super.mCameraRight.getPosition().x,super.mCameraRight.getPosition().y,super.mCameraRight.getPosition().z+speed);
//                    		player.setPosition(player.getPosition().x,player.getPosition().y,player.getPosition().z+speed);
//	                	handled = true;
//	                    break;
//	                case 96:
//	                	if (!loaded){
//	                		loadAnim2Obj(player,"player_shoot", false); loaded = true;
//	                			//createCube(player);
//	                		}
//	                	handled = true;
//	                    break;
//	                case 97:
//	                	if (!loaded){
//	                		loadAnim2Obj(player,"player_shoot", false); loaded = true;
//	                		}
//	                	handled = true;
//	                    break;
//	                default:
//	                    if (isFireKey(keyCode)) {
//	                    	handled = true;
//	                    }
//	                    loadAnim2Obj(player,"player_idle", false);
//            			break;
//	            }
//	        }
//	       return handled;
// }
//
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//	        int deviceId = event.getDeviceId();
//	        if (deviceId != -1) {
//	        	loadAnim2Obj(player,"player_idle", false); loaded = false;
//	        }
//	        return true;
//    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    public void onDrawFrame(GL10 glUnused) {
		super.onRenderFrame(glUnused);
		count+=0.1f;
	    //super.mCameraLeft.setPosition(player.getPosition().x,player.getPosition().y+2,player.getPosition().z+5);
		//super.mCameraRight.setPosition(player.getPosition().x,player.getPosition().y+2,player.getPosition().z+5);
		
//		if (mFrameCount++ >= mFrameRate) {
//            mFrameCount = 0;
//            Score = "X: " +  Double.toString(player.getX()) + " Y: " + Double.toString(player.getY()) + " Z: " + Double.toString(player.getZ()); 
//            updateTimeBitmap();
//        }
        //
        // -- update the texture because it is ready
        //
//        if (mShouldUpdateTexture) {
//            mTextTexture.setBitmap(mTextBitmap);
//            mTextureManager.replaceTexture(mTextTexture);
//            mShouldUpdateTexture = false;
//        }
        //playerbox.setPosition(getCurrentCamera().getX(),getCurrentCamera().getY(),getCurrentCamera().getZ()+5);
//		player.setOrientation(getCurrentCamera().getOrientation());
		
//		if (!bulletIntersected && bullet != null){
//			
//			
//			
//		}
		
//			if (colliders.length != 0){
//				for (Object3D collider : colliders){
//					Log.d("colliders", collider.getName());
//					bbox = collider.getGeometry().getBoundingBox();
//					bbox.transform(collider.getModelMatrix());
//					bbox2 = playerbox.getGeometry().getBoundingBox();
//					bbox2.transform(playerbox.getModelMatrix());
//		
//					intersected = bbox.intersectsWith(bbox2);
//				
//					if (intersected){
//						Log.d("intersected", "intersected");
//					
//					}
//				}
//			}
		}

//	@Override
	public void onModelLoadComplete(ALoader loader) {
		// TODO Auto-generated method stub
		
		Material roommat = new Material();
		roommat.setDiffuseMethod(new DiffuseMethod.Lambert());
		roommat.setColorInfluence(0);
		roommat.enableLighting(true);
		try{
			roommat.addTexture(new Texture("roommat", R.drawable.level));
		}catch(Exception e){
			
		}
		
		final LoaderOBJ obj = (LoaderOBJ) loader;
		
		room = obj.getParsedObject();
		
//		for (int i = 0; i < room.getNumChildren(); i++){
//			String Name = room.getChildAt(i).getName();
//			if(Name.startsWith("collider")){ 
//				colliders[i]  = room.getChildAt(i).clone();
//				colliders[i].setVisible(false);
//			}
//		}
//		
		room.setMaterial(roommat);
		room.setDoubleSided(true);
		room.setScale(1.03,1.03,1.03);
		room.setPosition(-0,-0,-0);
		room.setShowBoundingVolume(true);
		getCurrentScene().addChild(room);
		
	}



	// @Override
	public void onModelLoadFailed(ALoader loader) {
		// TODO Auto-generated method stub
		
	}
	}

