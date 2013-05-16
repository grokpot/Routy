package org.routy.sound;

import org.routy.R;
import org.routy.log.Log;
import org.routy.model.PreferencesModel;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class SoundPlayer {

	private static final String TAG = "SoundPlayer";
	private static SoundPool soundPool = null;
	private static AudioManager audioManager;
	private static float volume;
	
	private static Integer speak = 0;
	private static Integer click = 0;
	private static Integer bad = 0;
	
	private SoundPlayer() {
		super();
	}
	
	private static SoundPool initSoundPool(Context context, OnLoadCompleteListener listener) {
		Log.v(TAG, "initializing sound player's sound pool");
		if (audioManager == null) {
			audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		}
		
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		speak = soundPool.load(context, R.raw.routyspeak, 1);  
		bad = soundPool.load(context, R.raw.routybad, 1);
		click = soundPool.load(context, R.raw.routyclick, 1);
		if (listener != null) {
			soundPool.setOnLoadCompleteListener(listener);
		}
		
		return soundPool;
	}
	
	public static void playSpeak(Context context) {
		Log.v(TAG, "playing 'Routy'");
		playSound(context, RoutySound.SPEAK);
	}
	
	public static void playClick(Context context) {
		playSound(context, RoutySound.CLICK);
	}
	
	public static void playBad(Context context) {
		playSound(context, RoutySound.BAD);
	}
	
	private static void playSound(Context context, final RoutySound sound) {
		if (PreferencesModel.getSingleton().isSoundsOn()) {
			if (soundPool == null) {
				initSoundPool(context, new OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
						if (sampleId == getSoundId(sound)) {
							normalizeVolumeAndPlay(sampleId);
						}
					}
				});
			} else {
				normalizeVolumeAndPlay(getSoundId(sound));
			}
		}
	}
	
	private static int getSoundId(RoutySound sound) {
		switch (sound) {
		case BAD:
			return bad;
		case CLICK:
			return click;
		case SPEAK:
			return speak;
		default:
			return -1;
		}
		
	}
	
	private static void normalizeVolumeAndPlay(int soundId) {
		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
	    volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
	    soundPool.play(soundId, volume, volume, 1, 0, 1);
	}
	
	/**
	 * Releases the sound resources associated with the underlying {@link SoundPool}.
	 */
	public static void done() {
		Log.v(TAG, "releasing sound player");
		if (soundPool != null) {
			soundPool.release();
			soundPool = null;
		}
	}
	
	private enum RoutySound {
		SPEAK, CLICK, BAD;
	}
}
