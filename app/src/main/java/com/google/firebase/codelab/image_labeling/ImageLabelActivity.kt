package com.google.firebase.codelab.image_labeling

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.BitmapFactory
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
//import sun.jvm.hotspot.utilities.IntArray
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream


class ImageLabelActivity : BaseCameraActivity() {

    private val itemAdapter: ImageLabelAdapter by lazy {
        ImageLabelAdapter(listOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rvLabel.layoutManager = LinearLayoutManager(this)
        rvLabel.adapter = itemAdapter
    }

    private fun runImageLabeling(bitmap: Bitmap) {
        //Create a FirebaseVisionImage
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        //Get access to an instance of FirebaseImageDetector
        val detector = FirebaseVision.getInstance().visionLabelDetector

        //Use the detector to detect the labels inside the image
        detector.detectInImage(image)
                .addOnSuccessListener {
                    // Task completed successfully
                    progressBar.visibility = View.GONE
                    itemAdapter.setList(it)
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    progressBar.visibility = View.GONE
                    Toast.makeText(baseContext, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
                }
    }

    /* This function is for running the model on cloud */
    private fun runCloudImageLabeling(bitmap: Bitmap) {
        //Create a FirebaseVisionImage
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        //Get access to an instance of FirebaseCloudImageDetector
        val detector = FirebaseVision.getInstance().visionCloudLabelDetector

        //Use the detector to detect the labels inside the image
        detector.detectInImage(image)
                .addOnSuccessListener {
                    // Task completed successfully
                    progressBar.visibility = View.GONE
                    itemAdapter.setList(it)
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    progressBar.visibility = View.GONE
                    Toast.makeText(baseContext, "Sorry2, something went wrong!", Toast.LENGTH_SHORT).show()
                }
    }

    override fun onClick(v: View?) {
        progressBar.visibility = View.VISIBLE
        cameraView.captureImage {cameraKitImage ->
            // Get the Bitmap from the captured shot

            // reduce bitmap size- webcam takes very high quality pic on emulator.
            val original = cameraKitImage.bitmap
//            val org_size = original.byteCount
            val out = ByteArrayOutputStream()
            original.compress(Bitmap.CompressFormat.JPEG, 15, out)

//            val reduced_bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
//            val size_red = reduced_bitmap.byteCount
            runImageLabeling(original)
            runOnUiThread {
//                Toast.makeText(baseContext, "red size and org size $size_red and $org_size", Toast.LENGTH_LONG).show()
                showPreview()
                imagePreview.setImageBitmap(original) // preview in emulator for very high size is problematic.
            }
        }
    }

}
