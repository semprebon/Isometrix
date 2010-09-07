package com.nimbusly.isometrix.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.test.AndroidTestCase;
import android.test.SingleLaunchActivityTestCase;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.nimbusly.isometrix.Game;
import com.nimbusly.isometrix.GameController;
import com.nimbusly.isometrix.GameLoop;
import com.nimbusly.isometrix.Isometrix;

public class TestGameLoop extends SingleLaunchActivityTestCase {

	public TestGameLoop() {
		super("com.nimbusly.isometrix", Isometrix.class);
	}
		
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGameLoopShouldRunFast() {
		Isometrix activity = (Isometrix) getActivity();
	}

}
