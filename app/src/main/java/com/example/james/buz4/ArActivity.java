package com.example.james.buz4;

/**
 * Created by Taehyun Kim on 6/09/2016.
 */
/*
public class ArActivity extends FragmentActivity implements SurfaceHolder.Callback {

    private final String TAG = "ArActivity";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera mCamera;
    boolean inPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        inPreview = false;
        mSurfaceView = (SurfaceView)findViewById(R.id.mSurfaceView);
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int width, int height) {
        //Camera.Parameters parameters = camera.getParameters();
        Policy.Parameters parameters = camera.getParameters();
        Camera.Size size =getBestPreviewSize(width, height, parameters);
        if (size!=null) {
            parameters.setPreviewSize(size.width, size.height);
            camera.setParameters(parameters);
            camera.startPreview();
            inPreview=true;
        }
    }
    //return the preview size
    private Camera.Size getBestPreviewSize(int width,int height,Camera.Parameters parameters){
        Camera.Size result = null;
        for(Camera.Size size : parameters.getSupportedPreviewSizes()){
            if(size.width<=width && size.height<=height){
                if(result == null){
                    result = size;
                }
                int resultArea = result.width*result.height;
                int newArea = size.width*size.height;
                if (newArea>resultArea) {
                    result=size;
                }
            }

        }
        return result;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(previewHolder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onResume(){
        super.onResume();
        camera = Camera.open();

    }
    @Override
    public void onPause(){

        if(inPreview){
            camera.stopPreview();
        }

        camera.release();
        camera = null;
        inPreview = false;
        super.onPause();
    }

}
*/