import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.roi.ROIs
import qupath.lib.objects.PathObjects
import qupath.lib.regions.ImagePlane

import com.google.gson.Gson

// Script Arguments:
println "$args"

def modelFile = args[0]

def annotationPath = args[1]

def metadataPath = args[2]
Gson gson = new Gson()
File txtfile = new File(metadataPath)

// Only Run on a ROI. Exit if no ROI defined.
def roiCoords = args[3]
if(roiCoords == "0"){
    txtfile.write("Cell Detection Failure. Must specify ROI.")
    exportAllObjectsToGeoJson(annotationPath, "FEATURE_COLLECTION")
    System.exit(1)
}
else {
    double left = roiCoords.tokenize(',')[0] as double
    double top = roiCoords.tokenize(',')[1] as double
    double w = roiCoords.tokenize(',')[2] as double
    double h = roiCoords.tokenize(',')[3] as double

    println  "ROI Coords: [$left, $top, $w, $h]"

    def roi = ROIs.createRectangleROI(left, top, w, h, ImagePlane.getPlane(0, 0))
    def annot = PathObjects.createAnnotationObject(roi)

    addObject(annot)
    selectObjects(annot)
}

// Current Image:
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Run Cell Detection:
detection_model = new File(modelFile).text
println "Cell Detection Model Loaded: $detection_model"
println "Running Cell Detection"
runPlugin( 'qupath.imagej.detect.cells.WatershedCellDetection', detection_model )

// Export Annotations
exportAllObjectsToGeoJson(annotationPath, "FEATURE_COLLECTION")