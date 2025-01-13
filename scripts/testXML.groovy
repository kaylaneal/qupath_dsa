// Testing XML Input/Output
import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.regions.ImagePlane
import qupath.lib.objects.PathObjects
import qupath.lib.roi.ROIs

import com.google.gson.Gson

// Script Arguments:                                [modelFile, AnnotationOutputPath, OtherOutputPath, ROI]
println "$args"

def modelFile = args[0]                                
def annotPath = args[1]
def metaPath = args[2]
def roiCoords = args[3]                                // if == 0 then use whole image


// Load Image:
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Run analysis on ROI if defined:
if (roiCoords != "0"){
    double left = roiCoords.tokenize(',')[0] as double
    double top = roiCoords.tokenize(',')[1] as double
    double w = roiCoords.tokenize(',')[2] as double
    double h = roiCoords.tokenize(',')[3] as double

    println  "ROI Coords: [$left, $top, $w, $h]"

    def roi = ROIs.createRectangleROI(left, top, w, h, ImagePlane.getPlane(0, 0))
    def roiAnnot = PathObjects.createAnnotationObject(roi)

    addObject(roiAnnot)
    selectObjects(roiAnnot)
}

// Save Image Measurements
Gson gson = new Gson()
File txtfile = new File(metaPath)
txtfile.write( gson.toJson(server.getMetadata()).toString() )


// Export Annotations
exportObjectsToGeoJson(getAnnotationObjects(), annotPath, "FEATURE_COLLECTION")
