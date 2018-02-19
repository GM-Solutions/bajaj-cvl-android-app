/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.gladminds.bajajcvl.ORCUtil;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    ArrayList<String> upc = new ArrayList<>();
    private OnUPCFound onUPCFound;

    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();

        SparseArray<TextBlock> items = detections.getDetectedItems();

        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            Log.e("receiveDetections", "" + item.getValue());
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);

            if (item.getValue().length() > 8)
                validate(item.getValue());

            mGraphicOverlay.add(graphic);
        }
        if (upc.size() > 0) {
            if (onUPCFound != null) {
                onUPCFound.onFound(upc);
            }
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }


    public boolean validate(String str) {
        boolean value = false;

        String[] ids = str.split(" ");

        for (int i = 0; i < ids.length; i++) {
            Log.e("receiveDetections", "validate : " + ids[i]);
//            if (ids[i].length() < 11 || ids[i].length() > 13) {
//                Log.e("receiveDetections", "validate  retunt size: " + ids[i]);
//                return value;
//            }
            if (ids[i].contains("-") && ids[i].endsWith("pt")) {
                Log.e("receiveDetections", "validate = true: " + ids[i]);
                String[] vu = ids[i].split("-");
                String up = vu[0];
                up = up.toUpperCase();
                if (up.length() == 8 && (!upc.contains(up))) {
                    upc.add(up);
                }
                value = true;
            }
//            if (ids[i].length() == 8 && ids[i].substring(2, 8).matches("\\d+(?:\\.\\d+)?")) {
//                Log.e("receiveDetections", "validate = true: " + ids[i]);
//                value = true;
//                if (!upc.contains(ids[i]))
//                    upc.add(ids[i]);
//            } else {
//                Log.e("receiveDetections", "validate = false: " + ids[i]);
//            }
        }

        return value;
    }

    public void SetLisener(OnUPCFound onUPCFound) {
        this.onUPCFound = onUPCFound;
    }

    public void clearUpc() {
        if (upc != null)
            upc.clear();
    }

}

