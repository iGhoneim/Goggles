package info.Goggles;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.core.Mat;
import org.opencv.core.Range;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Bitmap.createBitmap;
import static android.graphics.BitmapFactory.decodeFile;
import static android.graphics.BitmapFactory.decodeResource;
import static org.opencv.android.OpenCVLoader.OPENCV_VERSION_3_1_0;
import static org.opencv.android.OpenCVLoader.initAsync;
import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.adaptiveThreshold;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.threshold;

public class Home extends Activity implements View.OnClickListener {

    static final int mRequest = 1010;
    Button mOpen, mReset, mOrg, mGrey, mBin, mABin;
    ImageView mPicture;
    Intent mIntent;
    Bitmap mTarget;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case SUCCESS:
                    init();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }

        @Override
        public void onPackageInstall(int operation, InstallCallbackInterface callback) {
            super.onPackageInstall(operation, callback);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (initAsync(OPENCV_VERSION_3_1_0, this, mLoaderCallback)) {
        }
        setContentView(R.layout.home);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequest) {
            if (resultCode == RESULT_OK) {
                Uri mImage = data.getData();
                String[] mPaths = {MediaStore.Images.Media.DATA};
                Cursor mCursor = getContentResolver().query(mImage, mPaths, null, null, null);
                mCursor.moveToFirst();
                int mIndex = mCursor.getColumnIndex(mPaths[0]);
                String mPath = mCursor.getString(mIndex);
                mCursor.close();
                mTarget = decodeFile(mPath);
                mPicture.setImageBitmap(mTarget);
            }
        }
    }

    private void init() {
        setContentView(R.layout.home);
        layout();
        showOf((originTarget()));
    }

    private void layout() {
        mIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mPicture = (ImageView) findViewById(R.id.picture);

        mGrey = (Button) findViewById(R.id.grey);
        mGrey.setOnClickListener(this);
        mBin = (Button) findViewById(R.id.bin);
        mBin.setOnClickListener(this);
        mABin = (Button) findViewById(R.id.abin);
        mABin.setOnClickListener(this);
        mReset = (Button) findViewById(R.id.reset);
        mReset.setOnClickListener(this);
        mOpen = (Button) findViewById(R.id.open);
        mOpen.setOnClickListener(this);
        mOrg = (Button) findViewById(R.id.org);
        mOrg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                showOf(bmpOf(matOf(getTarget())));
                break;
            case R.id.open:
                startActivityForResult(mIntent, mRequest);
                break;
            case R.id.grey:
                showOf(bmpOf(greyOf(matOf(getTarget()))));
                break;
            case R.id.bin:
                showOf(bmpOf(binOf(greyOf(matOf(getTarget())))));
                break;
            case R.id.abin:
                showOf(bmpOf(abinOf(greyOf(matOf(getTarget())))));
                break;
            case R.id.org:
                showOf(originTarget());
                break;
            default:
                break;
        }
    }

    private Bitmap originTarget() {
        //function by Ghoneim to get original image
        mTarget = decodeResource(getResources(), R.drawable.sign);
        return mTarget;
    }

    private Bitmap getTarget() {
        //function by Ghoneim to get current image
        return mTarget;
    }

    private void showOf(Bitmap bmp) {
         //function by Ghoneim to show image
        mPicture.setImageBitmap(bmp);
    }

    private Mat matOf(Bitmap bmp) {
         //function by Ghoneim to convert image to matrix
        Mat mat = new Mat(bmp.getHeight(), bmp.getWidth(), CV_8U);
        bitmapToMat(bmp, mat);
        return mat;
    }

    private Bitmap bmpOf(Mat mat) {
             //function by Ghoneim to convert matrix to image
        Bitmap bmp = createBitmap(mat.cols(), mat.rows(), ARGB_8888);
        matToBitmap(mat, bmp);
        return bmp;
    }

    private Mat copyOf(Mat src) {
        //function by Ghoneim to clone image matrix & properties to another one
        Mat dst = new Mat(src, Range.all());
        return dst;
    }

    private Mat greyOf(Mat src) {
         //function by Ghoneim to apply grey scale conversion
        Mat dst = copyOf(src);
        cvtColor(src, dst, COLOR_RGB2GRAY);
        return dst;
    }

    private Mat binOf(Mat src) { 
        //function by Ghoneim to apply binarization using threshold
        Mat dst = copyOf(src);
        threshold(src, dst, 127, 255, THRESH_BINARY);
        return dst;
    }

    private Mat abinOf(Mat src) {
        //function by Ghoneim to apply adaptive binarization using threshold
        Mat dst = copyOf(src);
        adaptiveThreshold(src, dst, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 7, 5);
        return dst;
    }
    privete Nasr()
    {

    }
}
